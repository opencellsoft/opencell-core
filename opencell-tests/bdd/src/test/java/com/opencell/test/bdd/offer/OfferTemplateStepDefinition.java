package com.opencell.test.bdd.offer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.catalog.GetOfferTemplateResponseDto;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;

import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;

public class OfferTemplateStepDefinition implements En {

    public OfferTemplateStepDefinition(BaseHook base) {
        Then("^The offer template is created$", () -> {
            base.getCode().ifPresent(code -> {
                if (base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/catalog/offerTemplate?offerTemplateCode=" + code,
                            "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetOfferTemplateResponseDto actualEntity = response.extract().body()
                            .as(GetOfferTemplateResponseDto.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getOfferTemplate());
                    assertEquals(code, actualEntity.getOfferTemplate().getCode());
                }
            });
        });
    }
}
