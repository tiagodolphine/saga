package org.kie.kogito.saga.api.builder;

import org.kie.kogito.saga.api.model.Request;

public class RequestBuilder {

    private String requestEvent;
    private String expectedEvent;
    private String compensateWith;
    private String waitFor;
    private String payload;

    public RequestBuilder withRequestEvent(String requestEvent) {
        this.requestEvent = requestEvent;
        return this;
    }

    public RequestBuilder withExpectedEvent(String expectedEvent) {
        this.expectedEvent = expectedEvent;
        return this;
    }

    public RequestBuilder withCompensateWith(String compensateWith) {
        this.compensateWith = compensateWith;
        return this;
    }

    public RequestBuilder withWaitFor(String waitFor) {
        this.waitFor = waitFor;
        return this;
    }

    public RequestBuilder withPayload(String payload) {
        this.payload = payload;
        return this;
    }

    public Request build() {
        return new Request(requestEvent, expectedEvent, compensateWith, waitFor, payload);
    }

}
