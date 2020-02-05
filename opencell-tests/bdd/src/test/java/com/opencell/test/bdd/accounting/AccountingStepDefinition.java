package com.opencell.test.bdd.accounting;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;
import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.billing.AccountingCodeGetResponseDto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AccountingStepDefinition implements En {

    public AccountingStepDefinition(BaseHook base) {
        Then("^The accounting code is created$", () -> {
            base.getCode().ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/billing/accountingCode?accountingCode=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    AccountingCodeGetResponseDto actualEntity = response.extract().body().as(AccountingCodeGetResponseDto.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getAccountingCode());
                    assertEquals(code, actualEntity.getAccountingCode().getCode());
                }
            });


        });
    }
}
