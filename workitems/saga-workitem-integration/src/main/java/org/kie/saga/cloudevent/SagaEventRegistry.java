package org.kie.saga.cloudevent;

import java.util.Optional;

public interface SagaEventRegistry {

   void register(SagaEventContext eventContext);
   Optional<SagaEventContext> get(String id);

}
