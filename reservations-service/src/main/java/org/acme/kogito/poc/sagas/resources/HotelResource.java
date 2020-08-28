package org.acme.kogito.poc.sagas.resources;

import javax.ws.rs.Path;

import org.acme.kogito.poc.sagas.model.hotel.HotelReservation;

@Path("/hotel")
public class HotelResource extends GenericResource<HotelReservation> {

}