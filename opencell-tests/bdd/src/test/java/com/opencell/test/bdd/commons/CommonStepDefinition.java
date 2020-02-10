package com.opencell.test.bdd.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.meveo.api.dto.ActionStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.opencell.test.utils.JsonParser;
import com.opencell.test.utils.RestApiUtils;

import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;

public class CommonStepDefinition implements En {

    private BaseHook base;

    public CommonStepDefinition(BaseHook base) {
        this.base = base;
        Given("^The entity has the following information \"([^\"]*)\" as \"([^\"]*)\"$", (String filename, String dto) -> {
            Class klazz = base.getEntityClass(dto).get();
                    setJsonObject(filename);
                    assertTrue(base.getEntityDto() != null || base.getJsonObject() != null);
                });
        Given("^The entity has the following information \"([^\"]*)\"$", (String filename) -> {
            setJsonObject(filename);
            assertTrue(base.getEntityDto() != null || base.getJsonObject() != null);
        });
        When("^I call the \"([^\"]*)\"$", (String api) -> {
            String bodyRequest = getBodyRequest();
            ValidatableResponse response = RestApiUtils.post(api, bodyRequest);
            ActionStatus actionStatus = (response.extract().jsonPath().get("actionStatus") == null
                    ? response.extract().body().as(ActionStatus.class)
                    : response.extract().jsonPath().getObject("actionStatus", ActionStatus.class));
            if (actionStatus.getMessage() == null)
                actionStatus.setMessage("");
            base.setResponse(
                    new ApiResponse(response.extract().statusCode(), actionStatus, response.extract().jsonPath()));
            assertNotNull(base.getResponse());
            assertNotNull(base.getResponse().getActionStatus());
            assertNotNull(base.getResponse().getHttpStatusCode());
        });
        When("^I call the \"([^\"]*)\" \"([^\"]*)\"$", (String action, String api) -> {
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
            ActionStatus actionStatus = null;
            try {
                actionStatus = (response.extract().jsonPath().get("actionStatus") == null
                        ? response.extract().body().as(ActionStatus.class)
                        : response.extract().jsonPath().getObject("actionStatus", ActionStatus.class));
                if (actionStatus.getMessage() == null)
                    actionStatus.setMessage("");
            } catch (Exception jpe) {
                System.out.println("DEBUG - Error parsing");
                System.out.println("DEBUG - Cannot parse: " + response.extract().body().asString());
            }
            base.setResponse(
                    new ApiResponse(response.extract().statusCode(), actionStatus, response.extract().jsonPath()));
            base.setJsonresponse(response);
            assertNotNull(base.getResponse());
            assertNotNull(base.getResponse().getActionStatus());
            assertNotNull(base.getResponse().getHttpStatusCode());
        });
        When("^I call the \"([^\"]*)\" \"([^\"]*)\" with identifier \"([^\"]*)\"$",
                (String action, String api, String field) -> {
                    String bodyRequest = getBodyRequest();
                    ValidatableResponse response = null;
                    switch (action) {
                    case "create":
                    case "Create":
                    case "Post":
                    case "post":
                    case "POST":
                    case "CreateOrUpdate":
                        response = RestApiUtils.post(api + base.getField(field).get(), bodyRequest);
                        break;
                    case "read":
                    case "Read":
                    case "Get":
                    case "get":
                    case "GET":
                        response = RestApiUtils.get(api + base.getField(field).get(), bodyRequest);
                        break;
                    case "update":
                    case "Update":
                    case "Put":
                    case "put":
                    case "PUT":
                        response = RestApiUtils.put(api + base.getField(field).get(), bodyRequest);
                        break;
                    case "delete":
                    case "Delete":
                    case "Del":
                    case "del":
                    case "DEL":
                        response = RestApiUtils.delete(api + base.getField(field).get(), bodyRequest);
                        break;
                    }
                    ActionStatus actionStatus = (response.extract().jsonPath().get("actionStatus") == null
                            ? response.extract().body().as(ActionStatus.class)
                            : response.extract().jsonPath().getObject("actionStatus", ActionStatus.class));
                    if (actionStatus.getMessage() == null)
                        actionStatus.setMessage("");
                    base.setResponse(new ApiResponse(response.extract().statusCode(), actionStatus,
                            response.extract().jsonPath()));
                    base.setJsonresponse(response);
                    assertNotNull(base.getResponse());
                    assertNotNull(base.getResponse().getActionStatus());
                    assertNotNull(base.getResponse().getHttpStatusCode());
                });
        When("^I call the delete \"([^\"]*)\"$", (String api) -> {
            String bodyRequest = getBodyRequest();
            base.getCode().ifPresent( code ->{
                ValidatableResponse response = RestApiUtils.delete(api + code, bodyRequest);
                base.setResponse(new ApiResponse(response.extract().statusCode(), response.extract().body().as(ActionStatus.class)));
                assertNotNull(base.getResponse());
                assertNotNull(base.getResponse().getActionStatus());
                assertNotNull(base.getResponse().getHttpStatusCode());
            });
        });
        When("^I call the delete \"([^\"]*)\" with identifier \"([^\"]*)\"$", (String api, String field) -> {
            String bodyRequest = getBodyRequest();
            base.getField(field).ifPresent( code ->{
                ValidatableResponse response = RestApiUtils.delete(api + code, bodyRequest);
                base.setResponse(new ApiResponse(response.extract().statusCode(), response.extract().body().as(ActionStatus.class)));
                assertNotNull(base.getResponse());
                assertNotNull(base.getResponse().getActionStatus());
                assertNotNull(base.getResponse().getHttpStatusCode());
            });
        });
        When("^I call the delete \"([^\"]*)\" with identifiers \"([^\"]*)\" and \"([^\"]*)\"$", (String api, String field1, String field2) -> {
            String bodyRequest = getBodyRequest();
            base.getField(field1).ifPresent( f1 ->{
                base.getField(field2).ifPresent(f2 ->{
                    ValidatableResponse response = RestApiUtils.delete(api + f1 + "/" + f2, bodyRequest);
                    base.setResponse(new ApiResponse(response.extract().statusCode(), response.extract().body().as(ActionStatus.class)));
                    assertNotNull(base.getResponse());
                    assertNotNull(base.getResponse().getActionStatus());
                    assertNotNull(base.getResponse().getHttpStatusCode());
                });
            });
        });
        Then("^The entity is deleted$", () -> {
            assertNotNull(base.getResponse());
        });
        Then("^The entity is created$", () -> {

        });    
        And("^Validate that the statusCode is \"([^\"]*)\"$", (String statusCode) -> {
            assertNotNull(base.getResponse());
            assertEquals(Integer.valueOf(statusCode).intValue(),
                    base.getResponse().getHttpStatusCode());
        });
        And("^The status is \"([^\"]*)\"$", (String actionStatus) -> {
            assertNotNull(base.getResponse());
            assertNotNull(base.getResponse().getActionStatus());
            assertEquals(base.getResponse().getActionStatus().getStatus().toString(),
                    base.getResponse().getActionStatus().getStatus().name(), actionStatus);
        });
        And("^The message  is \"([^\"]*)\"$", (String message) -> {
            assertNotNull(base.getResponse());
            assertNotNull(base.getResponse().getActionStatus());
            assertNotNull(base.getResponse().getActionStatus().getMessage());
            assertTrue(base.getResponse().getActionStatus().getMessage(),
                    base.getResponse().getActionStatus().getMessage().contains(message));
        });
        And("^The errorCode  is \"([^\"]*)\"$", (String errorCode) -> {
            assertNotNull(base.getResponse());
            assertNotNull(base.getResponse().getActionStatus());
            if(base.getResponse().getActionStatus().getErrorCode() != null) {
                assertEquals(base.getResponse().getActionStatus().getErrorCode().toString(),
                        base.getResponse().getActionStatus().getErrorCode().toString(), errorCode);
            }
        });

    }

    private void setJsonObject(String filename) {
        JsonParser<?> jsonParser = new JsonParser<>();
        JsonNode json = jsonParser.readValue(filename);
        base.setJsonObject(json);
    }

    private String getBodyRequest() throws JsonProcessingException {
        return JsonParser.writeValueAsString(base.getJsonObject());
    }
}
