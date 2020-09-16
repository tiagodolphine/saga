package org.acme.kogito.poc.sagas.model.payment;

import javax.validation.constraints.NotNull;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.acme.kogito.poc.sagas.model.Reservation;

@RegisterForReflection
public class Payment extends Reservation {

    public static final String RESOURCE_NAME = "payments";

    @NotNull
    private String token;
    @NotNull
    private Float amount;
    @NotNull
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
