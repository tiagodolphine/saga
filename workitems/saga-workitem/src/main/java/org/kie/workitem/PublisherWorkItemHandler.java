package org.kie.workitem;

import java.util.Collections;

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

@Wid(widfile = "CustomDefinitions.wid", name = "PublisherWorkItemHandler",
        displayName = "PublisherWorkItemHandler",
        defaultHandler = "mvel: new org.jbpm.contrib.PublisherWorkItemHandler()",
        documentation = "event-workitem/index.html",
        category = "event-workitem",
        icon = "CustomDefinitions.png",
        parameters = {
                @WidParameter(name = "publishType", required = false)
        },
        results = {
                @WidResult(name = "Result")
        },
        mavenDepends = {
                @WidMavenDepends(group = "org.jbpm.contrib", artifact = "event-workitem", version = "7.53.0.Final")
        },
        serviceInfo = @WidService(category = "event-workitem", description = "Event work item",
                keywords = "",
                action = @WidAction(title = "PublisherWorkItemHandler Name")
        )
)
public class PublisherWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {

    public void executeWorkItem(WorkItem workItem,
                                WorkItemManager manager) {
        try {
            RequiredParameterValidator.validate(this.getClass(),
                                                workItem);
            //debugging parameters
            String requestTopic = (String) workItem.getParameter("publishType");
            System.out.println("PublisherWorkItemHandler call....." + requestTopic);
            manager.completeWorkItem(workItem.getId(), Collections.emptyMap());
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