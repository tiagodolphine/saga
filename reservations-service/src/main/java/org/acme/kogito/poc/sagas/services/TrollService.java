package org.acme.kogito.poc.sagas.services;

import java.util.Random;
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

    public int withDelay() {
        if(delayMax == delayMin) {
            return delayMax;
        }
        return r.nextInt(delayMax - delayMin) + delayMin;
    }
}
