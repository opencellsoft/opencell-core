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

package org.meveo.service.script.payment;

import java.util.Date;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.script.Script;

/**
 * The Class PaymentScript.
 * 
 * @author anasseh
 * @author Rachid-AIT
 * @lastModifiedVersion 10.0.0
 */
public class PaymentScript extends Script implements PaymentScriptInterface {

    private static final long serialVersionUID = 2133629637888034337L;

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
    public static final String RESULT_TOKEN_ID = "RESULT_TOKEN_ID";

    /** The Constant CONTEXT_PAYMENT_ID. */
    public static final String CONTEXT_PAYMENT_ID = "CONTEXT_PAYMENT_ID";

    /** The Constant CONTEXT_PAYMENT_METHOD_TYPE. */
    public static final String CONTEXT_PAYMENT_METHOD_TYPE = "CONTEXT_PAYMENT_METHOD_TYPE";

    /** The Constant CONTEXT_MANDAT_REF. */
    public static final String CONTEXT_MANDAT_REF = "CONTEXT_MANDAT_REF";

    /** The Constant CONTEXT_MANDAT_ID. */
    public static final String CONTEXT_MANDAT_ID = "CONTEXT_MANDAT_ID";

    /** The Constant RESULT_MANDAT_ID. */
    public static final String RESULT_MANDAT_ID = "RESULT_MANDAT_ID";

    /** The Constant RESULT_STATE. */
    public static final String RESULT_STATE = "RESULT_STATE";

    /** The Constant RESULT_STANDARD. */
    public static final String RESULT_STANDARD = "RESULT_STANDARD";

    /** The Constant RESULT_MANDAT_REF. */
    public static final String RESULT_MANDAT_REF = "RESULT_MANDAT_REF";

    /** The Constant RESULT_INT_SCORE. */
    public static final String RESULT_INT_SCORE = "RESULT_INT_SCORE";

    /** The Constant RESULT_DATE_CREATED. */
    public static final String RESULT_DATE_CREATED = "RESULT_DATE_CREATED";

    /** The Constant RESULT_DATE_SIGNED. */
    public static final String RESULT_DATE_SIGNED = "RESULT_DATE_SIGNED";

    /** The Constant RESULT_SCHEME. */
    public static final String RESULT_SCHEME = "RESULT_SCHEME";

    /** The Constant RESULT_BIC. */
    public static final String RESULT_BIC = "RESULT_BIC";

    /** The Constant RESULT_IBAN. */
    public static final String RESULT_IBAN = "RESULT_IBAN";

    /** The Constant RESULT_BANK_NAME. */
    public static final String RESULT_BANK_NAME = "RESULT_BANK_NAME";

    /** The Constant CONTEXT_PG. */
    public static final String CONTEXT_PG = "PAYMENT_GATEWAY";

    /** The Constant PAYMENT_ID. */
    public static final String PAYMENT_ID = "PAYMENT_ID";

    /** The Constant CONTEXT_HOSTED_CO for hostedCheckoutInput. */
	public static final String CONTEXT_HOSTED_CO = "CONTEXT_HOSTED_CO";
	
	/** The Constant RESULT_HOSTED_CO_URL for  hostedCheckout url result. */
	public static final String RESULT_HOSTED_CO_URL = "RESULT_HOSTED_CO_URL";
	
	 /** The Constant CONTEXT_ACCOUNT_HOLDER_NAME. */
    public static final String CONTEXT_ACCOUNT_HOLDER_NAME = "CONTEXT_ACCOUNT_HOLDER_NAME";
    
    /** The Constant CONTEXT_IBAN. */
    public static final String CONTEXT_IBAN = "CONTEXT_IBAN";
    
    /** The Constant CONTEXT_BIC. */
    public static final String CONTEXT_BIC = "CONTEXT_BIC";

    /** The Constant MANDATE_REFERENCE. */
	public static final String CONTEXT_MANDATE_REFERENCE = "MANDATE_REFERENCE";

	 /** The Constant Mandate SIGNATURE_DATE. */
	public static final String CONTEXT_SIGNATURE_DATE = "SIGNATURE_DATE";



    @Override
    public void doPaymentCard(Map<String, Object> methodContext) throws BusinessException {
    }

    @Override
    public void doPaymentSepa(Map<String, Object> methodContext) throws BusinessException {
    }

    @Override
    public void cancelPayment(Map<String, Object> methodContext) throws BusinessException {
    }

    @Override
    public void doRefundToken(Map<String, Object> methodContext) throws BusinessException {
    }

    @Override
    public void doRefundSepa(Map<String, Object> methodContext) throws BusinessException {
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

    @Override
    public void checkPayment(Map<String, Object> methodContext) throws BusinessException {
    }

    @Override
    public void checkMandat(Map<String, Object> methodContext) throws BusinessException {
    }
	@Override
	public void getHostedCheckoutUrl(Map<String, Object> methodContext) throws BusinessException {		
	}

	@Override
	public void createInvoice(Map<String, Object> methodContext) throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createSepaDirectDebitToken(Map<String, Object> methodContext) throws BusinessException {
		
	}

	@Override
	public void createMandate(Map<String, Object> methodContext) throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void approveSepaDDMandate(Map<String, Object> methodContext) throws BusinessException {
		// TODO Auto-generated method stub
		
	}


}