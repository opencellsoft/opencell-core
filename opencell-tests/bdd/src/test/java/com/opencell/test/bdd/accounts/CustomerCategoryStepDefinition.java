package com.opencell.test.bdd.accounts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.account.GetCustomerCategoryResponseDto;
import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;

import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;

public class CustomerCategoryStepDefinition implements En {

    public CustomerCategoryStepDefinition(BaseHook base) {
        Then("^The customer category is created$", () -> {

            base.getCode().ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/account/customer/category/" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetCustomerCategoryResponseDto actualEntity = response.extract().body().as(GetCustomerCategoryResponseDto.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getCustomerCategory());
                    assertEquals(code, actualEntity.getCustomerCategory().getCode());
                }
            });
        });
    }
}
