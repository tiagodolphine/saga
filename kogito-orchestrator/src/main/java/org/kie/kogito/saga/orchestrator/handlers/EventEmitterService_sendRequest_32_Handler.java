package org.kie.kogito.saga.orchestrator.handlers;

@javax.enterprise.context.ApplicationScoped()
public class EventEmitterService_sendRequest_32_Handler implements org.kie.api.runtime.process.WorkItemHandler {

    @javax.inject.Inject()
    org.kie.kogito.saga.orchestrator.EventEmitterService service;

    public void executeWorkItem(org.kie.api.runtime.process.WorkItem workItem, org.kie.api.runtime.process.WorkItemManager workItemManager) {
        service.sendRequest((String) workItem.getParameter("eventType"), (io.vertx.core.json.JsonObject) workItem.getParameter("payload"), (String) workItem.getParameter("processInstanceId"));
        workItemManager.completeWorkItem(workItem.getId(), null);
    }

    public void abortWorkItem(org.kie.api.runtime.process.WorkItem workItem, org.kie.api.runtime.process.WorkItemManager workItemManager) {
    }

    public String getName() {
        return "org.kie.kogito.saga.orchestrator.EventEmitterService_sendRequest_32_Handler";
    }
}
