package org.acme.kogito.poc.sagas.model.orders;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.acme.kogito.poc.sagas.model.Reservation;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseOrderModel extends Reservation {

    public Map<String, Object> payload = new HashMap<>();

    @JsonAnySetter
    public void unknown(String name, Object value) {
        payload.put(name, value);
    }

    public Map<String, Object> getPayload() {
        return payload;
    }
}
