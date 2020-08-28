package org.acme.kogito.poc.sagas.resources;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.acme.kogito.poc.sagas.model.Reservation;
import org.acme.kogito.poc.sagas.services.ReservationService;

public abstract class GenericResource<T extends Reservation> {

    @Context
    private UriInfo uriInfo;

    @Inject
    private ReservationService<T> service;

    @GET
    @Path("/reservations")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<Collection<T>> list() {
        return CompletableFuture.supplyAsync(() -> service.list());
    }

    @POST
    @Path("/reservations")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(T reservation) {
        service.add(reservation);
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder().path(reservation.getId());
        return Response.created(uriBuilder.build()).build();
    }

    @GET
    @Path("/reservations/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<T> get(@PathParam("id") String id) {
        return CompletableFuture.supplyAsync(() -> {
            T t = service.get(id);
            if (t == null) {
                throw new NotFoundException();
            }
            return t;
        });
    }

    @POST
    @Path("/reservations/{id}/cancel")
    public CompletionStage<Response> cancel(@PathParam("id") String id) {
        return CompletableFuture.supplyAsync(() -> {
            service.cancel(id);
            return Response.accepted().build();
        });
    }

    @GET
    @Path("/cancellations")
    public Collection<String> getCancellations() {
        return service.getCancellations();
    }
}