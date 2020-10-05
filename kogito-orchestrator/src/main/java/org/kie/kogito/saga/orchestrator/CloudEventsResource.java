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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.cloudevents.CloudEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.lra.annotation.ws.rs.LRA;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.impl.Sig;
import org.kie.kogito.saga.orchestrator.model.CorrelationKey;
import org.kie.kogito.saga.orchestrator.model.SagaModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
public class CloudEventsResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudEventsResource.class);
    private static final String REQUEST_SUFFIX = "Request";

    @Inject
    CorrelationService correlationService;

    @Inject
    Processes processes;

    @Inject
    ObjectMapper objectMapper;

    @ConfigProperty(name = "saga.process.id")
    String sagaProcessId;

    private Process<? extends Model> process;

    @Inject
    LRAService lraService;

    @Inject
    EventEmitterService emitterService;

    @PostConstruct
    public void init() {
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        process = getProcess(sagaProcessId);
        if (Objects.isNull(process)) {
            throw new ExceptionInInitializerError("Unable to guess what process to use");
        }

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response receive(CloudEvent event) {
        if (isLoopBack(event)) {
            LOGGER.info("Ignoring possible callback event: " + event.getType());
            return Response.accepted().build();
        }
        if (event.getType().endsWith(REQUEST_SUFFIX)) {
            String sagaId = event.getSubject();
            String lraUri = lraService.start(sagaProcessId);
            SagaModel model = new SagaModel()
                    .setId(event.getId())
                    .setPayload(event.getData())
                    .setSagaId(sagaId)
                    .setSagaDefinitionId(sagaProcessId)
                    .setLraId(lraUri);
            ProcessInstance<? extends Model> instance = process.createInstance(sagaId, createDomainModel(process, model));
            instance.start();
            LOGGER.info("Started new {} instance.", sagaProcessId);
        } else {
            processIntermediateEvent(event, process);
        }
        return Response.accepted().build();
    }

    @Context
    HttpHeaders httpHeaders;

    @PUT
    @Path("/{correlationId}/" + LRAService.COMPENSATE)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response compensate(@PathParam("correlationId") String correlationId) {
        CorrelationKey correlationKey = correlationService.get(correlationId);
        if(correlationKey == null) {
            LOGGER.info("Unable to compensate process with missing correlationId: {}. Try to clean up.", correlationId);
            Optional<List<String>> lraHeader = httpHeaders.getRequestHeaders().entrySet().stream().filter(e -> e.getKey().equals(LRA.LRA_HTTP_CONTEXT_HEADER)).map(Map.Entry::getValue).findFirst();
            if(lraHeader.isPresent()) {
                lraService.cancel(lraHeader.get().get(0));
            }
            throw new NotFoundException();
        }
        emitterService.sendCompensation(correlationKey.getCompensationEventType(), correlationKey.getProcessId());
        SagaModel sagaModel = new SagaModel();
        sagaModel.fromMap(process.instances().findById(correlationKey.getProcessInstanceId()).get().variables().toMap());
        lraService.cancel(sagaModel.getLraId());
        return Response.ok().build();

    }

    private Model createDomainModel(Process<? extends Model> process, SagaModel model) {
        Model domainModel = process.createModel();
        domainModel.fromMap(model.toMap());
        return domainModel;
    }

    private void processIntermediateEvent(CloudEvent event, Process<? extends Model> process) {
        if (correlationService.contains(event.getSubject())) {
            CorrelationKey correlationKey = correlationService.get(event.getSubject());
            if (correlationKey == null) {
                LOGGER.warn("Ignoring unexpected event of type {}. No matching active instance.", event.getType());
                return;
            }
            final String processInstanceId = correlationKey.getProcessInstanceId();
            Optional<? extends ProcessInstance<? extends Model>> processInstance = process.instances().findById(processInstanceId);
            if (processInstance.isPresent()) {
                LOGGER.info("Received event of type {}.", event.getType());
                processInstance.get().send(Sig.of("Message-" + event.getType(), "event payload", null));
                lraService.close(correlationKey.getLraId());
            } else {
                LOGGER.warn("Ignoring event of type {} with no matching process instance.", event.getType());
            }
        }
    }

    private Process<SagaModel> getProcess(String processName) {
        return (Process<SagaModel>) processes.processById(processName);
    }

    private boolean isLoopBack(CloudEvent event) {
        return !(event.getType().endsWith(REQUEST_SUFFIX) ^ event.getExtension(EventEmitterService.SAGA_EXTENSION) != null);
    }

}
