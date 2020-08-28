package org.acme.kogito.poc.sagas.model.flight;

import java.time.ZonedDateTime;
import javax.validation.constraints.NotNull;

public class Trip {

    @NotNull
    private String departure;
    @NotNull
    private ZonedDateTime departureAt;
    @NotNull
    private String arrival;
    @NotNull
    private String flight;
    @NotNull
    private String seat;

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public ZonedDateTime getDepartureAt() {
        return departureAt;
    }

    public void setDepartureAt(ZonedDateTime departureAt) {
        this.departureAt = departureAt;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public String getFlight() {
        return flight;
    }

    public void setFlight(String flight) {
        this.flight = flight;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }
}
