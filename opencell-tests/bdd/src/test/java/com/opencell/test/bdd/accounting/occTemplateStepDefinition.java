package com.opencell.test.bdd.accounting;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;
import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.GetOccTemplateResponseDto;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class occTemplateStepDefinition implements En {

    public occTemplateStepDefinition(BaseHook base) {
        Then("^The occ template is created$", () -> {

            base.getCode().ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/occTemplate?occTemplateCode=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetOccTemplateResponseDto actualEntity = response.extract().body().as(GetOccTemplateResponseDto.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getOccTemplate());
                    assertEquals(code, actualEntity.getOccTemplate().getCode());
                }
            });


        });
    }
}
