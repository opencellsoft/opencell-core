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
import javax.inject.Inject;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.keystore.KeystoreManager;
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
import org.meveo.service.crm.impl.ProviderService;

/**
 * The Class PaymentGatewayService.
 *
 * @author anasseh
 */
@Stateless
public class PaymentGatewayService extends BusinessService<PaymentGateway> {

	@Inject
	private ProviderService providerService;
	
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
     * 
     * @param customerAccount the customer account
     * @param paymentMethod the payment method
     * @param cardType card type
     * @param seller the seller
     * @param paymentMethodType the paymentMethod type
     * @return
     */
    public PaymentGateway getPaymentGateway(CustomerAccount customerAccount, PaymentMethod paymentMethod, CreditCardTypeEnum cardType,
			Seller seller, PaymentMethodEnum paymentMethodType) {
		
    	return getAndCheckPaymentGateway(customerAccount, paymentMethod, cardType, seller,null, paymentMethodType);
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
    	return getAndCheckPaymentGateway(customerAccount, paymentMethod, cardType, seller, null);
    }
    
    /**
     * Gets the payment gateway.
     * 
     * @param customerAccount
     * @param paymentMethod
     * @param cardType
     * @param seller
     * @param selectedGatewayCode
     * @return
     * @throws BusinessException
     */
    public PaymentGateway getAndCheckPaymentGateway(CustomerAccount customerAccount, PaymentMethod paymentMethod, CreditCardTypeEnum cardType,Seller seller,String selectedGatewayCode) throws BusinessException {
    	return getAndCheckPaymentGateway(customerAccount, paymentMethod, cardType, seller, selectedGatewayCode, null);
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
    public PaymentGateway getAndCheckPaymentGateway(CustomerAccount customerAccount, PaymentMethod paymentMethod, CreditCardTypeEnum cardType,Seller seller,String selectedGatewayCode,PaymentMethodEnum paymentMethodType ) throws BusinessException {
        PaymentGateway paymentGateway = null;
        if (customerAccount == null) {
            throw new BusinessException("CustomerAccount is null in getPaymentGateway");
        }
        try {        	 
            CreditCardTypeEnum cardTypeToCheck = null;
            if( paymentMethod instanceof CardPaymentMethod) {
            	cardTypeToCheck = ((CardPaymentMethod) paymentMethod).getCardType();
            }           
            String queryStr = "from " + PaymentGateway.class.getSimpleName()
                    + " where paymentMethodType =:paymenTypeValueIN and disabled=false and (country is null or country =:countryValueIN) and "
                    + " (cardType is null or cardType =:cardTypeValueIN) and "
                    + " (seller is null or seller =:sellerIN) ";
                                  
            Query query = getEntityManager()
                .createQuery(queryStr)
                .setParameter("paymenTypeValueIN", paymentMethod == null ? paymentMethodType : paymentMethod.getPaymentType())
                .setParameter("countryValueIN", customerAccount.getAddress() == null ? null : customerAccount.getAddress().getCountry())               
                .setParameter("cardTypeValueIN", cardTypeToCheck)
                .setParameter("sellerIN", seller);              
                 
            List<PaymentGateway> paymentGateways = (List<PaymentGateway>) query.getResultList();
            
            if (paymentGateways == null || paymentGateways.isEmpty()) {
                return null;
            }else {
            	log.info("paymentGateways size:{}", paymentGateways.size());
            }
            for (PaymentGateway pg : paymentGateways) {            	
            	if( pg.getCode().equals(selectedGatewayCode) ) { 
            		log.info("PaymentGateway {} , rtuerned  as selectedGatewayCode" ,pg.getCode());
    				return pg;
            	}            	      	            		
            }
            for (PaymentGateway pg : paymentGateways) {
            	log.info("get PaymentGateway , current :{}" , pg.getCode());
            	if (!StringUtils.isBlank(pg.getApplicationEL()) && matchExpression(pg.getApplicationEL(), customerAccount, paymentMethod, pg, seller)) {
            		log.info("PaymentGateway {} , rtuerned  " ,pg.getCode());
        			return pg;
        		}             	            		
            }
            log.info("PaymentGateway {} , rtuerned " ,paymentGateways.get(0).getCode());
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
                log.info("get pg , current :{}" , pg.getCode());
                if (!StringUtils.isBlank(pg.getApplicationEL()) && matchExpression(pg.getApplicationEL(), null, null, paymentGateway, seller)) {
                	log.info(" pg {} , rtuerned " ,pg.getCode());
                    return pg;	                
                }
            }
            log.info(" pg {} , rtuerned " ,paymentGateways.get(0).getCode());
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
        userMap.put(ValueExpressionWrapper.VAR_CUSTOMER_ACCOUNT, customerAccount);
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

    @Override
    public void remove(PaymentGateway paymentGateway) {
        // remove credential of paymentGateway in the keystore
    	if(KeystoreManager.existKeystore()) {
    		KeystoreManager.removeCredential(paymentGateway.getClass().getSimpleName() + "." + paymentGateway.getId());
    	}

        super.remove(paymentGateway);
    }

}