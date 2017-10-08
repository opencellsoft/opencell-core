package org.meveo.service.payments.impl;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentGatewayTypeEnum;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.payment.PaymentScriptInterface;

@Stateless
public class GatewayPaymentFactory implements Serializable {

    private static final long serialVersionUID = -8729566002684225810L;

    @Inject
    private ScriptInstanceService scriptInstanceService;
    
    @Inject
    private PaymentGatewayService paymentGatewayService;


   
    public GatewayPaymentInterface getInstance(CustomerAccount customerAccount,CardPaymentMethod cardPaymentMethod) throws Exception {
        GatewayPaymentInterface gatewayPaymentInterface = null;        
        PaymentGateway paymentGateway =  paymentGatewayService.getPaymentGateway(customerAccount,cardPaymentMethod);
        if (paymentGateway == null) {
            throw new Exception("No payment gateway" );
        }
        
        if(paymentGateway.getType() == PaymentGatewayTypeEnum.CUSTOM) {
            gatewayPaymentInterface = new CustomApiGatewayPayment((PaymentScriptInterface) scriptInstanceService.getScriptInstance(paymentGateway.getScriptInstance().getCode()));
        }        
        if(paymentGateway.getType() == PaymentGatewayTypeEnum.NATIF) {
            Class<?> clazz = Class.forName(paymentGateway.getImplementationClassName());
             gatewayPaymentInterface = (GatewayPaymentInterface) clazz.newInstance();
        }      
       
        return gatewayPaymentInterface;
    }
}
