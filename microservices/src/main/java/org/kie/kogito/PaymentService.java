package org.kie.kogito;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class PaymentService extends BaseService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    ConfigurationHolder configurationHolder;

    @Inject
    @Channel("payment-error")
    Emitter<String> errorEmitter;

    @Inject
    @Channel("payment-success")
    Emitter<String> successEmitter;

    @Incoming("payment-request")
    @Outgoing("internal-payment")
    public Multi<String> requests(Multi<String> input) {
        return input.invoke(i -> logger.info("Received Payment {}", i));
    }

    @Incoming("internal-payment")
    public void errors(String input) {
        handleRequest(input);
    }

    @Override
    AtomicBoolean isFailed() {
        return configurationHolder.getPayment();
    }

    @Incoming("payment-cancel")
    public void compensations(String input) {
        logger.info("Cancel Payment received {}", input);
    }

    @Override
    Emitter<String> errorEmitter() {
        return errorEmitter;
    }

    @Override
    Emitter<String> successEmitter() {
        return successEmitter;
    }

    @Override
    String serviceName() {
        return "payment";
    }

    @Override
    Long delay() {
        return configurationHolder.getPaymentDelay().get();
    }
}
