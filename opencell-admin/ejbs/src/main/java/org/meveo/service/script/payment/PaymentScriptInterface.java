package org.meveo.service.script.payment;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.script.ScriptInterface;

/**
 * The Interface PaymentScriptInterface.
 *
 * @author anasseh
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

   
}