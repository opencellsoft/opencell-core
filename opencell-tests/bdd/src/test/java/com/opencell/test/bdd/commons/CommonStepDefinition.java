package com.opencell.test.bdd.commons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencell.test.utils.JSONParserException;
import com.opencell.test.utils.JsonParser;
import com.opencell.test.utils.RestApiUtils;
import cucumber.api.PendingException;
import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.BaseEntityDto;
import org.reflections.Reflections;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CommonStepDefinition implements En {

    private BaseHook base;


    public CommonStepDefinition(BaseHook base) {
        this.base = base;
        Given("^The entity has the following information \"([^\"]*)\" as \"([^\"]*)\"$", (String filename, String dto) -> {
            Class klazz = base.getEntityClass(dto).get();
            setJsonObject(filename, klazz);
            assertTrue(base.getEntityDto() != null || base.getJsonObject() != null);
        });
        When("^I call the \"([^\"]*)\"$", (String api) -> {
            String bodyRequest = getBodyRequest();
            ValidatableResponse response = RestApiUtils.post(api, bodyRequest);
            base.setResponse(new ApiResponse(response.extract().statusCode(), response.extract().body().as(ActionStatus.class)));
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
                response = RestApiUtils.post(api, bodyRequest);
                break;
            case "update":
            case "Update":
                response = RestApiUtils.put(api, bodyRequest);
                break;
            case "delete":
            case "Delete":
                response = RestApiUtils.delete(api, bodyRequest);
                break;
            }
            base.setResponse(new ApiResponse(response.extract().statusCode(), response.extract().body().as(ActionStatus.class)));
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
        And("^Validate that the statusCode is \"([^\"]*)\"$", (String statusCode) -> {
            assertNotNull(base.getResponse());
            assertEquals(Integer.valueOf(statusCode).intValue(), base.getResponse().getHttpStatusCode());
        });
        And("^The status is \"([^\"]*)\"$", (String actionStatus) -> {
            assertNotNull(base.getResponse());
            assertNotNull(base.getResponse().getActionStatus());
            assertEquals(base.getResponse().getActionStatus().getStatus().name(),actionStatus);
        });
        And("^The message  is \"([^\"]*)\"$", (String message) -> {
            assertNotNull(base.getResponse());
            assertNotNull(base.getResponse().getActionStatus());
            assertNotNull(base.getResponse().getActionStatus().getMessage());
            assertTrue(base.getResponse().getActionStatus().getMessage().contains(message));
        });
        And("^The errorCode  is \"([^\"]*)\"$", (String errorCode) -> {
            assertNotNull(base.getResponse());
            assertNotNull(base.getResponse().getActionStatus());
            if(base.getResponse().getActionStatus().getErrorCode() != null) {
                assertEquals(base.getResponse().getActionStatus().getErrorCode().name(), errorCode);
            }
        });

    }

    private void setJsonObject(String filename, Class klazz) {
        JsonParser<?> jsonParser = new JsonParser<>();
        try {
            base.setEntityDto(jsonParser.readValue(filename, klazz));
        }catch (JSONParserException e){
            JsonNode json = jsonParser.readValue(filename);
            base.setJsonObject(json);
        }
    }

    private String getBodyRequest() throws JsonProcessingException {
        if(base.getEntityDto() != null){
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(base.getEntityDto());
        }else {
            return JsonParser.writeValueAsString(base.getJsonObject());
        }
    }

}
