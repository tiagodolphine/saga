/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    abstract Emitter<String> errorEmitter();

    abstract Emitter<String> successEmitter();

    abstract String serviceName();

    abstract AtomicBoolean isFailed();

    abstract Long delay();

    void handleRequest(String input) {
        Uni.createFrom().item(input)
                .onItem().delayIt().by(Duration.ofSeconds(delay()))
                .invoke(i -> logger.info("Published {} {}", serviceName(), i))
                .subscribe()
                .with(i -> Optional.of(isFailed())
                        .filter(AtomicBoolean::get)
                        .map(s -> errorEmitter())
                        .orElseGet(() -> successEmitter())
                        .send(i));
    }
}
