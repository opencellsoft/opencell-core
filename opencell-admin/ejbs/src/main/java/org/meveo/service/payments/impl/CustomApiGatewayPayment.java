package org.meveo.service.payments.impl;

import java.util.HashMap;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.payment.PayByCardResponseDto;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.service.script.payment.PaymentScript;
import org.meveo.service.script.payment.PaymentScriptInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomApiGatewayPayment implements GatewayPaymentInterface {

    protected Logger log = LoggerFactory.getLogger(CustomApiGatewayPayment.class);
    private PaymentScriptInterface paymentScriptInterface;

    public CustomApiGatewayPayment(PaymentScriptInterface paymentScriptInterface) {
        this.paymentScriptInterface = paymentScriptInterface;
    }

    @Override
    public String createCardToken(CustomerAccount customerAccount, String alias, String cardNumber, String cardHolderName, String expirayDate, String issueNumber,
            CreditCardTypeEnum cardType, String countryCode) throws BusinessException {
        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(PaymentScript.CONTEXT_CA, customerAccount);
        scriptContext.put(PaymentScript.CONTEXT_ALIAS, alias);
        scriptContext.put(PaymentScript.CONTEXT_CARD_NUMBER, cardNumber);
        scriptContext.put(PaymentScript.CONTEXT_CARD_OWNER, cardHolderName);
        scriptContext.put(PaymentScript.CONTEXT_CARD_EXPIRATION, expirayDate);
        scriptContext.put(PaymentScript.CONTEXT_CARD_TYPE, cardType);
        scriptContext.put(PaymentScript.CONTEXT_ISSUE_NUMBER, issueNumber);
        scriptContext.put(PaymentScript.CONTEXT_COUNTRY_CODE, countryCode);

        paymentScriptInterface.createCardToken(scriptContext);

        return (String) scriptContext.get(PaymentScript.RESULT_TOKEN);
    }

    @Override
    public PayByCardResponseDto doPaymentToken(CardPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(PaymentScript.CONTEXT_TOKEN, paymentToken);
        scriptContext.put(PaymentScript.CONTEXT_AMOUNT_CTS, ctsAmount);
        scriptContext.put(PaymentScript.CONTEXT_ADDITIONAL_INFOS, additionalParams);

        paymentScriptInterface.doPaymentToken(scriptContext);

        PayByCardResponseDto doPaymentResponseDto = new PayByCardResponseDto();
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
    public PayByCardResponseDto doPaymentCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expirayDate,
            CreditCardTypeEnum cardType, String countryCode, Map<String, Object> additionalParams) throws BusinessException {
        return null;
    }

    @Override
    public void cancelPayment(String paymentID) throws BusinessException {

    }

    @Override
    public PayByCardResponseDto doRefundToken(CardPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(PaymentScript.CONTEXT_TOKEN, paymentToken);
        scriptContext.put(PaymentScript.CONTEXT_AMOUNT_CTS, ctsAmount);
        scriptContext.put(PaymentScript.CONTEXT_ADDITIONAL_INFOS, additionalParams);

        paymentScriptInterface.doRefundToken(scriptContext);

        PayByCardResponseDto doPaymentResponseDto = new PayByCardResponseDto();
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
    public void doBulkPaymentAsFile(DDRequestLOT ddRequestLot) throws BusinessException {
        // TODO PaymentRun
    }

    @Override
    public void doBulkPaymentAsService(DDRequestLOT ddRequestLot) throws BusinessException {
        // TODO PaymentRun
    }

    @Override
    public PayByCardResponseDto doRefundCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expirayDate,
            CreditCardTypeEnum cardType, String countryCode, Map<String, Object> additionalParams) throws BusinessException {
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(PaymentScript.CONTEXT_CA, customerAccount);
        scriptContext.put(PaymentScript.CONTEXT_CARD_NUMBER, cardNumber);
        scriptContext.put(PaymentScript.CONTEXT_CARD_OWNER, ownerName);
        scriptContext.put(PaymentScript.CONTEXT_CARD_EXPIRATION, expirayDate);
        scriptContext.put(PaymentScript.CONTEXT_CARD_TYPE, cardType);
        scriptContext.put(PaymentScript.CONTEXT_COUNTRY_CODE, countryCode);
        scriptContext.put(PaymentScript.CONTEXT_ADDITIONAL_INFOS, additionalParams);
        scriptContext.put(PaymentScript.CONTEXT_CARD_CVV, cvv);

        paymentScriptInterface.doRefundCard(scriptContext);

        PayByCardResponseDto doPaymentResponseDto = new PayByCardResponseDto();
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

}
