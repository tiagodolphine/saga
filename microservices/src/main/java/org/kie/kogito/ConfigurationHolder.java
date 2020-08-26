/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Singleton;

@Singleton
public class ConfigurationHolder {

    private static final Long DEFAULT_DELAY = 10L;

    private AtomicBoolean payment = new AtomicBoolean();
    private AtomicBoolean stock = new AtomicBoolean();
    private AtomicBoolean shipping = new AtomicBoolean();
    private AtomicLong paymentDelay = new AtomicLong(DEFAULT_DELAY);
    private AtomicLong stockDelay = new AtomicLong(DEFAULT_DELAY);
    private AtomicLong shippingDelay = new AtomicLong(DEFAULT_DELAY);

    public AtomicBoolean getPayment() {
        return payment;
    }

    public AtomicBoolean getStock() {
        return stock;
    }

    public AtomicBoolean getShipping() {
        return shipping;
    }

    public static Boolean toggle(AtomicBoolean item) {
        item.set(!item.get());
        return item.get();
    }

    public AtomicLong getPaymentDelay() {
        return paymentDelay;
    }

    public AtomicLong getStockDelay() {
        return stockDelay;
    }

    public AtomicLong getShippingDelay() {
        return shippingDelay;
    }
}
