package com.opencell.test.bdd.administration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.module.MeveoModuleDtoResponse;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;

import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;



public class ModuleStepDefinition implements En {

    public ModuleStepDefinition(BaseHook base) {
        Then("^The module is created$", () -> {

            base.getCode().ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/module?code=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    MeveoModuleDtoResponse actualEntity = response.extract().body().as(MeveoModuleDtoResponse.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getModule());
                    assertEquals(code, actualEntity.getModule().getCode());
                }
            });


        });
    }
}
