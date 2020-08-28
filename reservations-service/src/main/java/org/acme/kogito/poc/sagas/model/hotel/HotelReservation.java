package org.acme.kogito.poc.sagas.model.hotel;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.acme.kogito.poc.sagas.model.Person;
import org.acme.kogito.poc.sagas.model.Reservation;

public class HotelReservation implements Reservation {

    @JsonProperty(index = 0)
    @NotNull
    private String id;
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

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

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
