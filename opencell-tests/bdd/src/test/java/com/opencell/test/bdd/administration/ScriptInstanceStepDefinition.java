package com.opencell.test.bdd.administration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.GetScriptInstanceResponseDto;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;

import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;

public class ScriptInstanceStepDefinition implements En {

    public ScriptInstanceStepDefinition(BaseHook base) {
        Then("^The script instance is created$", () -> {

            base.getCode().ifPresent(code -> {
                if (base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/scriptInstance?scriptInstanceCode=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetScriptInstanceResponseDto actualEntity = response.extract().body()
                            .as(GetScriptInstanceResponseDto.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getScriptInstance());
                    assertEquals(code, actualEntity.getScriptInstance().getCode());
                }
            });

        });
    }
}