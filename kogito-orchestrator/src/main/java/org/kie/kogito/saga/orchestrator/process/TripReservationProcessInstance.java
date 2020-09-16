package org.kie.kogito.saga.orchestrator.process;

import org.kie.api.runtime.process.ProcessRuntime;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.kogito.saga.orchestrator.model.SagaModel;

public class TripReservationProcessInstance extends org.kie.kogito.process.impl.AbstractProcessInstance<SagaModel> {

    public TripReservationProcessInstance(TripReservationProcess process, SagaModel value, ProcessRuntime processRuntime) {
        super(process, value, processRuntime);
    }

    public TripReservationProcessInstance(TripReservationProcess process, SagaModel value, String businessKey, ProcessRuntime processRuntime) {
        super(process, value, businessKey, processRuntime);
    }

    public TripReservationProcessInstance(TripReservationProcess process, SagaModel value, ProcessRuntime processRuntime, WorkflowProcessInstance wpi) {
        super(process, value, processRuntime, wpi);
    }

    public TripReservationProcessInstance(TripReservationProcess process, SagaModel value, WorkflowProcessInstance wpi) {
        super(process, value, wpi);
    }

    protected java.util.Map<String, Object> bind(SagaModel variables) {
        return variables.toMap();
    }

    protected void unbind(SagaModel variables, java.util.Map<String, Object> vmap) {
        variables.fromMap(this.id(), vmap);
    }
}
