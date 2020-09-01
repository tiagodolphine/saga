package org.acme.kogito.poc.sagas.resources;

import java.io.IOException;
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
import org.acme.kogito.poc.sagas.model.RequestEvent;
import org.acme.kogito.poc.sagas.model.Reservation;
import org.acme.kogito.poc.sagas.model.car.CarReservation;
import org.acme.kogito.poc.sagas.model.flight.FlightReservation;
import org.acme.kogito.poc.sagas.model.hotel.HotelReservation;
import org.acme.kogito.poc.sagas.model.payment.PaymentRequest;
import org.acme.kogito.poc.sagas.services.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CloudEventResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudEventResource.class);

    @Context
    UriInfo uriInfo;

    ReservationService carReservationService = new ReservationService();
    ReservationService flightReservationService = new ReservationService();
    ReservationService hotelReservationService = new ReservationService();
    ReservationService paymentRequestService = new ReservationService();

    private Map<String, ReservationService> services = new HashMap<>();

    @Inject
    ObjectMapper objectMapper;

    @PostConstruct
    void init() {
        services.put(CarReservation.RESOURCE_NAME, carReservationService);
        services.put(FlightReservation.RESOURCE_NAME, flightReservationService);
        services.put(HotelReservation.RESOURCE_NAME, hotelReservationService);
        services.put(PaymentRequest.RESOURCE_NAME, paymentRequestService);
    }

    @GET
    @Path("/{resource}/reservations")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<Collection<Reservation>> list(@PathParam("resource") String resource) {
        return CompletableFuture.supplyAsync(() -> services.get(resource).list());
    }

    @POST
    public Response create(CloudEvent event) {
        RequestEvent req = RequestEvent.fromType(event.getType());
        ReservationService service = services.get(req.getResource());
        if (req.isCancellation()) {
            service.cancel(event.getSubject());
            return Response.ok().build();
        }
        Boolean success;
        try {
            Reservation resource = objectMapper.readValue(event.getData(), req.getReservation());
            success = service.add(event.getSubject(), resource);
        } catch (IOException e) {
            LOGGER.error("Unable to process reservation for " + req.getReservation(), e);
            throw new javax.ws.rs.BadRequestException();
        }

        return Response.ok(CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withSubject(event.getSubject())
                .withType(getResponseType(req.getReservationName(), success))
                .withSource(uriInfo.getAbsolutePath())
                .build()).build();
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