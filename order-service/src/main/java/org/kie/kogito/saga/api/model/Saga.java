package org.kie.kogito.saga.api.model;

public class Saga {

    private final String replyWith;
    private final String replyWithError;
    private final Request request;

    public Saga(String replyWith, String replyWithError, Request request) {
        this.replyWith = replyWith;
        this.replyWithError = replyWithError;
        this.request = request;
    }

    public String getReplyWith() {
        return replyWith;
    }

    public String getReplyWithError() {
        return replyWithError;
    }

    public Request getRequest() {
        return request;
    }
}
