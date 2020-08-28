package org.acme.kogito.poc.sagas.resources;

import javax.ws.rs.Path;

import org.acme.kogito.poc.sagas.model.car.CarReservation;

@Path("/cars")
public class CarResource extends GenericResource<CarReservation> {

}