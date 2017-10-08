/**
 * 
 */
package org.meveo.service.payments.impl;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.service.base.BusinessService;


/**
 * The Class PaymentGatewayService.
 *
 * @author anasseh
 */
@Stateless
public class PaymentGatewayService extends BusinessService<PaymentGateway> {


    
    /**
     * Gets the payment gateway.
     *
     * @param customerAccount the customer account
     * @param cardPaymentMethod the card payment method
     * @return the payment gateway
     * @throws BusinessException the business exception
     */
    // TODO paymentRun return gateway by CA, EL, Priority,.....
    public PaymentGateway getPaymentGateway(CustomerAccount customerAccount,CardPaymentMethod cardPaymentMethod) throws BusinessException {
	try {
	    return listActive().get(0);
	} catch (Exception e) {
	    throw new BusinessException("Cant find active PaymentGateway");
	}
    }
}
