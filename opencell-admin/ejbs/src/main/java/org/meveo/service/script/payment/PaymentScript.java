package org.meveo.service.script.payment;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.script.Script;

// TODO: Auto-generated Javadoc
/**
 * The Class PaymentScript.
 */
public class PaymentScript extends Script implements PaymentScriptInterface {

    /** The Constant CONTEXT_ALIAS. */
    public static final String CONTEXT_ALIAS = "CONTEXT_ALIAS";
    
    /** The Constant CONTEXT_CARD_NUMBER. */
    public static final String CONTEXT_CARD_NUMBER = "CONTEXT_CARD_NUMBER";
    
    /** The Constant CONTEXT_CARD_OWNER. */
    public static final String CONTEXT_CARD_OWNER = "CONTEXT_CARD_OWNER";
    
    /** The Constant CONTEXT_CARD_EXPIRATION. */
    public static final String CONTEXT_CARD_EXPIRATION = "CONTEXT_CARD_EXPIRATION";
    
    /** The Constant CONTEXT_CARD_TYPE. */
    public static final String CONTEXT_CARD_TYPE = "CONTEXT_CARD_TYPE";
    
    /** The Constant CONTEXT_ISSUE_NUMBER. */
    public static final String CONTEXT_ISSUE_NUMBER = "CONTEXT_ISSUE_NUMBER";
    
    /** The Constant CONTEXT_COUNTRY_CODE. */
    public static final String CONTEXT_COUNTRY_CODE = "CONTEXT_COUNTRY_CODE";
    
    /** The Constant CONTEXT_CARD_CVV. */
    public static final String CONTEXT_CARD_CVV = "CONTEXT_CARD_CVV";
    
    /** The Constant CONTEXT_PM. */
    public static final String CONTEXT_PM = "CONTEXT_PM";
    
    /** The Constant RESULT_CODE_CLIENT_SIDE. */
    public static final String RESULT_CODE_CLIENT_SIDE = "RESULT_CODE_CLIENT_SIDE";
    
    /** The Constant RESULT_PAYMENT_BRAND. */
    public static final String RESULT_PAYMENT_BRAND = "RESULT_PAYMENT_BRAND";
    
    /** The Constant RESULT_BANK_REFERENCE. */
    public static final String RESULT_BANK_REFERENCE = "RESULT_BANK_REFERENCE";
    
    /** The Constant RESULT_ERROR_MSG. */
    public static final String RESULT_ERROR_MSG = "RESULT_ERROR_MSG";
    
    /** The Constant RESULT_TRANSACTION_ID. */
    public static final String RESULT_TRANSACTION_ID = "RESULT_TRANSACTION_ID";
    
    /** The Constant RESULT_PAYMENT_STATUS. */
    public static final String RESULT_PAYMENT_STATUS = "RESULT_PAYMENT_STATUS";
    
    /** The Constant RESULT_TOKEN. */
    public static final String RESULT_TOKEN = "RESULT_TOKEN";
    
    /** The Constant RESULT_PAYMENT_ID. */
    public static final String RESULT_PAYMENT_ID = "RESULT_PAYMENT_ID";
    
    /** The Constant CONTEXT_TOKEN. */
    public static final String CONTEXT_TOKEN = "CONTEXT_TOKEN";
    
    /** The Constant CONTEXT_AMOUNT_CTS. */
    public static final String CONTEXT_AMOUNT_CTS = "CONTEXT_AMOUNT_CTS";
    
    /** The Constant CONTEXT_ADDITIONAL_INFOS. */
    public static final String CONTEXT_ADDITIONAL_INFOS = "CONTEXT_ADDITIONAL_INFOS";
    
    /** The Constant CONTEXT_CA. */
    public static final String CONTEXT_CA = "CONTEXT_CA";
    
    /** The Constant RESULT_TOKEN_ID. */
    public static final Object RESULT_TOKEN_ID = "RESULT_TOKEN_ID";

    @Override
    public void doPaymentCard(Map<String, Object> methodContext) throws BusinessException {
    }

    @Override
    public void cancelPayment(Map<String, Object> methodContext) throws BusinessException {
    }

    public void doRefundToken(Map<String, Object> methodContext) throws BusinessException {
    }

    @Override
    public void doRefundCard(Map<String, Object> methodContext) throws BusinessException {
    }

    @Override
    public void doPaymentToken(Map<String, Object> methodContext) throws BusinessException {
    }

    @Override
    public void createCardToken(Map<String, Object> methodContext) throws BusinessException {
    }

}