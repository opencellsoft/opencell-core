/**
 * 
 */
package org.meveo.service.payments.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;

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
    @SuppressWarnings("unchecked")
    public PaymentGateway getPaymentGateway(CustomerAccount customerAccount, PaymentMethod paymentMethod, CreditCardTypeEnum cardType) throws BusinessException {
        PaymentGateway paymentGateway = null;
        try {
            CreditCardTypeEnum cardTypeToCheck = null;
            if (paymentMethod == null) {
                cardTypeToCheck = cardType;
            } else if (paymentMethod instanceof CardPaymentMethod) {
                cardTypeToCheck = ((CardPaymentMethod) paymentMethod).getCardType();
            }

            Query query = getEntityManager()
                .createQuery("from " + PaymentGateway.class.getSimpleName()
                        + " where paymentMethodType =:paymenTypeValueIN and disabled=false and (country is null or country =:countryValueIN) and "
                        + " (tradingCurrency is null or tradingCurrency =:tradingCurrencyValueIN)  and  (cardType is null or cardType =:cardTypeValueIN) ")
                .setParameter("paymenTypeValueIN", paymentMethod == null ? PaymentMethodEnum.CARD : paymentMethod.getPaymentType())
                .setParameter("countryValueIN", customerAccount.getAddress() == null ? null : customerAccount.getAddress().getCountry())
                .setParameter("tradingCurrencyValueIN", customerAccount.getTradingCurrency()).setParameter("cardTypeValueIN", cardTypeToCheck);

            List<PaymentGateway> paymentGateways = (List<PaymentGateway>) query.getResultList();
            if (paymentGateways == null || paymentGateways.isEmpty()) {
                return null;
            }
            for (PaymentGateway pg : paymentGateways) {
                if (!StringUtils.isBlank(pg.getApplicationEL())) {
                    if (matchExpression(pg.getApplicationEL(), customerAccount, paymentMethod, pg)) {
                        return pg;
                    }
                }
            }
            paymentGateway = paymentGateways.get(0);
        } catch (Exception e) {
            log.error("Error on getPaymentGateway:", e);
        }
        return paymentGateway;
    }

    private boolean matchExpression(String expression, CustomerAccount customerAccount, PaymentMethod paymentMethod, PaymentGateway paymentGateway) throws BusinessException {
        Boolean result = true;
        if (StringUtils.isBlank(expression)) {
            return result;
        }

        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("customerAccount", customerAccount);
        userMap.put("paymentMethod", paymentMethod);
        userMap.put("paymentGateway", paymentGateway);

        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, Boolean.class);
        try {
            result = (Boolean) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to boolean but " + res);
        }
        return result;
    }

}