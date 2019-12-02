package com.opencell.test.bdd.offer;

import com.fasterxml.jackson.databind.JsonNode;
import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;
import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.catalog.GetOfferTemplateResponseDto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OfferStepDefinition implements En {

    private BaseHook base;

    public OfferStepDefinition(BaseHook base) {
        Then("^The offer is created$", () -> {
            JsonNode jsonObject = base.getJsonObject();
            String code = jsonObject.get("code").asText();

            if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                ValidatableResponse response = RestApiUtils.get("/catalog/offerTemplate?offerTemplateCode=" + code, "");
                response.assertThat().statusCode(HttpStatus.SC_OK);
                GetOfferTemplateResponseDto actualEntity = response.extract().body().as(GetOfferTemplateResponseDto.class);
                assertNotNull(actualEntity);
                assertNotNull(actualEntity.getOfferTemplate());
                assertEquals(code, actualEntity.getOfferTemplate().getCode());
            }
        });
    }
}
