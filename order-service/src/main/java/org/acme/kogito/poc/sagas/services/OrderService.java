package org.acme.kogito.poc.sagas.services;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.smallrye.mutiny.Uni;
import org.acme.kogito.poc.sagas.model.orders.Order;
import org.acme.kogito.poc.sagas.model.orders.OrderStatus;

@ApplicationScoped
public class OrderService {

    @Inject
    PaymentService paymentService;

    private Map<UUID, Order> orders = new HashMap<>();

    public Uni<UUID> create(Order order) {
        return Uni.createFrom().item(order).onItem().transform(o -> {
            UUID id = UUID.randomUUID();
            orders.put(id, o.setStatus(OrderStatus.PENDING));
            paymentService.reserveCredit(o.getPayment()).onItem().transform(status -> {
                switch (status) {
                    case APPROVED:
                        return OrderStatus.COMPLETED;
                    case REJECTED:
                        return OrderStatus.FAILED;
                }
                return null;
            }).subscribe().with(
                    orderStatus -> orders.put(id, orders.get(id).setStatus(OrderStatus.COMPLETED)),
                    failure -> orders.put(id, orders.get(id).setStatus(OrderStatus.FAILED)));
            return id;
        });
    }

    public Uni<OrderStatus> getStatus(UUID id) {
        return Uni.createFrom()
                .item(id)
                .onItem()
                .transform(i -> orders.get(i))
                .onItem()
                .ifNotNull()
                .transform(order -> order.getStatus());
    }

    public Uni<OrderStatus> abort(UUID id) {
        return paymentService.abort(orders.get(id).getPayment().getToken())
                .onItem()
                .transform(paymentStatus -> OrderStatus.FAILED);
    }
}
