/**
 * 
 */
package org.meveo.service.payments.impl;

import java.util.List;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentMethod;
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
     * @param paymentMethod the payment method
     * @return the payment gateway
     * @throws BusinessException the business exception
     */
    // TODO paymentRun return gateway by CA, EL, Priority,.....
    @SuppressWarnings("unchecked")
    public PaymentGateway getPaymentGateway(CustomerAccount customerAccount, PaymentMethod paymentMethod) throws BusinessException {
        PaymentGateway paymentGateway = null;
        if (paymentMethod == null) {
            paymentMethod = customerAccount.getPreferredPaymentMethod();
        }        
        try {
            List<PaymentGateway> paymentGateways = (List<PaymentGateway>) getEntityManager()
                .createQuery("from " + PaymentGateway.class.getSimpleName() + " where paymentMethodType =:paymenTypeValueIN and disabled=false  ")
                .setParameter("paymenTypeValueIN", paymentMethod.getPaymentType()).getResultList();
            if (paymentGateways != null && !paymentGateways.isEmpty()) {
                paymentGateway = paymentGateways.get(0);
            }
        } catch (Exception e) {
            log.error("Error on getPaymentGateway:", e);
        }
        return paymentGateway;
    }
}
