package org.acme.kogito.poc.sagas.model.orders;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class OrderPayment extends BaseOrderModel {

    public static final String RESOURCE_NAME = "order-payments";
}
