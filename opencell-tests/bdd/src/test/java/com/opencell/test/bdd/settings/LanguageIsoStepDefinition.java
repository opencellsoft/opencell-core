package com.opencell.test.bdd.settings;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;
import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.GetLanguagesIsoResponse;

import static org.junit.Assert.assertNotNull;

public class LanguageIsoStepDefinition implements En {
    public LanguageIsoStepDefinition(BaseHook base) {
        Then("^The language iso is created$", () -> {
            base.getField("code").ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/languageIso?languageCode=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetLanguagesIsoResponse actualEntity = response.extract().body().as(GetLanguagesIsoResponse.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getLanguages());
                }
            });
        });
    }
}
