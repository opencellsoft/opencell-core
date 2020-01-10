package com.opencell.test.bdd.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.GetInvoiceTypeResponse;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;

import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;

public class InvoiceTypeStepDefinition implements En {
    public InvoiceTypeStepDefinition(BaseHook base) {
        Then("^The invoice type is created$", () -> {
            base.getCode().ifPresent(code -> {
                if (base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/invoiceType?invoiceTypeCode=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetInvoiceTypeResponse actualEntity = response.extract().body().as(GetInvoiceTypeResponse.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getInvoiceTypeDto());
                    assertEquals(code, actualEntity.getInvoiceTypeDto().getCode());
                }
            });
        });
    }
}
