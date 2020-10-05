package org.kie.kogito.saga.orchestrator.app;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import io.narayana.lra.client.internal.proxy.nonjaxrs.LRAParticipantRegistry;

@ApplicationScoped
public class ParticipantProducer {

    LRAParticipantRegistry lraParticipantRegistry = new LRAParticipantRegistry();

    @Produces
    public LRAParticipantRegistry getLraParticipantRegistry() {
        return lraParticipantRegistry;
    }
}
