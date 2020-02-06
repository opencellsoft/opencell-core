package com.opencell.test.bdd.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.catalog.GetServiceTemplateResponseDto;

import com.fasterxml.jackson.databind.JsonNode;
import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.JsonParser;
import com.opencell.test.utils.RestApiUtils;

import cucumber.api.java8.En;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;

public class ServiceTemplateStepDefinition implements En {

    public ServiceTemplateStepDefinition(BaseHook base) {
        Then("^The service template is created$", () -> {
            base.getCode().ifPresent(code -> {
                if (base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils
                            .get("/catalog/serviceTemplate?serviceTemplateCode=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetServiceTemplateResponseDto actualEntity = response.extract().body()
                            .as(GetServiceTemplateResponseDto.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getServiceTemplate());
                    assertEquals(code, actualEntity.getServiceTemplate().getCode());
                }
            });
        });
        Then("^I get the Service template with a custom fields$", () -> {
            JsonPath json = base.getJsonresponse().extract().jsonPath();
            assertNotNull(json.get("listServiceTemplate[0].customFields.customField[0].code"));
        });
        And("Service template contains the following CF \"([^\"]*)\"$", (String file) -> {
            JsonParser<?> jsonParser = new JsonParser<>();
            JsonNode jsonnode = jsonParser.readValue(file);
            JsonPath expectedJson = new JsonPath(JsonParser.writeValueAsString(jsonnode));

            List<Object> customFields = base.getJsonresponse().extract().jsonPath()
                    .get("listServiceTemplate.customFields.customField[0]");
            Object expectedObject = expectedJson.getMap("");
            
            assertTrue(customFields.contains(expectedObject));
        });
    }
}
