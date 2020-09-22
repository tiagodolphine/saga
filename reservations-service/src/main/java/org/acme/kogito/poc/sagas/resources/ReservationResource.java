package org.acme.kogito.poc.sagas.resources;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.annotation.PostConstruct;
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
import javax.ws.rs.core.UriInfo;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.smallrye.mutiny.Uni;
import org.acme.kogito.poc.sagas.model.RequestEvent;
import org.acme.kogito.poc.sagas.model.Reservation;
import org.acme.kogito.poc.sagas.model.car.CarReservation;
import org.acme.kogito.poc.sagas.model.flight.FlightReservation;
import org.acme.kogito.poc.sagas.model.hotel.HotelReservation;
import org.acme.kogito.poc.sagas.model.orders.OrderPayment;
import org.acme.kogito.poc.sagas.model.orders.Shipping;
import org.acme.kogito.poc.sagas.model.orders.Stock;
import org.acme.kogito.poc.sagas.model.payment.Payment;
import org.acme.kogito.poc.sagas.services.ReservationService;
import org.acme.kogito.poc.sagas.services.TrollService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ReservationResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationResource.class);

    @Context
    UriInfo uriInfo;

    private Map<String, ReservationService> services = new HashMap<>();

    @Inject
    ObjectMapper objectMapper;

    @Inject
    TrollService trollService;

    @PostConstruct
    void init() {
        services.put(CarReservation.RESOURCE_NAME, new ReservationService());
        services.put(FlightReservation.RESOURCE_NAME, new ReservationService());
        services.put(HotelReservation.RESOURCE_NAME, new ReservationService());
        services.put(Payment.RESOURCE_NAME, new ReservationService());
        //Orders
        services.put(OrderPayment.RESOURCE_NAME, new ReservationService());
        services.put(Stock.RESOURCE_NAME, new ReservationService());
        services.put(Shipping.RESOURCE_NAME, new ReservationService());
    }

    @GET
    @Path("/{resource}/reservations")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<Map<String, Reservation>> getAll(@PathParam("resource") String resource) {
        return CompletableFuture.supplyAsync(() -> services.get(resource).getAll());
    }

    @POST
    public Response receive(CloudEvent event) {
        final RequestEvent req = RequestEvent.fromType(event.getType());
        ReservationService service = services.get(req.getResource());
        if (req.isCancellation()) {
            service.cancel(event.getSubject());
            return Response.ok().build();
        }
        Uni<CloudEvent> uni = Uni.createFrom().item(event);
        try {
            Reservation resource = objectMapper.readValue(event.getData(), req.getReservation());
            Duration delay = trollService.withDelay(resource.getDelay());
            if(delay != null) {
                uni = uni.onItem().delayIt().by(delay);
            }
            Boolean success = uni.map(ev -> {
                try {
                    Boolean added = service.add(event.getSubject(), resource);
                    if(trollService.shouldFail(resource.shouldFail())) {
                        return false;
                    }
                    return added;
                } catch (IllegalArgumentException e) {
                    LOGGER.error("Unable to process reservation for " + req.getReservation(), e);
                    throw new javax.ws.rs.BadRequestException();
                }
            }).await().indefinitely();
            io.cloudevents.core.v1.CloudEventBuilder cloudEventBuilder = CloudEventBuilder.v1()
                    .withId(UUID.randomUUID().toString())
                    .withSubject(event.getSubject())
                    .withType(getResponseType(req.getReservationName(), success))
                    .withSource(URI.create(req.getResource()))
                    .withDataContentType(MediaType.APPLICATION_JSON);
            event.getExtensionNames()
                    .forEach(extName -> cloudEventBuilder.withExtension(extName, (String) event.getExtension(extName)));
            return Response.ok(cloudEventBuilder.build()).build();
        } catch (IOException e) {
            LOGGER.error("Unable to process reservation for " + req.getReservation(), e);
            throw new javax.ws.rs.BadRequestException();
        }
    }

    @GET
    @Path("/{resource}/reservations/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<Reservation> get(@PathParam("resource") String resource, @PathParam("id") String id) {
        return CompletableFuture.supplyAsync(() -> {
            Reservation r = services.get(resource).get(id);
            if (r == null) {
                throw new NotFoundException();
            }
            return r;
        });
    }

    @POST
    @Path("/{resource}/reservations/{id}/cancel")
    public CompletionStage<Response> cancel(@PathParam("resource") String resource, @PathParam("id") String id) {
        return CompletableFuture.supplyAsync(() -> {
            services.get(resource).cancel(id);
            return Response.accepted().build();
        });
    }

    @GET
    @Path("/{resource}/cancellations")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<String> getCancellations(@PathParam("resource") String resource) {
        return services.get(resource).getCancellations();
    }


    private String getResponseType(String resource, boolean success) {
        if (success) {
            return resource + "Success";
        }
        return resource + "Failed";
    }
}