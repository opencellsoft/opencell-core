package com.opencell.test.bdd.accounts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.account.ProviderContactResponseDto;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;

import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;

public class ProviderContactStepDefinition implements En {

    public ProviderContactStepDefinition(BaseHook base) {
        Then("^The provider contact is created$", () -> {

            base.getCode().ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/account/providerContact?providerContactCode=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    ProviderContactResponseDto actualEntity = response.extract().body().as(ProviderContactResponseDto.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getProviderContact());
                    assertEquals(code, actualEntity.getProviderContact().getCode());
                }
            });
        });
    }
}
