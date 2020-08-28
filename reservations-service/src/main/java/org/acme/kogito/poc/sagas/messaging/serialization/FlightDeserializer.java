package org.acme.kogito.poc.sagas.messaging.serialization;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import org.acme.kogito.poc.sagas.model.flight.FlightReservation;

public class FlightDeserializer extends ObjectMapperDeserializer<FlightReservation> {

    public FlightDeserializer() {
        super(FlightReservation.class);
    }
}
