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

package org.meveo.service.payments.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.payment.HostedCheckoutInput;
import org.meveo.api.dto.payment.MandatInfoDto;
import org.meveo.api.dto.payment.PaymentHostedCheckoutResponseDto;
import org.meveo.api.dto.payment.PaymentResponseDto;
import org.meveo.model.billing.Invoice;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.MandatStateEnum;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.service.script.payment.PaymentScript;
import org.meveo.service.script.payment.PaymentScriptInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 *  @author anasseh
 *  @author Mounir Bahije
 *  @lastModifiedVersion 5.2
 */

public class CustomApiGatewayPayment implements GatewayPaymentInterface {

    protected Logger log = LoggerFactory.getLogger(CustomApiGatewayPayment.class);
    private PaymentScriptInterface paymentScriptInterface;
    private PaymentGateway paymentGateway;

    public CustomApiGatewayPayment(PaymentScriptInterface paymentScriptInterface) {
        this.paymentScriptInterface = paymentScriptInterface;      
    }

    @Override
    public Object getClientObject() {
        return null;
    }

    @Override
    public String createCardToken(CustomerAccount customerAccount, String alias, String cardNumber, String cardHolderName, String expirayDate, String issueNumber,
            CreditCardTypeEnum cardType) throws BusinessException {
        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(PaymentScript.CONTEXT_PG, paymentGateway);
        scriptContext.put(PaymentScript.CONTEXT_CA, customerAccount);
        scriptContext.put(PaymentScript.CONTEXT_ALIAS, alias);
        scriptContext.put(PaymentScript.CONTEXT_CARD_NUMBER, cardNumber);
        scriptContext.put(PaymentScript.CONTEXT_CARD_OWNER, cardHolderName);
        scriptContext.put(PaymentScript.CONTEXT_CARD_EXPIRATION, expirayDate);
        scriptContext.put(PaymentScript.CONTEXT_CARD_TYPE, cardType);
        scriptContext.put(PaymentScript.CONTEXT_ISSUE_NUMBER, issueNumber);

        paymentScriptInterface.createCardToken(scriptContext);

        return (String) scriptContext.get(PaymentScript.RESULT_TOKEN);
    }

    @Override
    public PaymentResponseDto doPaymentToken(CardPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(PaymentScript.CONTEXT_PG, paymentGateway);
        scriptContext.put(PaymentScript.CONTEXT_TOKEN, paymentToken);
        scriptContext.put(PaymentScript.CONTEXT_AMOUNT_CTS, ctsAmount);
        scriptContext.put(PaymentScript.CONTEXT_ADDITIONAL_INFOS, additionalParams);

        paymentScriptInterface.doPaymentToken(scriptContext);

        PaymentResponseDto doPaymentResponseDto = new PaymentResponseDto();
        doPaymentResponseDto.setPaymentID((String) scriptContext.get(PaymentScript.RESULT_PAYMENT_ID));
        doPaymentResponseDto.setTransactionId((String) scriptContext.get(PaymentScript.RESULT_TRANSACTION_ID));
        doPaymentResponseDto.setPaymentStatus((PaymentStatusEnum) scriptContext.get(PaymentScript.RESULT_PAYMENT_STATUS));
        doPaymentResponseDto.setErrorMessage((String) scriptContext.get(PaymentScript.RESULT_ERROR_MSG));
        doPaymentResponseDto.setCodeClientSide((String) scriptContext.get(PaymentScript.RESULT_CODE_CLIENT_SIDE));
        doPaymentResponseDto.setBankRefenrence((String) scriptContext.get(PaymentScript.RESULT_BANK_REFERENCE));
        doPaymentResponseDto.setPaymentBrand((String) scriptContext.get(PaymentScript.RESULT_PAYMENT_BRAND));
        return doPaymentResponseDto;
    }

