package com.opencell.test.bdd.payments;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;
import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.meveo.api.dto.payment.PaymentScheduleTemplateResponseDto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PaymentScheduleTemplateStepDefinition implements En {
    public PaymentScheduleTemplateStepDefinition(BaseHook base) {
        Then("^The payment schedule template is created$", () -> {
            base.getCode().ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/payment/paymentScheduleTemplate?paymentScheduleTemplateCode=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    PaymentScheduleTemplateResponseDto actualEntity = response.extract().body().as(PaymentScheduleTemplateResponseDto.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getPaymentScheduleTemplateDto());
                    assertEquals(code, actualEntity.getPaymentScheduleTemplateDto().getCode());
                }
            });
        });
    }
}
