package org.kie.kogito;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class OrderService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Incoming("order-success")
    public void success(String input) {
        logger.info("Order END SUCCESS response: {}", input);
    }

    @Incoming("order-error")
    public void error(String input) {
        logger.info("Order END ERROR response: {}", input);
    }
}
