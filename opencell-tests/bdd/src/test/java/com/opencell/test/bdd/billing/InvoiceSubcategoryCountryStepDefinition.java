package com.opencell.test.bdd.billing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.GetInvoiceSubCategoryCountryResponse;
import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;

import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;

public class InvoiceSubcategoryCountryStepDefinition implements En {

    public InvoiceSubcategoryCountryStepDefinition(BaseHook base) {
        Then("^The invoiceSubCategoryCountry is created$", () -> {

            base.getCode().ifPresent( invoiceSubCategory ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/invoiceSubCategoryCountry?invoiceSubCategoryCode=" + invoiceSubCategory, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetInvoiceSubCategoryCountryResponse actualEntity = response.extract().body().as(GetInvoiceSubCategoryCountryResponse.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getInvoiceSubCategoryCountryDto());
                    assertEquals(invoiceSubCategory, actualEntity.getInvoiceSubCategoryCountryDto().getInvoiceSubCategory());
                }
            });
        });
    }
}
