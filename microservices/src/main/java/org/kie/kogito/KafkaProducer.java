package org.kie.kogito;

import java.time.Duration;
import java.util.List;
import java.util.Random;

import javax.enterprise.context.ApplicationScoped;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class KafkaProducer {

//    Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    @Incoming("payment-request")
//    @Outgoing("internal-stream")
//    public Multi<String> stream(Multi<String> input) {
//        return input.invoke(i -> logger.info("Received {}", i));
//    }
//
//    @Incoming("internal-stream")
//    @Outgoing("error-topic")
//    public Multi<String> process(Multi<String> input) {
//        return input
//                .onItem()
//                .transformToUni(i -> Uni.createFrom().item(i).onItem().delayIt().by(Duration.ofSeconds(5)))
//                .merge()
//                .invoke(i-> logger.info("Published {}", i));
//    }
}
