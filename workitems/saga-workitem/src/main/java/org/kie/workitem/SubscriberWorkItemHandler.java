package org.kie.workitem;

import java.util.Collections;
import java.util.HashMap;

import org.drools.core.process.instance.impl.WorkItemImpl;
import org.jbpm.process.workitem.core.AbstractLogOrThrowWorkItemHandler;
import org.jbpm.process.workitem.core.util.RequiredParameterValidator;
import org.jbpm.process.workitem.core.util.Wid;
import org.jbpm.process.workitem.core.util.WidMavenDepends;
import org.jbpm.process.workitem.core.util.WidParameter;
import org.jbpm.process.workitem.core.util.WidResult;
import org.jbpm.process.workitem.core.util.service.WidAction;
import org.jbpm.process.workitem.core.util.service.WidService;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

@Wid(widfile = "CustomDefinitions.wid", name = "SubscriberWorkItemHandler",
        displayName = "SubscriberWorkItemHandler",
        defaultHandler = "mvel: new org.jbpm.contrib.SubscriberWorkItemHandler()",
        documentation = "event-workitem/index.html",
        category = "event-workitem",
        icon = "CustomDefinitions.png",
        parameters = {
                @WidParameter(name = "errorType", required = false),
                @WidParameter(name = "successType", required = false),
        },
        results = {
                @WidResult(name = "Result")
        },
        mavenDepends = {
                @WidMavenDepends(group = "org.jbpm.contrib", artifact = "event-workitem", version = "7.53.0.Final")
        },
        serviceInfo = @WidService(category = "event-workitem", description = "Event work item",
                keywords = "",
                action = @WidAction(title = "SubscriberWorkItemHandler Name")
        )
)
public class SubscriberWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {

    public void executeWorkItem(WorkItem workItem,
                                WorkItemManager manager) {
        try {
            RequiredParameterValidator.validate(this.getClass(),
                                                workItem);
            //debugging parameters
            String errorType = (String) workItem.getParameter("errorType");
            String successType = (String) workItem.getParameter("successType");
            System.out.println("SubscriberWorkItemHandler call..... " + successType + " " + errorType);
            manager.completeWorkItem(workItem.getId(), Collections.singletonMap("Result", Collections.singleton(
                    "result test output")));
        } catch (Throwable cause) {
            handleException(cause);
        }
    }

    @Override
    public void abortWorkItem(WorkItem workItem,
                              WorkItemManager manager) {
    }

    private RuntimeManager getRuntimeManager(WorkItem workItem) {
        String deploymentId = ((WorkItemImpl) workItem).getDeploymentId();
        return RuntimeManagerRegistry.get().getManager(deploymentId);
    }

    private RuntimeEngine getRuntimeEngine(WorkItem workItem, RuntimeManager manager) {
        Long processInstanceId = workItem.getProcessInstanceId();
        if (manager != null) {
            return manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
        }
        return null;
    }
}