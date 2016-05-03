package org.meveo.service.crm.impl;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.AccountEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.crm.AccountModelScript;
import org.meveo.service.script.AccountScript;
import org.meveo.service.script.AccountScriptInterface;
import org.meveo.service.script.CustomScriptService;
import org.meveo.service.script.Script;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class AccountModelScriptService extends CustomScriptService<AccountModelScript, AccountScriptInterface> {

	/**
	 * Find the class name in a source java text
	 * 
	 * @param src
	 *            Java source code
	 * @return Class name
	 */
	public String getClassName(String src) {
		String className = StringUtils.patternMacher("public class (.*) extends", src);
		if (className == null) {
			className = StringUtils.patternMacher("public class (.*) implements", src);
		}
		return className;
	}

	public void createAccount(String scriptCode, Seller seller, AccountEntity account, User currentUser) throws BusinessException {
		AccountScriptInterface scriptInterface = getScriptInstance(currentUser.getProvider(), scriptCode);
		Map<String, Object> scriptContext = new HashMap<String, Object>();
		scriptContext.put(Script.CONTEXT_ENTITY, account);
		scriptContext.put(AccountScript.CONTEXT_SELLER, seller);
		scriptInterface.createAccount(scriptContext, currentUser);
	}

	public void updateAccount(String scriptCode, Seller seller, AccountEntity account, User currentUser) throws BusinessException {
		AccountScriptInterface scriptInterface = getScriptInstance(currentUser.getProvider(), scriptCode);
		Map<String, Object> scriptContext = new HashMap<String, Object>();
		scriptContext.put(Script.CONTEXT_ENTITY, account);
		scriptContext.put(AccountScript.CONTEXT_SELLER, seller);
		scriptInterface.updateAccount(scriptContext, currentUser);
	}

	public void terminateAccount(String scriptCode, Seller seller, AccountEntity account, User currentUser) throws BusinessException {
		AccountScriptInterface scriptInterface = getScriptInstance(currentUser.getProvider(), scriptCode);
		Map<String, Object> scriptContext = new HashMap<String, Object>();
		scriptContext.put(Script.CONTEXT_ENTITY, account);
		scriptContext.put(AccountScript.CONTEXT_SELLER, seller);
		scriptInterface.terminateAccount(scriptContext, currentUser);
	}

	public void closeAccount(String scriptCode, Seller seller, AccountEntity account, User currentUser) throws BusinessException {
		AccountScriptInterface scriptInterface = getScriptInstance(currentUser.getProvider(), scriptCode);
		Map<String, Object> scriptContext = new HashMap<String, Object>();
		scriptContext.put(Script.CONTEXT_ENTITY, account);
		scriptContext.put(AccountScript.CONTEXT_SELLER, seller);
		scriptInterface.closeAccount(scriptContext, currentUser);
	}

}
