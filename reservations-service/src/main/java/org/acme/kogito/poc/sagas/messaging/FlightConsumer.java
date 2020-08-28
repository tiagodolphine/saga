package org.acme.kogito.poc.sagas.messaging;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.acme.kogito.poc.sagas.model.flight.FlightReservation;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class FlightConsumer extends MessageConsumer<FlightReservation> {

    @Inject
    @Channel("flight-reservation-success")
    Emitter<String> successEmitter;

    @Inject
    @Channel("flight-reservation-failed")
    Emitter<String> failureEmitter;

    @Incoming("flight-reservations")
    public void doReservation(FlightReservation reservation) {
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
