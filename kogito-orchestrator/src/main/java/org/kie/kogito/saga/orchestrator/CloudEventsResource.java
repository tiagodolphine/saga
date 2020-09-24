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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.Consumes;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.cloudevents.CloudEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.impl.Sig;
import org.kie.kogito.saga.orchestrator.model.SagaModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
public class CloudEventsResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudEventsResource.class);
    private static final String REQUEST_SUFFIX = "Request";
    private static final String MICRO_SAGA = "microsaga";
    private static final String MICRO_SAGA_REQUEST = "SagaRequest";

    @Inject
    CorrelationService correlationService;

    @Inject
    Processes processes;

    @Inject
    ObjectMapper objectMapper;

    @ConfigProperty(name = "saga.process.id")
    String sagaProcessId;

    @PostConstruct
    public void init() {
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response receive(CloudEvent event) {
        final String eventType = event.getType();
        final Process<? extends Model> process = getProcess(sagaProcessId);
        if (Objects.isNull(process)) {
            throw new NotFoundException("Saga definition not found " + sagaProcessId);
        }
        if (MICRO_SAGA.equals(sagaProcessId)) {
            if(event.getType().endsWith(MICRO_SAGA_REQUEST)) {
                try {
                    processMicroSaga(event, process);
                } catch (IOException e) {
                    LOGGER.error("Unable to create micro saga", e);
                    throw new BadRequestException();
                }
            } else {
                processIntermediateMicroSagaRequest(event, process);
            }
        } else if (eventType.endsWith(REQUEST_SUFFIX)) {
            String sagaId = event.getSubject();
            SagaModel model = new SagaModel()
                    .setId(event.getId())
                    .setPayload(event.getData())
                    .setSagaId(sagaId)
                    .setSagaDefinitionId(sagaProcessId);
            ProcessInstance<? extends Model> instance = process.createInstance(sagaId, createDomainModel(process, model));
            instance.start();
            LOGGER.info("Started new {} instance.", sagaProcessId);
        } else {
            processIntermediateEvent(event, process);
        }
        return Response.accepted().build();
    }

    private Model createDomainModel(Process<? extends Model> process, SagaModel model) {
        Model domainModel = process.createModel();
        domainModel.fromMap(model.toMap());
        return domainModel;
    }

    private void processIntermediateEvent(CloudEvent event, Process<? extends Model> process) {
        if (correlationService.contains(event.getSubject())) {
            final String processInstanceId = correlationService.getProcessInstanceId(event.getSubject());
            if (processInstanceId == null) {
                LOGGER.warn("Ignoring unexpected event of type {}. No matching active instance.", event.getType());
            }
            Optional<? extends ProcessInstance<? extends Model>> processInstance = process.instances().findById(processInstanceId);
            if (processInstance.isPresent()) {
                LOGGER.info("Received event of type {}.", event.getType());
                processInstance.get().send(Sig.of("Message-" + event.getType(), "event payload", null));
            } else {
                LOGGER.warn("Ignoring event of type {} with no matching process instance.", event.getType());
            }
        }
    }

    private Process<SagaModel> getProcess(String processName) {
        return (Process<SagaModel>) processes.processById(processName);
    }

    private void processMicroSaga(CloudEvent event, Process<? extends Model> process) throws IOException {
        Map<String, Object> params = new HashMap<>();
        JsonNode jsonNode = objectMapper.readTree(event.getData());
        params.put("requestSuccessEvent", jsonNode.get("replyWith").asText());
        params.put("requestFailedEvent", jsonNode.get("replyErrorWith").asText());
        JsonNode request = jsonNode.get("request");
        params.put("requestEvent", request.get("requestEvent").asText());
        params.put("expectedEvent", request.get("expectedEvent").asText());
        params.put("compensationEvent", request.get("compensateWith").asText());
        params.put("timer", request.get("waitFor").asText());
        params.put("payload", request.get("payload").toPrettyString());
        params.put("sagaId", event.getSubject());
        params.put("sagaDefinitionId", MICRO_SAGA);
        Model model = process.createModel();
        model.fromMap(params);
        process.createInstance(model).start();
    }

    private void processIntermediateMicroSagaRequest(CloudEvent event, Process<? extends Model> process) {
        if (correlationService.contains(event.getSubject())) {
            final String processInstanceId = correlationService.getProcessInstanceId(event.getSubject());
            if (processInstanceId == null) {
                LOGGER.warn("Ignoring unexpected event of type {}. No matching active instance.", event.getType());
            }
            Optional<? extends ProcessInstance<? extends Model>> processInstance = process.instances().findById(processInstanceId);
            if (processInstance.isPresent()) {
                LOGGER.info("Received event of type {}.", event.getType());
                String eventType = event.getType();
                Map<String, Object> params = processInstance.get().variables().toMap();
                if(eventType.equals(params.get("requestEvent"))) {
                    LOGGER.debug("Ignore loopback event");
                    return;
                }
                if (eventType.equals(params.get("expectedEvent"))) {
                    processInstance.get().send(Sig.of("Message-ExpectedEvent", "event payload", null));
                } else {
                    processInstance.get().send(Sig.of("Message-UnexpectedEvent", "event payload", null));
                }
            } else {
                LOGGER.warn("Ignoring event of type {} with no matching process instance.", event.getType());
            }
        }
    }
}
