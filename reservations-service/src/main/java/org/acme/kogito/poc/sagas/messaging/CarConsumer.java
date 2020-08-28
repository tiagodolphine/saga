package org.acme.kogito.poc.sagas.messaging;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.acme.kogito.poc.sagas.model.car.CarReservation;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class CarConsumer extends MessageConsumer<CarReservation> {

    @Inject
    @Channel("car-reservation-success")
    Emitter<String> successEmitter;

    @Inject
    @Channel("car-reservation-failed")
    Emitter<String> failureEmitter;

    @Incoming("car-reservations")
    public void doReservation(CarReservation reservation) {
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
