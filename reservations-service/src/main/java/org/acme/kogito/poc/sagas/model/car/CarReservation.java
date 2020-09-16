package org.acme.kogito.poc.sagas.model.car;

import java.time.ZonedDateTime;
import javax.validation.constraints.NotNull;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.acme.kogito.poc.sagas.model.Person;
import org.acme.kogito.poc.sagas.model.Reservation;

@RegisterForReflection
public class CarReservation extends Reservation {

    public static final String RESOURCE_NAME = "cars";

    @NotNull
    private Person driver;
    @NotNull
    private ZonedDateTime start;
    @NotNull
    private ZonedDateTime end;
    @NotNull
    private String license;
    @NotNull
    private CarType type;
    @NotNull
    private String startOffice;
    private String returnOffice;

    public Person getDriver() {
        return driver;
    }

    public void setDriver(Person driver) {
        this.driver = driver;
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

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public CarType getType() {
        return type;
    }

    public void setType(CarType type) {
        this.type = type;
    }

    public String getStartOffice() {
        return startOffice;
    }

    public void setStartOffice(String startOffice) {
        this.startOffice = startOffice;
    }

    public String getReturnOffice() {
        return returnOffice;
    }

    public void setReturnOffice(String returnOffice) {
        this.returnOffice = returnOffice;
    }

}
