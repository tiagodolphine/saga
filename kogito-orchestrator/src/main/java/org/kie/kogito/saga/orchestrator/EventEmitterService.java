/**
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.saga.orchestrator;

import java.net.URI;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.kie.kogito.saga.orchestrator.model.CorrelationKey;
import org.kie.kogito.saga.orchestrator.model.SagaModel;

@ApplicationScoped
public class EventEmitterService {

    private static final URI SOURCE = URI.create("org.kie.kogito.saga.orchestrator:TripReservation");

    @Inject
    @RestClient
    CloudEventsClient eventsClient;

    @Inject
    CorrelationService correlationService;

    public void sendRequest(String eventType, JsonObject payload, String processInstanceId) {
        String message = SagaModel.getMessage(payload, eventType);
        String correlation = UUID.randomUUID().toString();
        CloudEvent event = CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withType(eventType)
                .withSource(SOURCE)
                .withSubject(correlation)
                .withData(message.getBytes())
                .build();
        eventsClient.emit(event);
        correlationService.add(processInstanceId, new CorrelationKey()
                .setEventType(eventType)
                .setProcessInstanceId(processInstanceId));
    }

    public void sendResponse(String eventType, String sagaId, String processInstanceId) {
        CloudEvent event = CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withType(eventType)
                .withSource(SOURCE)
                .withSubject(sagaId)
                .build();
        eventsClient.emit(event);
        correlationService.deleteByProcessInstanceId(processInstanceId);
    }

    public void sendCompensation(String eventType, String processInstanceId) {
        String eventId = correlationService.getId(new CorrelationKey().setEventType(eventType).setProcessInstanceId(processInstanceId));
        if (eventId != null) {
            CloudEvent event = CloudEventBuilder.v1()
                    .withId(UUID.randomUUID().toString())
                    .withType(eventType)
                    .withSource(SOURCE)
                    .withSubject(eventId)
                    .build();
            eventsClient.emit(event);
        }
    }
}
