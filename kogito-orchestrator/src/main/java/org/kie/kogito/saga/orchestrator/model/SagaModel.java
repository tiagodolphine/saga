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
package org.kie.kogito.saga.orchestrator.model;

import java.util.HashMap;
import java.util.Map;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.kie.kogito.Model;

@RegisterForReflection
public class SagaModel implements Model {

    private static final String SAGA_ID = "sagaId";
    private static final String ID = "id";
    private static final String PAYLOAD = "payload";
    private static final String PROCESS_INSTANCE_ID = "processInstanceId";
    private static final String EVENT = "event";
    private static final String SAGA_DEFINITION_ID = "sagaDefinitionId";
    private static final String LRA_ID = "lraId";

    private String id;
    private String payload;
    private String processInstanceId;
    private String sagaId;
    private String sagaDefinitionId;
    private String event;
    private String lraId;

    public String getId() {
        return id;
    }

    public SagaModel setId(String id) {
        this.id = id;
        return this;
    }

    public String getPayload() {
        return payload;
    }

    public SagaModel setPayload(byte[] payload) {
        if (payload != null) {
            this.payload = new String(payload);
        }
        return this;
    }

    public SagaModel setPayload(String payload) {
        this.payload = payload;
        return this;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public SagaModel setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
        return this;
    }

    public String getSagaId() {
        return sagaId;
    }

    public SagaModel setSagaId(String sagaId) {
        this.sagaId = sagaId;
        return this;
    }

    public String getSagaDefinitionId() {
        return sagaDefinitionId;
    }

    public SagaModel setSagaDefinitionId(String sagaDefinitionId) {
        this.sagaDefinitionId = sagaDefinitionId;
        return this;
    }

    public String getEvent() {
        return event;
    }

    public SagaModel setEvent(String event) {
        this.event = event;
        return this;
    }

    public SagaModel setLraId(String lraId) {
        this.lraId = lraId;
        return this;
    }

    public String getLraId() {
        return lraId;
    }

    @Override
    public void update(Map<String, Object> params) {
        if (params.containsKey(ID)) {
            this.id = (String) params.get(ID);
        }
        if (params.containsKey(PAYLOAD)) {
            this.payload = (String) params.get(PAYLOAD);
        }
        if (params.containsKey(EVENT)) {
            this.event = (String) params.get(EVENT);
        }
        if (params.containsKey(SAGA_ID)) {
            this.sagaId = (String) params.get(SAGA_ID);
        }
        if (params.containsKey(SAGA_DEFINITION_ID)) {
            this.sagaDefinitionId = (String) params.get(SAGA_DEFINITION_ID);
        }
        if (params.containsKey(PROCESS_INSTANCE_ID)) {
            this.processInstanceId = (String) params.get(PROCESS_INSTANCE_ID);
        }
        if (params.containsKey(LRA_ID)) {
            this.lraId = (String) params.get(LRA_ID);
        }
    }

    @Override
    public void fromMap(Map<String, Object> params) {
        fromMap((String) params.get(ID), params);
    }

    public void fromMap(String id, Map<String, Object> params) {
        this.id = id;
        this.payload = (String) params.get(PAYLOAD);
        this.event = (String) params.get(EVENT);
        this.sagaId = (String) params.get(SAGA_ID);
        this.sagaDefinitionId = (String) params.get(SAGA_DEFINITION_ID);
        this.processInstanceId = (String) params.get(PROCESS_INSTANCE_ID);
        this.lraId = (String) params.get(LRA_ID);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> params = new HashMap<>();
        params.put(ID, id);
        params.put(PAYLOAD, payload);
        params.put(PROCESS_INSTANCE_ID, this.processInstanceId);
        params.put(SAGA_ID, this.sagaId);
        params.put(SAGA_DEFINITION_ID, this.sagaDefinitionId);
        params.put(EVENT, this.event);
        params.put(LRA_ID, this.lraId);
        return params;
    }
}
