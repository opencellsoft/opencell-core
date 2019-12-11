package com.opencell.test.bdd.custom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.CustomEntityTemplateResponseDto;
import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;

import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;

public class EntityCustomizationStepDefinition implements En {

    public EntityCustomizationStepDefinition(BaseHook base) {
        Then("^The entity customization is created$", () -> {
            base.getCode().ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/entityCustomization/entity/" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    CustomEntityTemplateResponseDto actualEntity = response.extract().body().as(CustomEntityTemplateResponseDto.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getCustomEntityTemplate());
                    assertEquals(code, actualEntity.getCustomEntityTemplate().getCode());
                }
            });
        });
    }
}
