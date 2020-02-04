package com.opencell.test.bdd.invoicing;

import org.apache.http.HttpStatus;
import org.meveo.api.dto.ActionStatus;

import com.opencell.test.bdd.commons.ApiResponse;
import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;

import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;

public class BillingRunStepDefinition implements En {
    public BillingRunStepDefinition(BaseHook base) {
        Then("^The billing run is created$", () -> {
            if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                // API not up to spec
            }
        });
        When("^I cancell billing run$", () -> {
            base.getField("id").ifPresent(id -> {
                ValidatableResponse response = RestApiUtils.post("/billing/invoicing/cancelBillingRun", id);
                base.setResponse(new ApiResponse(response.extract().statusCode(),
                        response.extract().body().as(ActionStatus.class)));
            });
        });
        Then("^The entity is cancelled$", () -> {

        });
    }
}
