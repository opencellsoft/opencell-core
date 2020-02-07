package com.opencell.test.bdd.billing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.billing.FindWalletOperationsResponseDto;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;

import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;

public class WalletOperationStepDefinition implements En {

    public WalletOperationStepDefinition(BaseHook base) {
        Then("^The wallet operation is created$", () -> {
            base.getCode().ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.post("/billing/wallet/operation/list",
                            "{\"filters\":{\"code\":\"" + code + "\"}}");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    FindWalletOperationsResponseDto actualEntity = response.extract().body()
                            .as(FindWalletOperationsResponseDto.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getWalletOperations());
                    assertEquals(code, actualEntity.getWalletOperations().get(0).getCode());
                }
            });
        });
    }
}
