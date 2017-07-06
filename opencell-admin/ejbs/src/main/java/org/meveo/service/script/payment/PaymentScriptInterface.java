package org.meveo.service.script.payment;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.script.ScriptInterface;

/**
 * @author anasseh
 **/
public interface PaymentScriptInterface extends ScriptInterface {

	/**
	 * Called after Account entity creation
	 * 
	 * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=AccountEntity and CONTEXT_SELLER=The current seller..
	 * @throws BusinessException
	 */
    void doPaymentToken(Map<String, Object> methodContext) throws BusinessException;
    
	/**
	 * Called after Account entity creation
	 * 
	 * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=AccountEntity and CONTEXT_SELLER=The current seller..
	 * @throws BusinessException
	 */
    void createCardToken(Map<String, Object> methodContext) throws BusinessException;

   
}