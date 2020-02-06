package com.opencell.test.bdd.settings;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;
import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.catalog.GetChannelResponseDto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ChannelStepDefinition implements En {
    public ChannelStepDefinition(BaseHook base) {
        Then("^The channel is created$", () -> {
            base.getCode().ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/catalog/channel?channelCode=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetChannelResponseDto actualEntity = response.extract().body().as(GetChannelResponseDto.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getChannel());
                    assertEquals(code, actualEntity.getChannel().getCode());
                }
            });
        });
    }
}
