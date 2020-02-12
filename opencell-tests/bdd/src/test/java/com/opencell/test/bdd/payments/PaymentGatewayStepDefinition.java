package com.opencell.test.bdd.payments;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;
import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.meveo.api.dto.payment.PaymentGatewayResponseDto;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PaymentGatewayStepDefinition implements En {
    public PaymentGatewayStepDefinition(BaseHook base) {
        Then("^The payment gateway is created$", () -> {
            base.getCode().ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/payment/paymentGateway?code=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    PaymentGatewayResponseDto actualEntity = response.extract().body().as(PaymentGatewayResponseDto.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getPaymentGateways());
                    assertEquals(code, actualEntity.getPaymentGateways().get(0).getCode());
                }
            });
        });
    }
}
