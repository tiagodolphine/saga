package org.acme.kogito.poc.sagas.messaging;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.acme.kogito.poc.sagas.model.payment.PaymentRequest;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class PaymentConsumer extends MessageConsumer<PaymentRequest> {

    @Inject
    @Channel("payment-reservation-success")
    Emitter<String> successEmitter;

    @Inject
    @Channel("payment-reservation-failed")
    Emitter<String> failureEmitter;

    @Incoming("payment-reservations")
    public void doReservation(PaymentRequest reservation) {
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
