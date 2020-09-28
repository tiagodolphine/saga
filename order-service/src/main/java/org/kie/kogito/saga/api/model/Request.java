package org.kie.kogito.saga.api.model;

public class Request {

    private final String requestEvent;
    private final String expectedEvent;
    private final String compensateWith;
    private final String waitFor;
    private final String payload;

    public Request(String requestEvent, String expectedEvent, String compensateWith, String waitFor, String payload) {
        this.requestEvent = requestEvent;
        this.expectedEvent = expectedEvent;
        this.compensateWith = compensateWith;
        this.waitFor = waitFor;
        this.payload = payload;
    }

    public String getRequestEvent() {
        return requestEvent;
    }

    public String getExpectedEvent() {
        return expectedEvent;
    }

    public String getCompensateWith() {
        return compensateWith;
    }

    public String getWaitFor() {
        return waitFor;
    }

    public String getPayload() {
        return payload;
    }
}
