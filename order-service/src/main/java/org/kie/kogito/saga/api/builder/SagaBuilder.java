package org.kie.kogito.saga.api.builder;

import org.kie.kogito.saga.api.model.Request;
import org.kie.kogito.saga.api.model.Saga;

public class SagaBuilder {

    private String replyWith;
    private String replyWithError;
    private Request request;

    public SagaBuilder withReplyWith(String replyWith) {
        this.replyWith = replyWith;
        return this;
    }

    public SagaBuilder withReplyWithError(String replyWithError) {
        this.replyWithError = replyWithError;
        return this;
    }

    public SagaBuilder withRequest(Request request) {
        this.request = request;
        return this;
    }

    public Saga build() {
        return new Saga(replyWith, replyWithError, request);
    }
}
