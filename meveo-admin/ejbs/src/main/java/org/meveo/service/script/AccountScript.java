package org.meveo.service.script;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;

/**
 * @author Edward P. Legaspi
 **/
public class AccountScript extends Script implements AccountScriptInterface {
	
	public static String CONTEXT_SELLER = "CONTEXT_SELLER";

	@Override
	public void createAccount(Map<String, Object> methodContext, User currentUser) throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateAccount(Map<String, Object> methodContext, User currentUser) throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void terminateAccount(Map<String, Object> methodContext, User currentUser) throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeAccount(Map<String, Object> methodContext, User currentUser) throws BusinessException {
		// TODO Auto-generated method stub
		
	}

}
