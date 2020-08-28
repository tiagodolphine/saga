package org.acme.kogito.poc.sagas.messaging;

import java.time.Duration;
import javax.inject.Inject;

import io.smallrye.mutiny.Uni;
import org.acme.kogito.poc.sagas.model.Reservation;
import org.acme.kogito.poc.sagas.services.ReservationService;
import org.acme.kogito.poc.sagas.services.TrollService;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MessageConsumer<T extends Reservation> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumer.class);

    @Inject
    ReservationService<T> service;

    @Inject
    TrollService trollService;

    public void handleReservation(T reservation) {
        int delay = trollService.withDelay();
        boolean fail = trollService.shouldFail();
        LOGGER.info("Received reservation request for {}. Delay set to {} seconds. Should fail {}", reservation.getId(), delay, fail);

        Uni<T> uni = Uni.createFrom()
                .item(reservation);
        if (delay > 0) {
            uni.onItem().delayIt()
                    .by(Duration.ofSeconds(delay));
        }
        uni.subscribe().with(i -> {
            if (fail) {
                LOGGER.info("Sending reservation failed event: {}", reservation.getId());
                getFailureEmitter().send(reservation.getId());
            } else {
                LOGGER.info("Sending reservation success event: {}", reservation.getId());
                service.add(reservation);
                getSuccessEmitter().send(reservation.getId());
            }
        });
    }

    protected abstract Emitter<String> getSuccessEmitter();

    protected abstract Emitter<String> getFailureEmitter();
}
