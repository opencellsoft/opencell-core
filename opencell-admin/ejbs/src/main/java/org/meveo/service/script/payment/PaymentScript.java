package org.meveo.service.script.payment;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.script.Script;


/**
 * The Class PaymentScript.
 */
public class PaymentScript extends Script implements PaymentScriptInterface {

    /** The result code client side. */
    public static String RESULT_CODE_CLIENT_SIDE = "RESULT_CODE_CLIENT_SIDE";
    
    /** The result payment brand. */
    public static String RESULT_PAYMENT_BRAND = "RESULT_PAYMENT_BRAND";
    
    /** The result bank reference. */
    public static String RESULT_BANK_REFERENCE = "RESULT_BANK_REFERENCE";
    
    /** The result error msg. */
    public static String RESULT_ERROR_MSG = "RESULT_ERROR_MSG";
    
    /** The result transaction id. */
    public static String RESULT_TRANSACTION_ID = "RESULT_TRANSACTION_ID";
    
    /** The result payment status. */
    public static String RESULT_PAYMENT_STATUS = "RESULT_PAYMENT_STATUS";
    
    /** The result token. */
    public static String RESULT_TOKEN = "RESULT_TOKEN";
    
    /** The result payment id. */
    public static String RESULT_PAYMENT_ID = "RESULT_PAYMENT_ID";
    
    /** The context token. */
    public static String CONTEXT_TOKEN = "CONTEXT_TOKEN";
    
    /** The context amount cts. */
    public static String CONTEXT_AMOUNT_CTS = "CONTEXT_AMOUNT_CTS";
    
    /** The context additional infos. */
    public static String CONTEXT_ADDITIONAL_INFOS = "CONTEXT_ADDITIONAL_INFOS";
    
    /** The context ca. */
    public static String CONTEXT_CA = "CONTEXT_CA";


    @Override
    public void doPaymentToken(Map<String, Object> methodContext) throws BusinessException {		
    }

    @Override
    public void createCardToken(Map<String, Object> methodContext) throws BusinessException {		
    }

}
