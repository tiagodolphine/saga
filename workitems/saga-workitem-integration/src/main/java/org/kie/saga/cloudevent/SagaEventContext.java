package org.kie.saga.cloudevent;

public class SagaEventContext {

    private String id;
    //used to check the subscribers
    private String type;

    //used to complete the work item when the event is received
    private String workItemId;
    private String processInstanceId;
    private String containerId;//deploymentID

    public SagaEventContext() {
    }

    public SagaEventContext(String id, String type, String workItemId, String processInstanceId, String containerId) {
        this.id = id;
        this.type = type;
        this.workItemId = workItemId;
        this.processInstanceId = processInstanceId;
        this.containerId = containerId;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getWorkItemId() {
        return workItemId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public String getContainerId() {
        return containerId;
    }
}
