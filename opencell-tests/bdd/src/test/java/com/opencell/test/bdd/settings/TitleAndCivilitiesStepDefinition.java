package com.opencell.test.bdd.settings;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;
import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.account.TitleResponseDto;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TitleAndCivilitiesStepDefinition implements En {
    public TitleAndCivilitiesStepDefinition(BaseHook base) {
        Then("^The title and civility is created$", () -> {
            base.getCode().ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/account/title?titleCode=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    TitleResponseDto actualEntity = response.extract().body().as(TitleResponseDto.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getTitleDto());
                    assertEquals(code, actualEntity.getTitleDto().getCode());
                }
            });
        });
    }
}
