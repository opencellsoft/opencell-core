package org.meveo.service.payments.impl;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentGatewayTypeEnum;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.payment.PaymentScriptInterface;

@Stateless
public class GatewayPaymentFactory implements Serializable {

    private static final long serialVersionUID = -8729566002684225810L;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    public GatewayPaymentInterface getInstance(PaymentGateway paymentGateway) throws Exception {
        GatewayPaymentInterface gatewayPaymentInterface = null;

        if (paymentGateway.getType() == PaymentGatewayTypeEnum.CUSTOM) {
            gatewayPaymentInterface = new CustomApiGatewayPayment((PaymentScriptInterface) scriptInstanceService.getScriptInstance(paymentGateway.getScriptInstance().getCode()));
        }
        if (paymentGateway.getType() == PaymentGatewayTypeEnum.NATIF) {
            Class<?> clazz = Class.forName(paymentGateway.getImplementationClassName());
            gatewayPaymentInterface = (GatewayPaymentInterface) clazz.newInstance();
        }

        return gatewayPaymentInterface;
    }
}
