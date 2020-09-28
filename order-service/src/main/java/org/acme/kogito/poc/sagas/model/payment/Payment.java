package org.acme.kogito.poc.sagas.model.payment;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Payment {

    private String token;
    private Float amount;
    private String currency;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
