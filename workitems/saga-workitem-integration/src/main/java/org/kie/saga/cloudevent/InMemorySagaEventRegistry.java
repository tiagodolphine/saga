package org.kie.saga.cloudevent;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemorySagaEventRegistry implements SagaEventRegistry {

    private static final Map<String, SagaEventContext> REGISTRY = new ConcurrentHashMap<>();

    @Override
    public void register(SagaEventContext eventContext) {
        REGISTRY.put(eventContext.getId(), eventContext);
    }

    @Override
    public Optional<SagaEventContext> get(String id) {
        return Optional.ofNullable(REGISTRY.get(id));
    }
}
