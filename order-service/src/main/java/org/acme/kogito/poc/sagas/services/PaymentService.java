package org.acme.kogito.poc.sagas.services;

import java.time.Duration;
import javax.enterprise.context.ApplicationScoped;

import io.smallrye.mutiny.Uni;
import org.acme.kogito.poc.sagas.model.payment.Payment;
import org.acme.kogito.poc.sagas.model.payment.PaymentStatus;

@ApplicationScoped
public class PaymentService {

    public Uni<PaymentStatus> reserveCredit(Payment payment) {
        //TODO: Complete
        return Uni.createFrom()
                .item(payment)
                .onItem()
                .delayIt()
                .by(Duration.ofSeconds(10))
                .onItem()
                .transform((p) -> PaymentStatus.APPROVED);
    }

    public Uni<PaymentStatus> abort(String id) {
        //TODO: Complete
        return Uni.createFrom().item(id).onItem().transform(i -> PaymentStatus.REJECTED);
    }
}
