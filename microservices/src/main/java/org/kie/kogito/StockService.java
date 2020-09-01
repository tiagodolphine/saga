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
public class StockService extends BaseService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    ConfigurationHolder configurationHolder;

    @Inject
    @Channel("stock-error")
    Emitter<String> errorEmitter;

    @Inject
    @Channel("stock-success")
    Emitter<String> successEmitter;

    @Incoming("stock-request")
    @Outgoing("internal-stock")
    public Multi<String> requests(Multi<String> input) {
        return input.invoke(i -> logger.info("Received {} {}", serviceName(), i));
    }

    @Incoming("internal-stock")
    public void errors(String input) {
        handleRequest(input);
    }

    @Override
    AtomicBoolean isFailed() {
        return configurationHolder.getStock();
    }

    @Incoming("stock-cancel")
    public void compensations(String input) {
        logger.info("Cancel Stock received {}", input);
        cancel(input);
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
        return "stock";
    }

    @Override
    Long delay() {
        return configurationHolder.getStockDelay().get();
    }
}
