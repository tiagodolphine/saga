package org.kie.kogito.saga.orchestrator;

import java.net.URI;
import java.time.temporal.ChronoUnit;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import io.narayana.lra.client.NarayanaLRAClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.lra.annotation.ws.rs.LRA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class LRAService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LRAService.class);

    public static final String COMPENSATE = "compensate";

    @Inject
    NarayanaLRAClient lraClient;

    @ConfigProperty(name = "internal.base.uri")
    String internalBaseUri;

    public String start(String processId) {
        URI lraURI = lraClient.startLRA(processId);
        return lraURI.toString();
    }

    public String addParticipant(String processId, String parentLra, String correlationId, Long timeLimit) {
        URI parentLraURI = null;
        if(parentLra != null) {
            parentLraURI = URI.create(parentLra);
        }
        URI childLra = lraClient.startLRA(parentLraURI, processId, 0L, ChronoUnit.SECONDS);
        lraClient.joinLRA(childLra, timeLimit,
                UriBuilder.fromPath(internalBaseUri).path(correlationId).path(COMPENSATE).build(), null,
                null, null, null, null, null);
        return childLra.toString();
    }

    @ActivateRequestContext
    public void close(String lraId) {
        lraClient.closeLRA(URI.create(lraId));
    }

    @ActivateRequestContext
    public void cancel(String lraId) {
        lraClient.cancelLRA(URI.create(lraId));
    }

}
