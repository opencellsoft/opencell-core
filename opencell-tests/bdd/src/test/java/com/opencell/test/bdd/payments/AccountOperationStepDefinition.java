package com.opencell.test.bdd.payments;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;
import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.payment.AccountOperationResponseDto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AccountOperationStepDefinition implements En {
    public AccountOperationStepDefinition(BaseHook base) {
        Then("^The account operation is created$", () -> {
            base.getCode().ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/accountOperation?id=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    AccountOperationResponseDto actualEntity = response.extract().body().as(AccountOperationResponseDto.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getAccountOperation());
                    assertEquals(code, actualEntity.getAccountOperation().getCode());
                }
            });
        });
    }
}
