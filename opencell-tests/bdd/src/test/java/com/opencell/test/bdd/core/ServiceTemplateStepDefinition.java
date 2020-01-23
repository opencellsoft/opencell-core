package com.opencell.test.bdd.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.JsonParser;

import cucumber.api.java8.En;
import io.restassured.path.json.JsonPath;

public class ServiceTemplateStepDefinition implements En {

    public ServiceTemplateStepDefinition(BaseHook base) {
        Then("^I get the Service template  with a custom fields$", () -> {
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
