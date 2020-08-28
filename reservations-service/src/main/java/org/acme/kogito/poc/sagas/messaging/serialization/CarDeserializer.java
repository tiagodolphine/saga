package org.acme.kogito.poc.sagas.messaging.serialization;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import org.acme.kogito.poc.sagas.model.car.CarReservation;

public class CarDeserializer extends ObjectMapperDeserializer<CarReservation> {

    public CarDeserializer() {
        super(CarReservation.class);
    }
}
