package com.opencell.test.bdd.settings;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;
import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.GetCurrenciesIsoResponse;
import org.meveo.api.dto.response.GetCurrencyIsoResponse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CurrencyIsoStepDefinition implements En {
    public CurrencyIsoStepDefinition(BaseHook base) {
        Then("^The currency iso is created$", () -> {
            base.getField("code").ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/currencyIso?currencyCode=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetCurrenciesIsoResponse actualEntity = response.extract().body().as(GetCurrenciesIsoResponse.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getCurrencies());
                }
            });
        });
    }
}
