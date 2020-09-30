package org.acme.kogito.poc.sagas.services;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.narayana.lra.client.NarayanaLRAClient;
import io.smallrye.mutiny.Uni;
import org.acme.kogito.poc.sagas.model.orders.Order;
import org.acme.kogito.poc.sagas.model.orders.OrderStatus;

@ApplicationScoped
public class OrderService {

    @Inject
    NarayanaLRAClient lraClient;

    private Map<String, Order> orders = new HashMap<>();

    public Uni<String> create(Order order) {
        return Uni.createFrom().item(order).onItem().transform(o -> {
            String lraPath = lraClient.getCurrent().getPath();
            String id = lraPath.substring(lraPath.lastIndexOf("/") + 1);
            o.setLraPath(lraPath);
            orders.put(id, o.setStatus(OrderStatus.PENDING));
            return id;
        });
    }

    public Uni<OrderStatus> getStatus(String lraId) {
        return Uni.createFrom()
                .item(lraId)
                .onItem()
                .transform(id -> orders.get(id))
                .onItem()
                .ifNotNull()
                .transform(order -> order.getStatus());
    }

    public Uni<String> cancel(String lraId) {
        return Uni.createFrom().item(lraId).onItem().invoke(id -> orders.get(id).setStatus(OrderStatus.FAILED));
    }

    public void abort(String lraId) {
        lraClient.cancelLRA(URI.create(orders.get(lraId).getLraPath()));
    }

    public Uni<String> complete(String lraId) {
        return Uni.createFrom().item(lraId).onItem().invoke(id -> orders.get(id).setStatus(OrderStatus.COMPLETED));
    }
}
