package com.opencell.test.bdd.billing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.GetInvoiceSubCategoryResponse;
import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;

import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;

public class InvoiceSubcategoryStepDefition implements En {

    public InvoiceSubcategoryStepDefition(BaseHook base) {
        Then("^The invoiceSubCategory is created$", () -> {

            base.getCode().ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/invoiceSubCategory?invoiceSubCategoryCode=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetInvoiceSubCategoryResponse actualEntity = response.extract().body().as(GetInvoiceSubCategoryResponse.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getInvoiceSubCategory());
                    assertEquals(code, actualEntity.getInvoiceSubCategory().getCode());
                }
            });


        });
    }
}
