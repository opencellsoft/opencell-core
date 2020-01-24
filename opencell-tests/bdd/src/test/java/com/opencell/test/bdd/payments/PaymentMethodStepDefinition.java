package com.opencell.test.bdd.payments;

import com.opencell.test.bdd.commons.BaseHook;

import cucumber.api.java8.En;

public class PaymentMethodStepDefinition implements En {
    public PaymentMethodStepDefinition(BaseHook base) {
        Then("^The payment method is created$", () -> {
            // Nothing
        });
    }
}
