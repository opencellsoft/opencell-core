package com.opencell.test.bdd.subscriptions;

import com.fasterxml.jackson.databind.JsonNode;
import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;
import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.billing.GetSubscriptionResponseDto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SubscriptionStepDefinition implements En {

    private BaseHook base;

    public SubscriptionStepDefinition(BaseHook base) {
        Then("^The subscription is created$", () -> {
            JsonNode jsonObject = base.getJsonObject();
            String code = jsonObject.get("code").asText();

            if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                ValidatableResponse response = RestApiUtils.get("/billing/subscription?subscriptionCode=" + code, "");
                response.assertThat().statusCode(HttpStatus.SC_OK);
                GetSubscriptionResponseDto actualEntity = response.extract().body().as(GetSubscriptionResponseDto.class);
                assertNotNull(actualEntity);
                assertNotNull(actualEntity.getSubscription());
                assertEquals(code, actualEntity.getSubscription().getCode());
            }
        });

    }
}
