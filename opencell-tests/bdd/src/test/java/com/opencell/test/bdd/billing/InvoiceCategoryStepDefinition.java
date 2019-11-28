package com.opencell.test.bdd.billing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.GetInvoiceCategoryResponse;
import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;

import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;

public class InvoiceCategoryStepDefinition implements En {

    public InvoiceCategoryStepDefinition(BaseHook base) {
        Then("^The invoice category is created$", () -> {

            base.getCode().ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/invoiceCategory?invoiceCategoryCode=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetInvoiceCategoryResponse actualEntity = response.extract().body().as(GetInvoiceCategoryResponse.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getInvoiceCategory());
                    assertEquals(code, actualEntity.getInvoiceCategory().getCode());
                }
            });
        });
    }
}
