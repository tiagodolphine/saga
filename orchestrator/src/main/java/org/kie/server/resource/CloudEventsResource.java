package org.kie.server.resource;

import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import io.swagger.annotations.Api;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.kie.api.runtime.query.QueryContext;
import org.springframework.beans.factory.annotation.Autowired;

@Api(value="Cloud Events")
@ApplicationScoped
@Path("/tiago")
public class CloudEventsResource {

    @Autowired
    private ProcessService processService;

    @Autowired
    private RuntimeDataService dataService;


    public void trigger() {

    }

    @GET
    @Path("/")
    public String get(){
        QueryContext queryContext = new QueryContext(0, 10);
        System.out.println(dataService.getProcesses(queryContext).stream().map(Object::toString).collect(Collectors.joining(",")));

        return "opaaaa";
    }

//    @PUT
//    @Path("/events")
//    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
//    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
//    public Response completeWorkItem(@javax.ws.rs.core.Context HttpHeaders headers,
//                                     @ApiParam(value = "container id that process instance belongs to", required = true, example = "evaluation_1.0.0-SNAPSHOT") @PathParam(CONTAINER_ID) String containerId,
//                                     @ApiParam(value = "identifier of the process instance that work item belongs to", required = true, example = "123") @PathParam(PROCESS_INST_ID) Long processInstanceId,
//                                     @ApiParam(value = "identifier of the work item to complete", required = true, example = "567") @PathParam("workItemId") Long workItemId,
//                                     @ApiParam(value = "optional outcome data give as map", required = false, examples = @Example(value = {
//                                             @ExampleProperty(mediaType = JSON, value = VAR_MAP_JSON),
//                                             @ExampleProperty(mediaType = XML, value = VAR_MAP_XML)})) String resultPayload) {
//
//        String type = MediaType.APPLICATION_JSON;
//        processServiceBase.completeWorkItem(containerId, processInstanceId, workItemId, resultPayload, type);
//        return Response.ok().build();
//    }
}
