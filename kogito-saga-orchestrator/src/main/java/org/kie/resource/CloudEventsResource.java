package org.kie.resource;

import java.util.HashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.example.CompensationWithEventsModel;
import io.cloudevents.CloudEvent;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Path("/")
public class CloudEventsResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudEventsResource.class);

    @Inject
    @Named("compensationWithEvents")
    Process<? extends Model> process;

    @Inject
    Application application;

    public void trigger() {

    }

    @GET
    public String get() {
        return "Hello !!!";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(CloudEvent event) {

        try {
            String subject = event.getSubject();
            Response response = Response.ok().build();
            if (event.getType().equals("start")) {

                response = UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(), () -> {
                    ProcessInstance<? extends Model> instance = process.createInstance(null, new CompensationWithEventsModel());
                    instance.start();

                    System.out.println(instance.id());

                    return Response.ok("id " + instance.id()).build();
                });
            }

            //process event
            String processInstanceId = subject.split(":")[0];
            String workItemId = subject.split(":")[1];
            process.instances().findById(processInstanceId).ifPresent(i -> i.completeWorkItem(workItemId,
                                                                                              new HashMap<>()));

            LOGGER.info("Received cloud event {}", event);
            return response;
        } catch (Exception e) {
            LOGGER.error("Unable to process cloud event : {} ", event, e);
            throw new javax.ws.rs.BadRequestException();
        }
    }
}