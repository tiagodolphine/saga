package org.acme.kogito.poc.sagas.resources;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.smallrye.mutiny.Uni;
import org.acme.kogito.poc.sagas.model.orders.Order;
import org.acme.kogito.poc.sagas.model.orders.OrderStatus;
import org.acme.kogito.poc.sagas.services.OrderService;
import org.eclipse.microprofile.lra.annotation.Compensate;
import org.eclipse.microprofile.lra.annotation.Complete;
import org.eclipse.microprofile.lra.annotation.ws.rs.LRA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/payments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LRA(LRA.Type.SUPPORTS)
public class PaymentResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentResource.class);

    @Inject
    ObjectMapper mapper;

    @Inject
    OrderService service;

    @POST
    @LRA(value = LRA.Type.REQUIRES_NEW,
            cancelOn = Response.Status.INTERNAL_SERVER_ERROR,
            cancelOnFamily = Response.Status.Family.CLIENT_ERROR,
            timeLimit = 1, timeUnit = ChronoUnit.MINUTES
    )
    public Uni<Response> create(@Context UriInfo uriInfo, CloudEvent event) {
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
        return Uni.createFrom().item(event).onItem().transformToUni(e -> {
            if (e == null || e.getData() == null) {
                LOGGER.warn("received empty event");
                throw new BadRequestException();
            }
            try {
                LOGGER.info("received create order request");
                Order order = mapper.readValue(e.getData(), Order.class);
                return service.create(order)
                        .onItem()
                        .transform(id -> Response.created(uriBuilder.path(id.toString()).build()).build());
            } catch (IOException ex) {
                LOGGER.warn("unable to read order request from event", ex);
                throw new BadRequestException();
            }
        });
    }

    @Compensate
    @PUT
    @Path("/cancel")
    public Uni<Response> cancel(CloudEvent event) {
        return service.abort(UUID.fromString(event.getSubject())).onItem().transform(id -> Response.ok().build());
    }

    @Complete
    @PUT
    @Path("/complete")
    public Uni<Response> complete(CloudEvent event) {
        return service.complete(UUID.fromString(event.getSubject())).onItem().transform(id -> Response.ok().build());
    }

    @GET
    @Path("/{id}")
    @LRA(LRA.Type.NOT_SUPPORTED)
    public Uni<OrderStatus> get(@PathParam("id") UUID id) {
        return service.getStatus(id);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> abort(@PathParam("id") UUID id) {
        return service.abort(id).onItem().transform(orderStatus -> Response.accepted().build());
    }
}
