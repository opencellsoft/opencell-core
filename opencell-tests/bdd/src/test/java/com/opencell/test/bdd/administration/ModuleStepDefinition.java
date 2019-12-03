package com.opencell.test.bdd.administration;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;
import cucumber.api.PendingException;
import cucumber.api.java.en.Then;
import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.billing.AccountingCodeGetResponseDto;
import org.meveo.api.dto.response.module.MeveoModuleDtoResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;



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
