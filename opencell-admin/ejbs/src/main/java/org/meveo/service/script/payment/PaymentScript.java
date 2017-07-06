package org.meveo.service.script.payment;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.script.Script;

public class PaymentScript extends Script implements PaymentScriptInterface {

    public static String RESULT_CODE_CLIENT_SIDE = "RESULT_CODE_CLIENT_SIDE";
	public static String RESULT_PAYMENT_BRAND = "RESULT_PAYMENT_BRAND";
	public static String RESULT_BANK_REFERENCE = "RESULT_BANK_REFERENCE";
	public static String RESULT_ERROR_MSG = "RESULT_ERROR_MSG";
	public static String RESULT_TRANSACTION_ID = "RESULT_TRANSACTION_ID";
	public static String RESULT_PAYMENT_STATUS = "RESULT_PAYMENT_STATUS";
	public static String RESULT_TOKEN = "RESULT_TOKEN";
	public static String RESULT_PAYMENT_ID = "RESULT_PAYMENT_ID";
	public static String CONTEXT_TOKEN = "CONTEXT_TOKEN";
    public static String CONTEXT_AMOUNT_CTS = "CONTEXT_AMOUNT_CTS";
    public static String CONTEXT_ADDITIONAL_INFOS = "CONTEXT_ADDITIONAL_INFOS";
    public static String CONTEXT_CA = "CONTEXT_CA";
	
	@Override
	public void doPaymentToken(Map<String, Object> methodContext) throws BusinessException {		
	}

	@Override
	public void createCardToken(Map<String, Object> methodContext) throws BusinessException {		
	}
	
	
  
}
