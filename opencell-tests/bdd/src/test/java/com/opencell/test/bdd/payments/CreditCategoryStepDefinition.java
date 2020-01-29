package com.opencell.test.bdd.payments;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;
import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.payment.AccountOperationResponseDto;
import org.meveo.api.dto.response.payment.CreditCategoryResponseDto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CreditCategoryStepDefinition implements En {
    public CreditCategoryStepDefinition(BaseHook base) {
        Then("^The credit category is created$", () -> {
            base.getCode().ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/payment/creditCategory?creditCategoryCode=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    CreditCategoryResponseDto actualEntity = response.extract().body().as(CreditCategoryResponseDto.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getCreditCategory());
                    assertEquals(code, actualEntity.getCreditCategory().getCode());
                }
            });
        });
    }
}
