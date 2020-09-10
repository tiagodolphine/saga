package org.kie.resource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.process.instance.ProcessInstance;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;

@ApplicationScoped
public class Publisher {

    @Inject
    @Named("compensationWithEvents")
    Process<? extends Model> process;

    @Inject
    Application application;

    public void publish(ProcessInstance processInstance){
        System.out.println("publish event " + processInstance);
    }

}
