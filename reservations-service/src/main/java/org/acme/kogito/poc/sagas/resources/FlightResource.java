package org.acme.kogito.poc.sagas.resources;

import javax.ws.rs.Path;

import org.acme.kogito.poc.sagas.model.flight.FlightReservation;

@Path("/flight")
public class FlightResource extends GenericResource<FlightReservation> {

}