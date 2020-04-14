package com.opencell.test.bdd.offer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.tax.TaxClassResponseDto;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;

import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;

public class TaxClassStepDefinition implements En {

    public TaxClassStepDefinition(BaseHook base) {
        Then("^The tax class is created$", () -> {
            base.getCode().ifPresent(code -> {
                if (base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/taxClass?code=" + code,
                            "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    TaxClassResponseDto actualEntity = response.extract().body().as(TaxClassResponseDto.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getDto());
                    assertEquals(code, actualEntity.getDto().getCode());
                }
            });
        });
    }
}
