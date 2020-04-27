/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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
import org.meveo.model.admin.Seller;
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
     * @param cardType card type
     * @return the payment gateway
     * @throws BusinessException the business exception
     */    
    public PaymentGateway getPaymentGateway(CustomerAccount customerAccount, PaymentMethod paymentMethod, CreditCardTypeEnum cardType) throws BusinessException {
       return getPaymentGateway(customerAccount, paymentMethod, cardType,customerAccount.getCustomer().getSeller());
    }

    /**
     * Gets the payment gateway.
     *
     * @param customerAccount the customer account
     * @param paymentMethod the payment method
     * @param cardType the card type
     * @param seller the seller
     * @return the payment gateway
     * @throws BusinessException the business exception
     */
    @SuppressWarnings("unchecked")
    public PaymentGateway getPaymentGateway(CustomerAccount customerAccount, PaymentMethod paymentMethod, CreditCardTypeEnum cardType,Seller seller) throws BusinessException {
        PaymentGateway paymentGateway = null;
        try {        	 
            CreditCardTypeEnum cardTypeToCheck = null;
            if (paymentMethod == null) {
                cardTypeToCheck = cardType;
            } else if (paymentMethod instanceof CardPaymentMethod) {
                cardTypeToCheck = ((CardPaymentMethod) paymentMethod).getCardType();
            }
            String queryStr = "from " + PaymentGateway.class.getSimpleName()
                    + " where paymentMethodType =:paymenTypeValueIN and disabled=false and (country is null or country =:countryValueIN) and "
                    + " (tradingCurrency is null or tradingCurrency =:tradingCurrencyValueIN)  and  (cardType is null or cardType =:cardTypeValueIN) and "
                    + " (seller is null or seller =:sellerIN)";

            if (customerAccount == null) {
                throw new BusinessException("CustomerAccount is null in getPaymentGateway");
            }
            if(seller == null && customerAccount.getCustomer() != null) {
            	seller = customerAccount.getCustomer().getSeller();
            }
            
            Query query = getEntityManager()
                .createQuery(queryStr)
                .setParameter("paymenTypeValueIN", paymentMethod == null ? PaymentMethodEnum.CARD : paymentMethod.getPaymentType())
                .setParameter("countryValueIN", customerAccount.getAddress() == null ? null : customerAccount.getAddress().getCountry())
                .setParameter("tradingCurrencyValueIN", customerAccount.getTradingCurrency())
                .setParameter("cardTypeValueIN", cardTypeToCheck)
                .setParameter("sellerIN", seller);
            
            log.info("paymenTypeValueIN:"+(paymentMethod == null ? PaymentMethodEnum.CARD : paymentMethod.getPaymentType()));
            log.info("countryValueIN:"+(customerAccount.getAddress() == null ? null : customerAccount.getAddress().getCountry()));
            log.info("tradingCurrencyValueIN:"+(customerAccount.getTradingCurrency()));
            log.info("cardTypeValueIN:"+(cardTypeToCheck));
            log.info("sellerIN:"+(seller == null ?  null : seller.getCode()));
            
                 
            List<PaymentGateway> paymentGateways = (List<PaymentGateway>) query.getResultList();
            log.info("paymentGateways:"+(paymentGateways == null ? null : paymentGateways.size()));
            
            if (paymentGateways == null || paymentGateways.isEmpty()) {
                return null;
            }
            for (PaymentGateway pg : paymentGateways) {
                log.info("get pg , current :" + pg.getCode());
				if (!StringUtils.isBlank(pg.getApplicationEL())) {
					if (matchExpression(pg.getApplicationEL(), customerAccount, paymentMethod, pg, seller)) {
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


    /**
     * Gets the payment gateway.
     *
     * @param seller the seller
     * @param paymentMethodEnum the payment method enum
     * @return the payment gateway
     * @throws BusinessException the business exception
     */
    public PaymentGateway getPaymentGateway(Seller seller, PaymentMethodEnum paymentMethodEnum) throws BusinessException {
        PaymentGateway paymentGateway = null;
        try {
            Query query = getEntityManager()
                .createQuery("from " + PaymentGateway.class.getSimpleName() + " where paymentMethodType =:paymenTypeValueIN and disabled=false and seller =:sellerIN ")
                .setParameter("paymenTypeValueIN", paymentMethodEnum).setParameter("sellerIN", seller);

            List<PaymentGateway> paymentGateways = (List<PaymentGateway>) query.getResultList();
            if (paymentGateways == null || paymentGateways.isEmpty()) {
                return null;
            }
            for (PaymentGateway pg : paymentGateways) {
                log.info("get pg , current :" + pg.getCode());
                if (!StringUtils.isBlank(pg.getApplicationEL())) {
	                if (matchExpression(pg.getApplicationEL(), null, null, paymentGateway, seller)) {
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

    
    /**
     * Match expression.
     *
     * @param expression the expression
     * @param customerAccount the customer account
     * @param paymentMethod the payment method
     * @param paymentGateway the payment gateway
     * @param seller the seller
     * @return true, if successful
     * @throws BusinessException the business exception
     */
    private boolean matchExpression(String expression, CustomerAccount customerAccount, PaymentMethod paymentMethod, PaymentGateway paymentGateway,Seller seller) throws BusinessException {

        Boolean result = true;
        if (StringUtils.isBlank(expression)) {
            return result;
        }

        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("customerAccount", customerAccount);
        userMap.put("ca", customerAccount);
        userMap.put("paymentMethod", paymentMethod);
        userMap.put("paymentGateway", paymentGateway);
        userMap.put("seller", seller);

        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, Boolean.class);
        try {
            result = (Boolean) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to boolean but " + res);
        }
        return result;
    }

}