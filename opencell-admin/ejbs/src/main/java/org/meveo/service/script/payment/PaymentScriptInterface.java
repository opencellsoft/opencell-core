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
import org.meveo.service.script.ScriptInterface;

/**
 * The Interface PaymentScriptInterface.
 *
 * @author anasseh
 * @lastModifiedVersion 5.2
 */
public interface PaymentScriptInterface extends ScriptInterface {

    /**
     * Creates the card token.
     *
     * @param methodContext the method context
     * @throws BusinessException the business exception
     */
    void createCardToken(Map<String, Object> methodContext) throws BusinessException;
    
    /**
     * Do payment with token.
     *
     * @param methodContext the method context
     * @throws BusinessException the business exception
     */
    void doPaymentToken(Map<String, Object> methodContext) throws BusinessException;
    
    /**
     * Do payment with card.
     *
     * @param methodContext the method context
     * @throws BusinessException the business exception
     */
    void doPaymentCard(Map<String, Object> methodContext) throws BusinessException;
    
    /**
     * Cancel payment.
     *
     * @param methodContext the method context
     * @throws BusinessException the business exception
     */
    void cancelPayment(Map<String, Object> methodContext) throws BusinessException;
    
    /**
     * Do refund with token.
     *
     * @param methodContext the method context
     * @throws BusinessException the business exception
     */
    void doRefundToken(Map<String, Object> methodContext) throws BusinessException;
    
    /**
     * Do refund with card.
     *
     * @param methodContext the method context
     * @throws BusinessException the business exception
     */
    void doRefundCard(Map<String, Object> methodContext) throws BusinessException;
    
    /**
     * Do payment with mandat.
     *
     * @param methodContext the method context
     * @throws BusinessException the business exception
     */
    void doPaymentSepa(Map<String, Object> methodContext) throws BusinessException;
    
    /**
     * Do refund with mandat.
     *
     * @param methodContext the method context
     * @throws BusinessException the business exception
     */
    void doRefundSepa(Map<String, Object> methodContext) throws BusinessException;
    
    /**
     * Check payment.
     *
     * @param methodContext the method context
     * @throws BusinessException the business exception
     */
    void checkPayment(Map<String, Object> methodContext) throws BusinessException;
    
    /**
     * Check mandat.
     *
     * @param methodContext the method context
     * @throws BusinessException the business exception
     */
    void checkMandat(Map<String, Object> methodContext) throws BusinessException;
    
    /**
     * get HostedCheckoutUrl.
     *
     * @param methodContext the method context
     * @throws BusinessException the business exception
     */
    void getHostedCheckoutUrl(Map<String, Object> methodContext) throws BusinessException;
    
    void createInvoice(Map<String, Object> methodContext) throws BusinessException;
    /**
     * Declare a sepa direct debit on the psp and return the token for the future uses.
     * 
     * @param customerAccount customer account.
     * @param alias An alias for the token. This can be used to visually represent the token.If no alias is given in Create token calls, a payment product specific default is used, e.g. the obfuscated card number for card payment products.
              Do not include any unobfuscated sensitive data in the alias.
     * @param accountHolderName Name in which the account is held 
     * @param iban The IBAN is the International Bank Account Number,is required for Create and Update token
     * @return sepa token.
     * @throws BusinessException business exception
     */
    void createSepaDirectDebitToken(Map<String, Object> methodContext) throws BusinessException;
    
    /**
     * Creates a mandate to be used in a SEPA Direct Debit payment.
     * 
     * @param customerAccount customer account. 
     * @param iban The IBAN is the International Bank Account Number,is required for Create and Update token 
     * @param mandateReference mandate reference.
     * @throws BusinessException business exception
     */
     void createMandate(Map<String, Object> methodContext) throws BusinessException;
    
    /**
     * approve a mandate to be used in a SEPA Direct Debit payment.
     * 
     * @param token 
     * @param iban The IBAN is the International Bank Account Number,is required for Create and Update token 
     * @throws BusinessException business exception
     */
     void approveSepaDDMandate(Map<String, Object> methodContext) throws BusinessException;
    
    }