package com.opencell.test.bdd.settings;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;
import cucumber.api.PendingException;
import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.GetSellerResponse;
import org.meveo.api.dto.response.SellerResponseDto;
import org.meveo.api.dto.response.module.MeveoModuleDtoResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SellerStepDefinition implements En {
    public SellerStepDefinition(BaseHook base) {
        Then("^The seller is created$", () -> {
            base.getCode().ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/seller?sellerCode=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetSellerResponse actualEntity = response.extract().body().as(GetSellerResponse.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getSeller());
                    assertEquals(code, actualEntity.getSeller().getCode());
                }
            });
        });
    }
}
