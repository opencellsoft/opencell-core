package com.opencell.test.bdd.customers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.account.GetCustomerAccountResponseDto;
import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;

import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;

public class CustomerAccountStepDefinition implements En {

    public CustomerAccountStepDefinition(BaseHook base) {
        Then("^The customer account is created$", () -> {

            base.getCode().ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/account/customerAccount?customerAccountCode=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetCustomerAccountResponseDto actualEntity = response.extract().body().as(GetCustomerAccountResponseDto.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getCustomerAccount());
                    assertEquals(code, actualEntity.getCustomerAccount().getCode());
                }
            });
        });
    }
}
