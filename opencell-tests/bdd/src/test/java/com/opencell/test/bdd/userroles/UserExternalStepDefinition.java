package com.opencell.test.bdd.userroles;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.GetProviderResponse;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;

import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;

public class UserExternalStepDefinition implements En {
    public UserExternalStepDefinition(BaseHook base) {
        Then("^The user external is created$", () -> {
            base.getCode().ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/provider", "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetProviderResponse actualEntity = response.extract().body().as(GetProviderResponse.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getProvider());
                    assertEquals(code, actualEntity.getProvider().getCode());
                }
            });
        });
    }
}
