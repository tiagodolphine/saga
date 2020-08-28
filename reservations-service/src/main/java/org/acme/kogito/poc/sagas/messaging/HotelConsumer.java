package org.acme.kogito.poc.sagas.messaging;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.acme.kogito.poc.sagas.model.hotel.HotelReservation;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class HotelConsumer extends MessageConsumer<HotelReservation> {

    @Inject
    @Channel("hotel-reservation-success")
    Emitter<String> successEmitter;

    @Inject
    @Channel("hotel-reservation-failed")
    Emitter<String> failureEmitter;

    @Incoming("hotel-reservations")
    public void doReservation(HotelReservation reservation) {
        handleReservation(reservation);
    }

    @Override
    public Emitter<String> getFailureEmitter() {
        return failureEmitter;
    }

    @Override
    public Emitter<String> getSuccessEmitter() {
        return successEmitter;
    }
}
