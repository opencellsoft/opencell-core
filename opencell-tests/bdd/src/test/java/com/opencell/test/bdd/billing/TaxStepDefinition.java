package com.opencell.test.bdd.billing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.GetTaxResponse;
import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;

import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;

public class TaxStepDefinition implements En {

    public TaxStepDefinition(BaseHook base) {
        Then("^The tax is created$", () -> {

            base.getCode().ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/tax?taxCode=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetTaxResponse actualEntity = response.extract().body().as(GetTaxResponse.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getTax());
                    assertEquals(code, actualEntity.getTax().getCode());
                }
            });


        });
    }
}
