package org.kie.kogito.saga.orchestrator.model;

public class CompensationData {

    private String proccessId;
    private String compensationEventType;

    public String getProccessId() {
        return proccessId;
    }

    public CompensationData setProccessId(String proccessId) {
        this.proccessId = proccessId;
        return this;
    }

    public String getCompensationEventType() {
        return compensationEventType;
    }

    public CompensationData setCompensationEventType(String compensationEventType) {
        this.compensationEventType = compensationEventType;
        return this;
    }
}
