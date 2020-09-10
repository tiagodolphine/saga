package org.kie.kogito.saga.orchestrator;

import javax.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.Processes;

@QuarkusTest
class SimpleTripTest {

    @Inject
    Processes processes;

    @Test
    void testEventGateway() {
        Process<? extends Model> process = processes.processById("Compensation.SimpleTrip");
        ProcessInstance<? extends Model> instance = process.createInstance(process.createModel());

        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, instance.status());
    }
}
