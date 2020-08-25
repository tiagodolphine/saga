package org.kie.kogito;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static org.kie.kogito.ConfigurationHolder.toggle;

@Path("/services")
public class ManagementResource {

    private ConfigurationHolder configurationHolder;

    @Inject
    public ManagementResource(ConfigurationHolder configurationHolder) {
        this.configurationHolder = configurationHolder;
    }

    @PUT
    @Path("/payment/fail")
    public Boolean failPayment() {
        return toggle(configurationHolder.getPayment());
    }

    @PUT
    @Path("/stock/fail")
    public Boolean failStock() {
        return toggle(configurationHolder.getStock());
    }

    @PUT
    @Path("/shipping/fail")
    public Boolean failShipping() {
        return toggle(configurationHolder.getShipping());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigurationHolder publish() {
        return configurationHolder;
    }
}
