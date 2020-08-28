package org.acme.kogito.poc.sagas.model.flight;

import java.util.Collection;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.acme.kogito.poc.sagas.model.Person;
import org.acme.kogito.poc.sagas.model.Reservation;

public class FlightReservation implements Reservation {

    @JsonProperty(index = 0)
    private String id;
    @NotNull
    private Person passenger;
    @NotNull
    private Collection<Trip> itinerary;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public Person getPassenger() {
        return passenger;
    }

    public void setPassenger(Person passenger) {
        this.passenger = passenger;
    }

    public Collection<Trip> getItinerary() {
        return itinerary;
    }

    public void setItinerary(Collection<Trip> itinerary) {
        this.itinerary = itinerary;
    }
}
