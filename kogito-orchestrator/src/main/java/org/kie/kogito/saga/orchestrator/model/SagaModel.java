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

import io.vertx.core.json.JsonObject;
import org.kie.kogito.Model;

public class SagaModel implements Model {

    private static final String ID = "id";
    private static final String PAYLOAD = "payload";
    private static final String PROCESS_INSTANCE_ID = "processInstanceId";
    private static final String SAGA_ID = "sagaId";
    private static final String EVENT = "event";

    private String id;
    private JsonObject payload;
    private String processInstanceId;
    private String sagaId;
    private String event;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JsonObject getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        if (payload != null) {
            this.payload = new JsonObject(new String(payload));
        }
    }

    public void setPayload(JsonObject payload) {
        this.payload = payload;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getSagaId() {
        return sagaId;
    }

    public void setSagaId(String sagaId) {
        this.sagaId = sagaId;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    @Override
    public void update(Map<String, Object> params) {
        if (params.containsKey(ID)) {
            this.id = (String) params.get(ID);
        }
        if (params.containsKey(PAYLOAD)) {
            this.payload = new JsonObject((String) params.get(PAYLOAD));
        }
        if (params.containsKey(EVENT)) {
            this.event = (String) params.get(EVENT);
        }
        if (params.containsKey(SAGA_ID)) {
            this.sagaId = (String) params.get(SAGA_ID);
        }
        if (params.containsKey(PROCESS_INSTANCE_ID)) {
            this.processInstanceId = (String) params.get(PROCESS_INSTANCE_ID);
        }
    }

    @Override
    public void fromMap(Map<String, Object> params) {
        fromMap((String) params.get(ID), params);
    }

    public void fromMap(String id, Map<String, Object> params) {
        this.id = id;
        this.payload = new JsonObject((String) params.get(PAYLOAD));
        this.event = (String) params.get(EVENT);
        this.sagaId = (String) params.get(SAGA_ID);
        this.processInstanceId = (String) params.get(PROCESS_INSTANCE_ID);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> params = new HashMap<>();
        params.put(ID, id);
        params.put(PAYLOAD, payload.encode());
        params.put(PROCESS_INSTANCE_ID, this.processInstanceId);
        params.put(SAGA_ID, this.sagaId);
        params.put(EVENT, this.event);
        return params;
    }

    public static String getMessage(JsonObject payload, String service) {
        if (!payload.containsKey(service)) {
            throw new IllegalArgumentException("Unable to retrieve message for service: " + service);
        }
        return payload.getJsonObject(service).encodePrettily();
    }
}
