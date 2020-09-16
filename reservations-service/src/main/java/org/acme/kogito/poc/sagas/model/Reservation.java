package org.acme.kogito.poc.sagas.model;

public abstract class Reservation {

    private Boolean fail;

    private Integer delay;

    public Boolean shouldFail() {
        return fail;
    }

    public void setFail(Boolean fail) {
        this.fail = fail;
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }
}
