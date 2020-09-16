package org.acme.kogito.poc.sagas.model.flight;

import java.util.Collection;
import javax.validation.constraints.NotNull;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.acme.kogito.poc.sagas.model.Person;
import org.acme.kogito.poc.sagas.model.Reservation;

@RegisterForReflection
public class FlightReservation extends Reservation {

    public static final String RESOURCE_NAME = "flights";

    @NotNull
    private Person passenger;
    @NotNull
    private Collection<Trip> itinerary;

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
