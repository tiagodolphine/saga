package org.acme.kogito.poc.sagas.resources;

import javax.ws.rs.Path;

import org.acme.kogito.poc.sagas.model.payment.PaymentRequest;

@Path("/payment")
public class PaymentResource extends GenericResource<PaymentRequest> {

}