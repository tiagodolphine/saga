package org.kie.server.resource;

import java.io.IOException;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.swagger.annotations.Api;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.kie.api.runtime.query.QueryContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Api(value = "Cloud Events")
@ApplicationScoped
@Path("/")
public class CloudEventsResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudEventsResource.class);

    public void trigger() {

    }

    @GET
    public String get() {
        return "Hello !!!";
    }

    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(CloudEvent event) {
        Boolean success;
        try {
            //process event

            LOGGER.info("Received cloud event {}", event);
        } catch (Exception e) {
            LOGGER.error("Unable to process cloud event : {} ", event, e);
            throw new javax.ws.rs.BadRequestException();
        }
        return Response.ok().build();
    }
}