package org.acme.kogito.poc.sagas.messaging.serialization;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import org.acme.kogito.poc.sagas.model.payment.PaymentRequest;

public class PaymentDeserializer extends ObjectMapperDeserializer<PaymentRequest> {

    public PaymentDeserializer() {
        super(PaymentRequest.class);
    }
}
