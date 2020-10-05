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

import java.util.Objects;

public class CorrelationKey {

    private String processInstanceId;
    private String eventType;
    private String processId;
    private String compensationEventType;
    private String lraId;

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public CorrelationKey setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
        return this;
    }

    public String getEventType() {
        return eventType;
    }

    public CorrelationKey setEventType(String eventType) {
        this.eventType = eventType;
        return this;
    }

    public String getProcessId() {
        return processId;
    }

    public CorrelationKey setProcessId(String processId) {
        this.processId = processId;
        return this;
    }

    public String getCompensationEventType() {
        return compensationEventType;
    }

    public CorrelationKey setCompensationEventType(String compensationEventType) {
        this.compensationEventType = compensationEventType;
        return this;
    }

    public CorrelationKey setLraId(String lraId) {
        this.lraId = lraId;
        return this;
    }

    public String getLraId() {
        return lraId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CorrelationKey)) return false;
        CorrelationKey that = (CorrelationKey) o;
        return Objects.equals(processInstanceId, that.processInstanceId)
                && Objects.equals(eventType, that.eventType)
                && Objects.equals(processId, that.processId)
                && Objects.equals(compensationEventType, that.compensationEventType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(processInstanceId, eventType);
    }
}