    @Override
    public PaymentResponseDto doPaymentCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expirayDate,
            CreditCardTypeEnum cardType, String countryCode, Map<String, Object> additionalParams) throws BusinessException {
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(PaymentScript.CONTEXT_PG, paymentGateway);
        scriptContext.put(PaymentScript.CONTEXT_CA, customerAccount);
        scriptContext.put(PaymentScript.CONTEXT_CARD_NUMBER, cardNumber);
        scriptContext.put(PaymentScript.CONTEXT_CARD_OWNER, ownerName);
        scriptContext.put(PaymentScript.CONTEXT_CARD_EXPIRATION, expirayDate);
        scriptContext.put(PaymentScript.CONTEXT_CARD_TYPE, cardType);
        scriptContext.put(PaymentScript.CONTEXT_COUNTRY_CODE, countryCode);
        scriptContext.put(PaymentScript.CONTEXT_ADDITIONAL_INFOS, additionalParams);
        scriptContext.put(PaymentScript.CONTEXT_CARD_CVV, cvv);

        paymentScriptInterface.doPaymentCard(scriptContext);

        PaymentResponseDto doPaymentResponseDto = new PaymentResponseDto();
        doPaymentResponseDto.setPaymentID((String) scriptContext.get(PaymentScript.RESULT_PAYMENT_ID));
        doPaymentResponseDto.setTransactionId((String) scriptContext.get(PaymentScript.RESULT_TRANSACTION_ID));
        doPaymentResponseDto.setPaymentStatus((PaymentStatusEnum) scriptContext.get(PaymentScript.RESULT_PAYMENT_STATUS));
        doPaymentResponseDto.setErrorMessage((String) scriptContext.get(PaymentScript.RESULT_ERROR_MSG));
        doPaymentResponseDto.setCodeClientSide((String) scriptContext.get(PaymentScript.RESULT_CODE_CLIENT_SIDE));
        doPaymentResponseDto.setBankRefenrence((String) scriptContext.get(PaymentScript.RESULT_BANK_REFERENCE));
        doPaymentResponseDto.setPaymentBrand((String) scriptContext.get(PaymentScript.RESULT_PAYMENT_BRAND));
        doPaymentResponseDto.setTokenId((String) scriptContext.get(PaymentScript.RESULT_TOKEN_ID));
        return doPaymentResponseDto;
    }

    @Override
    public void cancelPayment(String paymentID) throws BusinessException {

        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(PaymentScript.CONTEXT_PG, paymentGateway);
        scriptContext.put(PaymentScript.PAYMENT_ID, paymentID);       

        paymentScriptInterface.cancelPayment(scriptContext);
    }

    @Override
    public PaymentResponseDto doRefundToken(CardPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(PaymentScript.CONTEXT_PG, paymentGateway);
        scriptContext.put(PaymentScript.CONTEXT_TOKEN, paymentToken);
        scriptContext.put(PaymentScript.CONTEXT_AMOUNT_CTS, ctsAmount);
        scriptContext.put(PaymentScript.CONTEXT_ADDITIONAL_INFOS, additionalParams);

        paymentScriptInterface.doRefundToken(scriptContext);

        PaymentResponseDto doPaymentResponseDto = new PaymentResponseDto();
        doPaymentResponseDto.setPaymentID((String) scriptContext.get(PaymentScript.RESULT_PAYMENT_ID));
        doPaymentResponseDto.setTransactionId((String) scriptContext.get(PaymentScript.RESULT_TRANSACTION_ID));
        doPaymentResponseDto.setPaymentStatus((PaymentStatusEnum) scriptContext.get(PaymentScript.RESULT_PAYMENT_STATUS));
        doPaymentResponseDto.setErrorMessage((String) scriptContext.get(PaymentScript.RESULT_ERROR_MSG));
        doPaymentResponseDto.setCodeClientSide((String) scriptContext.get(PaymentScript.RESULT_CODE_CLIENT_SIDE));
        doPaymentResponseDto.setBankRefenrence((String) scriptContext.get(PaymentScript.RESULT_BANK_REFERENCE));
        doPaymentResponseDto.setPaymentBrand((String) scriptContext.get(PaymentScript.RESULT_PAYMENT_BRAND));
        return doPaymentResponseDto;
    }

    @Override
    public void doBulkPaymentAsService(DDRequestLOT ddRequestLot) throws BusinessException {
        // TODO PaymentRun
    }

    @Override
    public PaymentResponseDto doRefundCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expirayDate,
            CreditCardTypeEnum cardType, String countryCode, Map<String, Object> additionalParams) throws BusinessException {
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(PaymentScript.CONTEXT_PG, paymentGateway);
        scriptContext.put(PaymentScript.CONTEXT_CA, customerAccount);
        scriptContext.put(PaymentScript.CONTEXT_CARD_NUMBER, cardNumber);
        scriptContext.put(PaymentScript.CONTEXT_CARD_OWNER, ownerName);
        scriptContext.put(PaymentScript.CONTEXT_CARD_EXPIRATION, expirayDate);
        scriptContext.put(PaymentScript.CONTEXT_CARD_TYPE, cardType);
        scriptContext.put(PaymentScript.CONTEXT_COUNTRY_CODE, countryCode);
        scriptContext.put(PaymentScript.CONTEXT_ADDITIONAL_INFOS, additionalParams);
        scriptContext.put(PaymentScript.CONTEXT_CARD_CVV, cvv);

        paymentScriptInterface.doRefundCard(scriptContext);

        PaymentResponseDto doPaymentResponseDto = new PaymentResponseDto();
        doPaymentResponseDto.setPaymentID((String) scriptContext.get(PaymentScript.RESULT_PAYMENT_ID));
        doPaymentResponseDto.setTransactionId((String) scriptContext.get(PaymentScript.RESULT_TRANSACTION_ID));
        doPaymentResponseDto.setPaymentStatus((PaymentStatusEnum) scriptContext.get(PaymentScript.RESULT_PAYMENT_STATUS));
        doPaymentResponseDto.setErrorMessage((String) scriptContext.get(PaymentScript.RESULT_ERROR_MSG));
        doPaymentResponseDto.setCodeClientSide((String) scriptContext.get(PaymentScript.RESULT_CODE_CLIENT_SIDE));
        doPaymentResponseDto.setBankRefenrence((String) scriptContext.get(PaymentScript.RESULT_BANK_REFERENCE));
        doPaymentResponseDto.setPaymentBrand((String) scriptContext.get(PaymentScript.RESULT_PAYMENT_BRAND));
        doPaymentResponseDto.setTokenId((String) scriptContext.get(PaymentScript.RESULT_TOKEN_ID));
        return doPaymentResponseDto;
    }

    @Override
    public PaymentResponseDto doPaymentSepa(DDPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(PaymentScript.CONTEXT_PG, paymentGateway);
        scriptContext.put(PaymentScript.CONTEXT_TOKEN, paymentToken);
        scriptContext.put(PaymentScript.CONTEXT_AMOUNT_CTS, ctsAmount);
        scriptContext.put(PaymentScript.CONTEXT_ADDITIONAL_INFOS, additionalParams);

        paymentScriptInterface.doPaymentSepa(scriptContext);

        PaymentResponseDto doPaymentResponseDto = new PaymentResponseDto();
        doPaymentResponseDto.setPaymentID((String) scriptContext.get(PaymentScript.RESULT_PAYMENT_ID));
        doPaymentResponseDto.setTransactionId((String) scriptContext.get(PaymentScript.RESULT_TRANSACTION_ID));
        doPaymentResponseDto.setPaymentStatus((PaymentStatusEnum) scriptContext.get(PaymentScript.RESULT_PAYMENT_STATUS));
        doPaymentResponseDto.setErrorMessage((String) scriptContext.get(PaymentScript.RESULT_ERROR_MSG));
        doPaymentResponseDto.setCodeClientSide((String) scriptContext.get(PaymentScript.RESULT_CODE_CLIENT_SIDE));
        doPaymentResponseDto.setBankRefenrence((String) scriptContext.get(PaymentScript.RESULT_BANK_REFERENCE));
        doPaymentResponseDto.setPaymentBrand((String) scriptContext.get(PaymentScript.RESULT_PAYMENT_BRAND));
        return doPaymentResponseDto;
    }
    
    @Override
    public MandatInfoDto checkMandat(String mandatReference, String mandateId) throws BusinessException {
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(PaymentScript.CONTEXT_PG, paymentGateway);
        scriptContext.put(PaymentScript.CONTEXT_MANDAT_REF, mandatReference);
        scriptContext.put(PaymentScript.CONTEXT_MANDAT_ID, mandateId);

        paymentScriptInterface.checkMandat(scriptContext);

        MandatInfoDto mandatInfoDto = new MandatInfoDto();
        mandatInfoDto.setId((String) scriptContext.get(PaymentScript.RESULT_MANDAT_ID));
        mandatInfoDto.setReference((String) scriptContext.get(PaymentScript.RESULT_MANDAT_REF));
        mandatInfoDto.setState((MandatStateEnum) scriptContext.get(PaymentScript.RESULT_STATE));
        mandatInfoDto.setStandard((String) scriptContext.get(PaymentScript.RESULT_STANDARD));
        mandatInfoDto.setInitialScore((int) scriptContext.get(PaymentScript.RESULT_INT_SCORE));
        mandatInfoDto.setDateCreated((Date) scriptContext.get(PaymentScript.RESULT_DATE_CREATED));
        mandatInfoDto.setDateSigned((Date) scriptContext.get(PaymentScript.RESULT_DATE_SIGNED));
        mandatInfoDto.setPaymentScheme((String) scriptContext.get(PaymentScript.RESULT_SCHEME));
        mandatInfoDto.setBic((String) scriptContext.get(PaymentScript.RESULT_BIC));
        mandatInfoDto.setIban((String) scriptContext.get(PaymentScript.RESULT_IBAN));
        mandatInfoDto.setBankName((String) scriptContext.get(PaymentScript.RESULT_BANK_NAME));
        return mandatInfoDto;        
    }

    @Override
    public PaymentResponseDto doRefundSepa(DDPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(PaymentScript.CONTEXT_PG, paymentGateway);
        scriptContext.put(PaymentScript.CONTEXT_TOKEN, paymentToken);
        scriptContext.put(PaymentScript.CONTEXT_AMOUNT_CTS, ctsAmount);
        scriptContext.put(PaymentScript.CONTEXT_ADDITIONAL_INFOS, additionalParams);

        paymentScriptInterface.doRefundSepa(scriptContext);

        PaymentResponseDto doPaymentResponseDto = new PaymentResponseDto();
        doPaymentResponseDto.setPaymentID((String) scriptContext.get(PaymentScript.RESULT_PAYMENT_ID));
        doPaymentResponseDto.setTransactionId((String) scriptContext.get(PaymentScript.RESULT_TRANSACTION_ID));
        doPaymentResponseDto.setPaymentStatus((PaymentStatusEnum) scriptContext.get(PaymentScript.RESULT_PAYMENT_STATUS));
        doPaymentResponseDto.setErrorMessage((String) scriptContext.get(PaymentScript.RESULT_ERROR_MSG));
        doPaymentResponseDto.setCodeClientSide((String) scriptContext.get(PaymentScript.RESULT_CODE_CLIENT_SIDE));
        doPaymentResponseDto.setBankRefenrence((String) scriptContext.get(PaymentScript.RESULT_BANK_REFERENCE));
        doPaymentResponseDto.setPaymentBrand((String) scriptContext.get(PaymentScript.RESULT_PAYMENT_BRAND));
        return doPaymentResponseDto;    
    }


    @Override
    public PaymentResponseDto checkPayment(String paymentID, PaymentMethodEnum paymentMethodType) throws BusinessException {
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(PaymentScript.CONTEXT_PG, paymentGateway);
        scriptContext.put(PaymentScript.CONTEXT_PAYMENT_ID, paymentID); 
        scriptContext.put(PaymentScript.CONTEXT_PAYMENT_METHOD_TYPE, paymentMethodType);

        paymentScriptInterface.checkPayment(scriptContext);

        PaymentResponseDto doPaymentResponseDto = new PaymentResponseDto();
        doPaymentResponseDto.setPaymentID((String) scriptContext.get(PaymentScript.RESULT_PAYMENT_ID));
        doPaymentResponseDto.setTransactionId((String) scriptContext.get(PaymentScript.RESULT_TRANSACTION_ID));
        doPaymentResponseDto.setPaymentStatus((PaymentStatusEnum) scriptContext.get(PaymentScript.RESULT_PAYMENT_STATUS));
        doPaymentResponseDto.setErrorMessage((String) scriptContext.get(PaymentScript.RESULT_ERROR_MSG));
        doPaymentResponseDto.setCodeClientSide((String) scriptContext.get(PaymentScript.RESULT_CODE_CLIENT_SIDE));
        doPaymentResponseDto.setBankRefenrence((String) scriptContext.get(PaymentScript.RESULT_BANK_REFERENCE));
        doPaymentResponseDto.setPaymentBrand((String) scriptContext.get(PaymentScript.RESULT_PAYMENT_BRAND));
        return doPaymentResponseDto;    
    }

    @Override
    public PaymentHostedCheckoutResponseDto getHostedCheckoutUrl(HostedCheckoutInput hostedCheckoutInput) throws BusinessException {
    	 Map<String, Object> scriptContext = new HashMap<>();
         scriptContext.put(PaymentScript.CONTEXT_PG, paymentGateway);
         scriptContext.put(PaymentScript.CONTEXT_HOSTED_CO, hostedCheckoutInput);

         paymentScriptInterface.getHostedCheckoutUrl(scriptContext);

         String hostedCheckoutUrl = (String) scriptContext.get(PaymentScript.RESULT_HOSTED_CO_URL);

         return new PaymentHostedCheckoutResponseDto(hostedCheckoutUrl, null, null);
    }

    @Override
    public void setPaymentGateway(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

	@Override
	public String createInvoice(Invoice invoice) throws BusinessException {
		 Map<String, Object> scriptContext = new HashMap<String, Object>();
         scriptContext.put(PaymentScript.CONTEXT_PG, paymentGateway);
         scriptContext.put("INVOICE", invoice);
        
         paymentScriptInterface.createInvoice(scriptContext);
  
         String hostedCheckoutUrl = (String) scriptContext.get(PaymentScript.RESULT_HOSTED_CO_URL);
        
         return hostedCheckoutUrl;
	}

}
