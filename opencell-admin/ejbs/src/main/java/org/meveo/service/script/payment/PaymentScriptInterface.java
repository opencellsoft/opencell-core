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

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
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
    
    }