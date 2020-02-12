package com.opencell.test.bdd.custom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.GetCustomFieldTemplateReponseDto;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;

import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;

public class CustomFieldTemplateStepDefinition implements En {

    public CustomFieldTemplateStepDefinition(BaseHook base) {
        Then("^The custom field template is created$", () -> {
            base.getCode().ifPresent( code ->{
                base.getField("appliesTo").ifPresent(appliesTo -> {
                    if (base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                        ValidatableResponse response = RestApiUtils
                                .get("/customFieldTemplate?customFieldTemplateCode=" + code + "&appliesTo=" + appliesTo,
                                        "");
                        response.assertThat().statusCode(HttpStatus.SC_OK);
                        GetCustomFieldTemplateReponseDto actualEntity = response.extract().body()
                                .as(GetCustomFieldTemplateReponseDto.class);
                        assertNotNull(actualEntity);
                        assertNotNull(actualEntity.getCustomFieldTemplate());
                        assertEquals(code, actualEntity.getCustomFieldTemplate().getCode());
                    }
                });
            });
        });
    }
}
