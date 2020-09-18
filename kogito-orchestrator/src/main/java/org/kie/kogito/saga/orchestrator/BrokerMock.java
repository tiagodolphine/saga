package org.kie.kogito.saga.orchestrator;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.cloudevents.CloudEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/broker")
public class BrokerMock {

    Logger LOGGER = LoggerFactory.getLogger(BrokerMock.class);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response emit(CloudEvent event) {
        LOGGER.info("received post {}", event);
        return Response.ok().build();
    }
}
