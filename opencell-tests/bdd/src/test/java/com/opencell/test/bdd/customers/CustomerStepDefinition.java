package com.opencell.test.bdd.customers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.account.GetCustomerResponseDto;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;

import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;

public class CustomerStepDefinition implements En {

    public CustomerStepDefinition(BaseHook base) {
        Then("^The customer is created$", () -> {

            base.getCode().ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/account/customer?customerCode=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetCustomerResponseDto actualEntity = response.extract().body().as(GetCustomerResponseDto.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getCustomer());
                    assertEquals(code, actualEntity.getCustomer().getCode());
                }
            });
        });
    }
}
