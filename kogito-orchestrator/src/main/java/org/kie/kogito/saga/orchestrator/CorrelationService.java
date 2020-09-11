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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;

import org.kie.kogito.saga.orchestrator.model.CorrelationKey;

@ApplicationScoped
public class CorrelationService {

    private final Map<String, CorrelationKey> emitted = new HashMap<>();

    public void add(String eventId, CorrelationKey key) {
        emitted.put(eventId, key);
    }

    public CorrelationKey remove(String eventId) {
        return emitted.remove(eventId);
    }

    public CorrelationKey get(String eventId) {
        return emitted.get(eventId);
    }

    public boolean contains(String eventId) {
        return emitted.containsKey(eventId);
    }

    public String getEventType(String eventId) {
        return Optional.of(emitted.get(eventId)).orElse(new CorrelationKey()).getEventType();
    }

    public String getProcessInstanceId(String eventId) {
        return Optional.of(emitted.get(eventId)).orElse(new CorrelationKey()).getProcessInstanceId();
    }

    public void deleteByProcessInstanceId(String processInstanceId) {
        emitted.entrySet().removeIf(entry -> entry.getValue().getProcessInstanceId().equals(processInstanceId));
    }

    public String getId(CorrelationKey correlationKey) {
        return emitted.entrySet().stream()
                .filter(entry -> entry.getValue().equals(correlationKey))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
}
