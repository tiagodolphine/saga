package org.kie.kogito;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.smallrye.mutiny.Multi;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ShippingService extends BaseService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    ConfigurationHolder configurationHolder;

    @Inject
    @Channel("shipping-error")
    Emitter<String> errorEmitter;

    @Inject
    @Channel("shipping-success")
    Emitter<String> successEmitter;

    @Incoming("shipping-request")
    @Outgoing("internal-shipping")
    public Multi<String> requests(Multi<String> input) {
        return input.invoke(i -> logger.info("Received Shipping {}", i));
    }

    @Incoming("internal-shipping")
    public void errors(String input) {
        handleRequest(input);
    }

    @Override
    AtomicBoolean isFailed() {
        return configurationHolder.getShipping();
    }

    @Incoming("shipping-cancel")
    public void compensations(String input) {
        logger.info("Cancel Shipping received {}", input);
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
        return "shipping";
    }

    @Override
    Long delay() {
        return configurationHolder.getShippingDelay().get();
    }
}
