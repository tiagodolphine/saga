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
import org.eclipse.microprofile.lra.annotation.ws.rs.LRA;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.kie.kogito.saga.orchestrator.model.CorrelationKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class EventEmitterService {

    private static Logger LOGGER = LoggerFactory.getLogger(EventEmitterService.class);

    //This extension attribute can be used for filtering out events of the same type
    //but involved services must propagate all extensions in the response cloudevent
    public static final String SAGA_EXTENSION = "sagaid";

    @Inject
    @RestClient
    CloudEventsClient eventsClient;

    @Inject
    CorrelationService correlationService;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    LRAService lraService;

    public void sendRequest(String eventType, String payload, String processInstanceId, String sagaDefinitionId, String lraId) {
        LOGGER.info("sendRequest {} {}", eventType, processInstanceId);
        String message = getMessage(payload, eventType);
        String correlation = UUID.randomUUID().toString();
        CloudEvent event = CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withType(eventType)
                .withSource(createSource(sagaDefinitionId))
                .withSubject(correlation)
                .withData(message.getBytes())
                .withDataContentType(MediaType.APPLICATION_JSON)
                .withExtension(SAGA_EXTENSION, sagaDefinitionId)
                .withExtension(LRA.LRA_HTTP_CONTEXT_HEADER, lraId)
                .build();
        Response response = eventsClient.emit(event);
        if (response.getStatus() < Response.Status.BAD_REQUEST.getStatusCode()) {
            correlationService.add(correlation, new CorrelationKey()
                    .setEventType(eventType)
                    .setProcessInstanceId(processInstanceId));
        }
    }

    public void sendResponse(String eventType, String sagaId, String processInstanceId, String sagaDefinitionId) {
        LOGGER.info("sendResponse {} {}", eventType, processInstanceId);
        CloudEvent event = CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withType(eventType)
                .withSource(createSource(sagaDefinitionId))
                .withSubject(sagaId)
                .withDataContentType(MediaType.APPLICATION_JSON)
                .build();
        Response response = eventsClient.emit(event);
        if (response.getStatus() < Response.Status.BAD_REQUEST.getStatusCode()) {
            correlationService.deleteByProcessInstanceId(processInstanceId);
        }
    }

    public void sendRequestLRA(String eventType, String payload, String processInstanceId, String sagaDefinitionId, String lraId, String compensationEvent) {
        sendRequestLRA(eventType, payload, processInstanceId, sagaDefinitionId, lraId, compensationEvent, 0);
    }

    public void sendRequestLRA(String eventType, String payload, String processInstanceId, String sagaDefinitionId, String lraId, String compensationEvent, Integer timeLimit) {
        LOGGER.info("sendRequest {} {}", eventType, processInstanceId);
        String message = getMessage(payload, eventType);
        String correlation = UUID.randomUUID().toString();
        CloudEvent event = CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withType(eventType)
                .withSource(createSource(sagaDefinitionId))
                .withSubject(correlation)
                .withData(message.getBytes())
                .withDataContentType(MediaType.APPLICATION_JSON)
                .withExtension(SAGA_EXTENSION, sagaDefinitionId)
                .build();
        Response response = eventsClient.emit(event);
        if (response.getStatus() < Response.Status.BAD_REQUEST.getStatusCode()) {
            String childLra = lraService.addParticipant(sagaDefinitionId, lraId, correlation, timeLimit.longValue());
            correlationService.add(correlation, new CorrelationKey()
                    .setEventType(eventType)
                    .setProcessInstanceId(processInstanceId)
                    .setProcessId(sagaDefinitionId)
                    .setCompensationEventType(compensationEvent)
                    .setLraId(childLra)
            );
        } else {
            lraService.cancel(lraId);
        }
    }

    public void sendCompensation(String eventType, String sagaDefinitionId) {
        LOGGER.info("sendCompensation {} {}", eventType, sagaDefinitionId);
        String correlation = UUID.randomUUID().toString();
        CloudEvent event = CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withType(eventType)
                .withSource(createSource(sagaDefinitionId))
                .withSubject(correlation)
                .withDataContentType(MediaType.APPLICATION_JSON)
                .withExtension(SAGA_EXTENSION, sagaDefinitionId)
                .build();
        eventsClient.emit(event);
    }

    public void sendResponseLRA(String eventType, String sagaId, String processInstanceId, String sagaDefinitionId, String lraId) {
        LOGGER.info("sendResponse {} {}", eventType, processInstanceId);
        CloudEvent event = CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withType(eventType)
                .withSource(createSource(sagaDefinitionId))
                .withSubject(sagaId)
                .withDataContentType(MediaType.APPLICATION_JSON)
                .build();
        Response response = eventsClient.emit(event);
        if (response.getStatus() < Response.Status.BAD_REQUEST.getStatusCode()) {
            correlationService.deleteByProcessInstanceId(processInstanceId);
            lraService.close(lraId);
        }
    }

    private String getMessage(String payload, String service) {
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
