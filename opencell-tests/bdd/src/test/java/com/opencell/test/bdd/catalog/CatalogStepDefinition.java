package com.opencell.test.bdd.catalog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.catalog.GetBusinessOfferModelResponseDto;
import org.meveo.api.dto.response.catalog.GetBusinessServiceModelResponseDto;
import org.meveo.api.dto.response.catalog.GetOfferTemplateResponseDto;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;

import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;

public class CatalogStepDefinition implements En {

    public CatalogStepDefinition(BaseHook base) {
        Then("^The business offer model is created$", () -> {
            base.getCode().ifPresent(code -> {
                if (base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils
                            .get("/catalog/businessOfferModel?businessOfferModelCode=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetBusinessOfferModelResponseDto actualEntity = response.extract().body()
                            .as(GetBusinessOfferModelResponseDto.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getBusinessOfferModel());
                    assertEquals(code, actualEntity.getBusinessOfferModel().getCode());
                }
            });
        });
        Then("^The business service model is created$", () -> {
            base.getCode().ifPresent(code -> {
                if (base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils
                            .get("/catalog/businessServiceModel?businessServiceModelCode=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetBusinessServiceModelResponseDto actualEntity = response.extract().body()
                            .as(GetBusinessServiceModelResponseDto.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getBusinessServiceModel());
                    assertEquals(code, actualEntity.getBusinessServiceModel().getCode());
                }
            });
        });
        Then("^The offer from bom is created$", () -> {
            base.getField("bomCode").ifPresent(code -> {
                if (base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils
                            .get("/catalog/offerTemplate?offerTemplateCode=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetOfferTemplateResponseDto actualEntity = response.extract().body()
                            .as(GetOfferTemplateResponseDto.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getOfferTemplate());
                    assertEquals(code, actualEntity.getOfferTemplate().getCode());
                }
            });
        });
    }
}
