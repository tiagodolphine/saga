package org.acme.kogito.poc.sagas.model.orders;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Item {

    private String reference;
    private float price;
    private int quantity;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
