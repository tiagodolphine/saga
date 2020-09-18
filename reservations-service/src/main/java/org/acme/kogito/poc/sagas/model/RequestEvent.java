package org.acme.kogito.poc.sagas.model;

import java.util.Comparator;
import java.util.Map;

import org.acme.kogito.poc.sagas.model.car.CarReservation;
import org.acme.kogito.poc.sagas.model.flight.FlightReservation;
import org.acme.kogito.poc.sagas.model.hotel.HotelReservation;
import org.acme.kogito.poc.sagas.model.orders.OrderPayment;
import org.acme.kogito.poc.sagas.model.orders.Shipping;
import org.acme.kogito.poc.sagas.model.orders.Stock;
import org.acme.kogito.poc.sagas.model.payment.Payment;

public class RequestEvent {

    private static final Map<Class<? extends Reservation>, String> RESOURCES = Map.of(
            CarReservation.class, CarReservation.RESOURCE_NAME,
            HotelReservation.class, HotelReservation.RESOURCE_NAME,
            FlightReservation.class, FlightReservation.RESOURCE_NAME,
            Payment.class, Payment.RESOURCE_NAME,
            //Orders services
            Stock.class, Stock.RESOURCE_NAME,
            Shipping.class, Shipping.RESOURCE_NAME,
            OrderPayment.class, OrderPayment.RESOURCE_NAME);

    private static final String CANCEL_PREFIX = "Cancel";

    private String resource;
    private Class<? extends Reservation> reservation;
    private boolean cancellation;

    private RequestEvent(Class reservation, boolean cancellation) {
        this.reservation = reservation;
        this.cancellation = cancellation;
        this.resource = RESOURCES.get(reservation);
    }

    private String getResourceName(Class reservationType) {
        if (CarReservation.class.equals(reservationType)) {
            return CarReservation.RESOURCE_NAME;
        }
        if (FlightReservation.class.equals(reservationType)) {
            return FlightReservation.RESOURCE_NAME;
        }
        if (HotelReservation.class.equals(reservationType)) {
            return HotelReservation.RESOURCE_NAME;
        }
        if (Payment.class.equals(reservationType)) {
            return Payment.RESOURCE_NAME;
        }
        throw new IllegalArgumentException("Unknown resource: " + reservationType.getName());
    }

    public String getResource() {
        return resource;
    }

    public String getReservationName() {
        return reservation.getSimpleName();
    }

    public Class<? extends Reservation> getReservation() {
        return reservation;
    }

    public boolean isCancellation() {
        return cancellation;
    }

    public static RequestEvent fromType(String eventType) {
        return RESOURCES.keySet()
                .stream()
                .filter(r -> eventType.contains(r.getSimpleName()))
                .max(Comparator.comparing(r -> r.getSimpleName().length()))
                .map(r -> new RequestEvent(r, eventType.startsWith(CANCEL_PREFIX)))
                .orElseThrow(() -> new IllegalArgumentException("Unable to parse the provided event type: " + eventType));
    }
}
