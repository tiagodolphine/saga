package org.acme.kogito.poc.sagas.model.orders;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.acme.kogito.poc.sagas.model.payment.Payment;

@RegisterForReflection
public class Order {

    private List<Item> items;
    private Payment payment;
    private OrderStatus status;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Order setStatus(OrderStatus status) {
        this.status = status;
        return this;
    }
}
