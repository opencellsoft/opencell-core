package com.opencell.test.bdd.invoicing;

import static org.junit.Assert.assertNotNull;

import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.InvoicesDto;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;

import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;

public class InvoiceStepDefinition implements En {
    public InvoiceStepDefinition(BaseHook base) {
        Then("^The invoice is created$", () -> {
            if (base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                ValidatableResponse response = RestApiUtils.get("/invoice/list", null);
                response.assertThat().statusCode(HttpStatus.SC_OK);
                InvoicesDto actualEntity = response.extract().body().as(InvoicesDto.class);
                assertNotNull(actualEntity);
                assertNotNull(actualEntity.getInvoices());
            }
        });
    }
}
