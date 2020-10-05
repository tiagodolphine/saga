package org.acme.kogito.poc.sagas.app;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import io.narayana.lra.client.internal.proxy.nonjaxrs.LRAParticipantRegistry;

@ApplicationScoped
public class SagaApp {

    @Produces
    LRAParticipantRegistry getLRAParticipantRegistry() {
        return new LRAParticipantRegistry();
    }
}
