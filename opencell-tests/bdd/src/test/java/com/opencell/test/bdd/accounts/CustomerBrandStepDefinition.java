package com.opencell.test.bdd.accounts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.account.GetCustomerAccountResponseDto;
import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;

import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;

public class CustomerBrandStepDefinition implements En {

    public CustomerBrandStepDefinition(BaseHook base) {
        Then("^The customer brand is created$", () -> {
            // Missing a find/get API
        });
    }
}
