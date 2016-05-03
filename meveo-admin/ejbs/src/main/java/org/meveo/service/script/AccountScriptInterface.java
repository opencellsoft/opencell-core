package org.meveo.service.script;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;

/**
 * @author Edward P. Legaspi
 **/
public interface AccountScriptInterface extends ScriptInterface {

	void createAccount(Map<String, Object> methodContext, User currentUser) throws BusinessException;

	void updateAccount(Map<String, Object> methodContext, User currentUser) throws BusinessException;

	void terminateAccount(Map<String, Object> methodContext, User currentUser) throws BusinessException;

	void closeAccount(Map<String, Object> methodContext, User currentUser) throws BusinessException;

}
