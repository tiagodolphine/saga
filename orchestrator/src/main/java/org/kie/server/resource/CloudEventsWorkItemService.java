package org.kie.server.resource;

import java.util.HashMap;
import java.util.Objects;

import io.cloudevents.CloudEvent;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.kie.saga.cloudevent.InMemorySagaEventRegistry;
import org.kie.saga.cloudevent.SagaEventRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CloudEventsWorkItemService {

    SagaEventRegistry sagaEventRegistry;

    @Autowired
    ProcessService processService;

    @Autowired
    RuntimeDataService runtimeDataService;

    public CloudEventsWorkItemService() {
        sagaEventRegistry = new InMemorySagaEventRegistry();
    }

    public void handleEvent(CloudEvent event) {

        processService.getWorkItem(1L);

//        String eventId = event.getSubject();
//        sagaEventRegistry
//                .get(eventId)
//                .filter(c -> Objects.equals(c.getType(), event.getType()))
//                .ifPresent(c -> processService.completeWorkItem(Long.parseLong(c.getWorkItemId()), new HashMap<>()));
    }
}
