package com.opencell.test.bdd.settings;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;
import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.GetCountryIsoResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CountryIsoStepDefinition implements En {
    public CountryIsoStepDefinition(BaseHook base) {
        Then("^The country iso is created$", () -> {
           BaseHook baseb = base;
            base.getField("countryCode").ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/countryIso?countryCode=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetCountryIsoResponse actualEntity = response.extract().body().as(GetCountryIsoResponse.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getCountry());
                    assertEquals(code, actualEntity.getCountry().getCountryCode());
                }
            });
        });
    }
}
