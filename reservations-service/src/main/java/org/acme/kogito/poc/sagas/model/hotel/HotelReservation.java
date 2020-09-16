package org.acme.kogito.poc.sagas.model.hotel;

import java.time.ZonedDateTime;
import javax.validation.constraints.NotNull;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.acme.kogito.poc.sagas.model.Person;
import org.acme.kogito.poc.sagas.model.Reservation;

@RegisterForReflection
public class HotelReservation extends Reservation {

    public static final String RESOURCE_NAME = "hotels";

    @NotNull
    private Person guest;
    @NotNull
    private String ref;
    @NotNull
    private ZonedDateTime start;
    @NotNull
    private ZonedDateTime end;
    @NotNull
    private RoomType type;

    public Person getGuest() {
        return guest;
    }

    public void setGuest(Person guest) {
        this.guest = guest;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public void setEnd(ZonedDateTime end) {
        this.end = end;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

}
