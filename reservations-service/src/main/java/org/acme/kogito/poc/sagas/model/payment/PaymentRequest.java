package org.acme.kogito.poc.sagas.model.payment;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.acme.kogito.poc.sagas.model.Reservation;

public class PaymentRequest implements Reservation {

    @JsonProperty(index = 0)
    @NotNull
    private String id;
    @NotNull
    private String token;
    @NotNull
    private Float amount;
    @NotNull
    private String currency;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

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
