package com.opencell.test.bdd.commons;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.meveo.api.dto.ActionStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opencell.test.utils.JsonParser;
import com.opencell.test.utils.RestApiUtils;

import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;

public class GenericStepDefinition implements En {

    private BaseHook base;

    public GenericStepDefinition(BaseHook base) {
        this.base = base;

        When("^I call the generic \"([^\"]*)\"$", (String api) -> {
            String bodyRequest = getBodyRequest();
            ValidatableResponse response = RestApiUtils.post(api, bodyRequest);
            ActionStatus actionStatus = (response.extract().jsonPath().get("actionStatus") == null
                    ? response.extract().body().as(ActionStatus.class)
                    : response.extract().jsonPath().getObject("actionStatus", ActionStatus.class));
            if (actionStatus.getMessage() == null)
                actionStatus.setMessage("");
            base.setResponse(new ApiResponse(response.extract().statusCode(), actionStatus));
            assertNotNull(base.getResponse());
            assertNotNull(base.getResponse().getActionStatus());
            assertNotNull(base.getResponse().getHttpStatusCode());
        });
        When("^I call the generic \"([^\"]*)\" \"([^\"]*)\"$", (String action, String api) -> {
            String bodyRequest = getBodyRequest();
            ValidatableResponse response = null;
            switch(action) {
            case "create":
            case "Create":
            case "Post":
            case "post":
            case "POST":
            case "CreateOrUpdate":
                response = RestApiUtils.post(api, bodyRequest);
                break;
            case "read":
            case "Read":
            case "Get":
            case "get":
            case "GET":
                response = RestApiUtils.get(api, bodyRequest);
                break;
            case "update":
            case "Update":
            case "Put":
            case "put":
            case "PUT":
                response = RestApiUtils.put(api, bodyRequest);
                break;
            case "delete":
            case "Delete":
            case "Del":
            case "del":
            case "DEL":
                response = RestApiUtils.delete(api + base.getCode().get(), bodyRequest);
                break;
            }

            base.setResponse(new ApiResponse(response.extract().statusCode(), null, response.extract().jsonPath()));
            base.setJsonresponse(response);
        });

        Then("^I get a generic response$", () -> {
            assertNotNull(base.getResponse());
            assertNotNull(base.getResponse().getJsonResponse());
            assertNotNull(base.getResponse().getHttpStatusCode());
        });
        And("^The field \"([^\"]*)\" is equal to \"([^\"]*)\"$", (String fieldName, String value) -> {
            String fieldValue = base.getResponse().getJsonResponse().get(fieldName).toString();
            assertEquals(value, fieldValue);
        });
        And("^The field \"([^\"]*)\" is greater than \"([^\"]*)\"$", (String fieldName, String value) -> {
            String fieldValue = base.getResponse().getJsonResponse().get(fieldName).toString();
            assertThat(Long.parseLong(fieldValue), greaterThan(Long.parseLong(value)));
        });
        And("^The field \"([^\"]*)\" is less than \"([^\"]*)\"$", (String fieldName, String value) -> {
            String fieldValue = base.getResponse().getJsonResponse().get(fieldName).toString();
            assertThat(Long.parseLong(fieldValue), lessThan(Long.parseLong(value)));
        });
        And("^The field \"([^\"]*)\" exists$", (String fieldName) -> {
            String fieldValue = base.getResponse().getJsonResponse().get(fieldName).toString();
            assertNotNull(fieldValue);
        });
    }

    private String getBodyRequest() throws JsonProcessingException {
        return JsonParser.writeValueAsString(base.getJsonObject());
    }
}
