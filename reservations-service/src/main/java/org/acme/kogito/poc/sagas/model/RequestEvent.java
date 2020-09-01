package org.acme.kogito.poc.sagas.model;

import org.acme.kogito.poc.sagas.model.car.CarReservation;
import org.acme.kogito.poc.sagas.model.flight.FlightReservation;
import org.acme.kogito.poc.sagas.model.hotel.HotelReservation;
import org.acme.kogito.poc.sagas.model.payment.PaymentRequest;

public class RequestEvent {

    private static final Class[] resources = {
            CarReservation.class,
            HotelReservation.class,
            FlightReservation.class,
            PaymentRequest.class
    };

    private static final String CANCEL_SUFFIX = "Cancel";

    private String resource;
    private Class<? extends Reservation> reservation;
    private boolean cancellation;

    private RequestEvent(Class reservation, boolean cancellation) {
        this.reservation = reservation;
        this.cancellation = cancellation;
        this.resource = getResourceName(reservation);
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
        if (PaymentRequest.class.equals(reservationType)) {
            return PaymentRequest.RESOURCE_NAME;
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
        for (Class<? extends Reservation> r : resources) {
            if (eventType.startsWith(r.getSimpleName())) {
                return new RequestEvent(r, eventType.endsWith(CANCEL_SUFFIX));
            }
        }
        throw new IllegalArgumentException("Unable to parse the provided event type: " + eventType);
    }
}
