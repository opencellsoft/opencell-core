package com.opencell.test.bdd.accounts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.account.GetUserAccountResponseDto;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;

import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;

public class UserAccountStepDefinition implements En {

    public UserAccountStepDefinition(BaseHook base) {
        Then("^The user account is created$", () -> {

            base.getCode().ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/account/userAccount?userAccountCode=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetUserAccountResponseDto actualEntity = response.extract().body()
                            .as(GetUserAccountResponseDto.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getUserAccount());
                    assertEquals(code, actualEntity.getUserAccount().getCode());
                }
            });
        });
    }
}
