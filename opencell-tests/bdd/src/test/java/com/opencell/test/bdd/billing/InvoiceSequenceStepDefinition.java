package com.opencell.test.bdd.billing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.GetInvoiceSequenceResponse;
import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;

import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;

public class InvoiceSequenceStepDefinition implements En {

    public InvoiceSequenceStepDefinition(BaseHook base) {
        Then("^The invoice sequence is created$", () -> {

            base.getCode().ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/invoiceSequence?invoiceSequenceCode=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetInvoiceSequenceResponse actualEntity = response.extract().body().as(GetInvoiceSequenceResponse.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getInvoiceSequenceDto());
                    assertEquals(code, actualEntity.getInvoiceSequenceDto().getCode());
                }
            });
        });
    }
}
