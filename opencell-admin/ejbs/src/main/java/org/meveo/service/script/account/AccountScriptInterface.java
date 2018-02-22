package org.meveo.service.script.account;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.script.ScriptInterface;

/**
 * @author Edward P. Legaspi
 **/
public interface AccountScriptInterface extends ScriptInterface {

	/**
	 * Called after Account entity creation
	 * 
	 * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=AccountEntity and CONTEXT_SELLER=The current seller..
	 * @throws BusinessException business exception.
	 */
    void createAccount(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Called after Account entity update.
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=AccountEntity and CONTEXT_SELLER=The current seller..
     * @throws BusinessException business exception.
     */
    void updateAccount(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Called after either Billing or User account termination.
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=BillingAccount or a UserAccount.
     * @throws BusinessException business exception.
     */
    void terminateAccount(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Called after closing of a Customer Account.
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=CustomerAccount.
     * @throws BusinessException business exception.
     */
    void closeAccount(Map<String, Object> methodContext) throws BusinessException;
}