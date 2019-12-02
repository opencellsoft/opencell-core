package com.opencell.test.bdd.administration;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;
import cucumber.api.PendingException;
import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.GetBillingCycleResponse;
import org.meveo.api.dto.response.module.MeveoModuleDtoResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BillingCycleStepDefinition implements En {
    public BillingCycleStepDefinition(BaseHook base) {
        Then("^The billing cycle is created$", () -> {
            base.getCode().ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/billingCycle?billingCycleCode=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetBillingCycleResponse actualEntity = response.extract().body().as(GetBillingCycleResponse.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getBillingCycle());
                    assertEquals(code, actualEntity.getBillingCycle().getCode());
                }
            });
        });
    }
}
