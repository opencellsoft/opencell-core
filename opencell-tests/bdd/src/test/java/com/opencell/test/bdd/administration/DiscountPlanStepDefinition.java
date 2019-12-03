package com.opencell.test.bdd.administration;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;
import cucumber.api.PendingException;
import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.catalog.GetDiscountPlanResponseDto;
import org.meveo.api.dto.response.module.MeveoModuleDtoResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DiscountPlanStepDefinition implements En {
    public DiscountPlanStepDefinition(BaseHook base) {
        Then("^The discount Plan is created$", () -> {
            base.getCode().ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/catalog/discountPlan?discountPlanCode=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetDiscountPlanResponseDto actualEntity = response.extract().body().as(GetDiscountPlanResponseDto.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getDiscountPlanDto());
                    assertEquals(code, actualEntity.getDiscountPlanDto().getCode());
                }
            });
        });
    }
}
