package com.opencell.test.bdd.settings;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;
import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.GetProviderResponse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ProviderStepDefinition implements En {
    public ProviderStepDefinition(BaseHook base) {
        Then("^The provider is created$", () -> {
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
