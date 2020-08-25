package org.kie.kafka;

import java.io.Serializable;

public class Event implements Serializable {
    static final long serialVersionUID = 42L;

    private String id;
    private String sagaId;
    private String type;

    public Event() {
    }

    public Event(String id, String sagaId, String type) {
        this.id = id;
        this.sagaId = sagaId;
        this.type = type;
    }

    public String getSagaId() {
        return sagaId;
    }

    public void setSagaId(String sagaId) {
        this.sagaId = sagaId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
