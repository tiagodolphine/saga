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

import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
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

    @Inject
    CorrelationService correlationService;

    @Inject
    Processes processes;

    @Inject
    ObjectMapper objectMapper;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response receive(CloudEvent event) throws Exception {
        //this is the process id (from cloud event extension header)
        String sagaDefinitionId = Optional
                .ofNullable(event.getExtension("saga-definition-id"))//header ce-saga-definition-id
                .map(String::valueOf)
                .orElse("tripReservation");

        final Process<? extends Model> process = getProcess(sagaDefinitionId);

        String eventType = event.getType();
        switch (eventType) {
            case "SagaRequest":
                SagaModel model = new SagaModel()
                        .setId(event.getId())
                        .setPayload(event.getData())
                        .setSagaId(event.getSubject());
                ProcessInstance<? extends Model> instance = process.createInstance(createDomainModel(process, model));
                instance.start();
                LOGGER.info("Started new {} instance.", sagaDefinitionId);
                break;
            default:
                processIntermediateEvent(event, process);
        }
        return Response.accepted().build();
    }

    private Model createDomainModel(Process<? extends Model> process, SagaModel model) throws JsonProcessingException {
        return objectMapper.readValue(objectMapper.writeValueAsString(model),
                                      process.createModel().getClass());
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
}
