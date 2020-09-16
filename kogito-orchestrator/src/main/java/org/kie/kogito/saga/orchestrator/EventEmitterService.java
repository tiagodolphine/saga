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
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.kie.kogito.saga.orchestrator.model.CorrelationKey;

@ApplicationScoped
public class EventEmitterService {

    @Inject
    @RestClient
    CloudEventsClient eventsClient;

    @Inject
    CorrelationService correlationService;

    @Inject
    ObjectMapper objectMapper;

    public void sendRequest(String processInstanceId, String eventType, String payload) {
        String message = getMessage(payload, eventType);
        String correlation = UUID.randomUUID().toString();
        //FIXME: get the saga id
        String sagaId = processInstanceId;
        CloudEvent event = CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withType(eventType)
                .withSource(createSource(sagaId))
                .withSubject(correlation)
                .withData(message.getBytes())
                .withDataContentType(MediaType.APPLICATION_JSON)
                .build();
        Response response = eventsClient.emit(event);
        if (response.getStatus() < Response.Status.BAD_REQUEST.getStatusCode()) {
            correlationService.add(correlation, new CorrelationKey()
                    .setEventType(eventType)
                    .setProcessInstanceId(processInstanceId));
        }
    }

    public void sendResponse(String eventType, String sagaId, String processInstanceId) {
        CloudEvent event = CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withType(eventType)
                .withSource(createSource(sagaId))
                .withSubject(sagaId)
                .withDataContentType(MediaType.APPLICATION_JSON)
                .build();
        Response response = eventsClient.emit(event);
        if (response.getStatus() < Response.Status.BAD_REQUEST.getStatusCode()) {
            correlationService.deleteByProcessInstanceId(processInstanceId);
        }
    }

    public void sendCompensation(String compensateForType, String eventType, String processInstanceId) {
        String eventId = correlationService.getId(new CorrelationKey().setEventType(compensateForType).setProcessInstanceId(processInstanceId));
        //FIXME
        String sagaId = processInstanceId;
        if (eventId != null) {
            CloudEvent event = CloudEventBuilder.v1()
                    .withId(UUID.randomUUID().toString())
                    .withType(eventType)
                    .withSource(createSource(sagaId))
                    .withSubject(eventId)
                    .withDataContentType(MediaType.APPLICATION_JSON)
                    .build();
            eventsClient.emit(event);
        }
    }

    public String getMessage(String payload, String service) {
        try {
            return Optional.ofNullable(objectMapper.readValue(payload, JsonNode.class))
                    .map(node -> node.get(service))
                    .map(JsonNode::toPrettyString)
                    .orElseThrow(() -> new IllegalArgumentException("Unable to retrieve message for service: " + service));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error parsing the payload json", e);
        }
    }

    private static URI createSource(String sagaId) {
        return URI.create("org.kie.kogito.saga.orchestrator:" + sagaId);
    }
}
