package org.acme.kogito.poc.sagas.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;

import org.acme.kogito.poc.sagas.model.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ReservationService<T extends Reservation> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationService.class);

    private Map<String, T> reservations = new HashMap<>();
    private Set<String> cancellations = new HashSet<>();

    public Collection<T> list() {
        return reservations.values();
    }

    public T get(String id) {
        return reservations.get(id);
    }

    public Boolean add(T reservation) {
        if(reservations.containsKey(reservation.getId())) {
            LOGGER.info("Ignoring repeated reservation: {}", reservation.getId());
            return false;
        }
        if (cancellations.contains(reservation.getId())){
            LOGGER.info("Ignoring cancelled reservation: {}", reservation.getId());
            return false;
        }
        reservations.put(reservation.getId(), reservation);
        return true;
    }

    public Boolean cancel(String id) {
        if (!cancellations.contains(id)) {
            cancellations.add(id);
            return true;
        }
        return false;
    }

    public Collection<String> getCancellations() {
        return cancellations.stream()
                .collect(Collectors.toList());
    }
}
