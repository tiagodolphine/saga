package org.acme.kogito.poc.sagas.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.acme.kogito.poc.sagas.model.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReservationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationService.class);

    private Map<String, Reservation> reservations = new HashMap<>();
    private Set<String> cancellations = new HashSet<>();

    public Map<String, Reservation> getAll() {
        return reservations;
    }

    public Reservation get(String id) {
        return reservations.get(id);
    }

    public Boolean add(String id, Reservation reservation) {
        if (reservations.containsKey(id)) {
            LOGGER.info("Ignoring repeated reservation: {}", id);
            return false;
        }
        if (cancellations.contains(id)) {
            LOGGER.info("Ignoring cancelled reservation: {}", id);
            return false;
        }
        LOGGER.info("Received reservation: {}", id);
        reservations.put(id, reservation);
        return true;
    }

    public Boolean cancel(String id) {
        if (!cancellations.contains(id)) {
            LOGGER.info("Cancelled reservation: {}", id);
            cancellations.add(id);
            return true;
        }
        LOGGER.info("Ignoring already cancelled reservation: {}", id);
        return false;
    }

    public Collection<String> getCancellations() {
        return cancellations.stream()
                .collect(Collectors.toList());
    }
}
