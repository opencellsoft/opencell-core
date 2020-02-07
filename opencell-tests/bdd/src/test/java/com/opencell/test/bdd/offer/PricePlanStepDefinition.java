package com.opencell.test.bdd.offer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.catalog.GetPricePlanResponseDto;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;

import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;

public class PricePlanStepDefinition implements En {

    public PricePlanStepDefinition(BaseHook base) {
        Then("^The price plan is created$", () -> {
            base.getCode().ifPresent(code -> {
                if (base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get(
                            "/catalog/pricePlan?pricePlanCode=" + code,
                            "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetPricePlanResponseDto actualEntity = response.extract().body().as(GetPricePlanResponseDto.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getPricePlan());
                    assertEquals(code, actualEntity.getPricePlan().getCode());
                }
            });
        });
    }
}
