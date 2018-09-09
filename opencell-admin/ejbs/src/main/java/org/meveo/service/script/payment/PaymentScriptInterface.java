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
    
    }