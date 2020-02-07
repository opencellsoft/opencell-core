package com.opencell.test.bdd.offer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.catalog.GetRecurringChargeTemplateResponseDto;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;

import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;

public class ReccuringChargeStepDefinition implements En {

    public ReccuringChargeStepDefinition(BaseHook base) {
        Then("^The recurring charge is created$", () -> {
            base.getCode().ifPresent(code -> {
                if (base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get(
                            "/catalog/recurringChargeTemplate?recurringChargeTemplateCode=" + code,
                            "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetRecurringChargeTemplateResponseDto actualEntity = response.extract().body()
                            .as(GetRecurringChargeTemplateResponseDto.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getRecurringChargeTemplate());
                    assertEquals(code, actualEntity.getRecurringChargeTemplate().getCode());
                }
            });
        });
    }
}
