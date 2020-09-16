package org.acme.kogito.poc.sagas.services;

import java.time.Duration;
import java.util.Random;
import java.util.ResourceBundle;
import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class TrollService {

    @ConfigProperty(name = "fail.probability", defaultValue = "0.0")
    Double failProbability;

    @ConfigProperty(name = "delay.min", defaultValue = "1")
    int delayMin;

    @ConfigProperty(name = "delay.max", defaultValue = "10")
    int delayMax;

    private final Random r = new Random(System.currentTimeMillis());

    public boolean shouldFail() {
        return r.nextDouble() <= failProbability;
    }

    public boolean shouldFail(Boolean fail) {
        if(fail == null) {
            return shouldFail();
        }
        return fail;
    }

    public Duration withDelay() {
        if(delayMax == delayMin) {
            return Duration.ofSeconds(delayMax);
        }
        return Duration.ofSeconds(r.nextInt(delayMax - delayMin) + delayMin);
    }

    public Duration withDelay(Integer delay) {
        if(delay == null) {
            return withDelay();
        }
        if(delay == 0) {
            return null;
        }
        return Duration.ofSeconds(delay);
    }
}
