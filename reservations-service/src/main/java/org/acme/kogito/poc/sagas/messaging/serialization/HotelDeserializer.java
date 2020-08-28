package org.acme.kogito.poc.sagas.messaging.serialization;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import org.acme.kogito.poc.sagas.model.hotel.HotelReservation;

public class HotelDeserializer extends ObjectMapperDeserializer<HotelReservation> {

    public HotelDeserializer() {
        super(HotelReservation.class);
    }
}
