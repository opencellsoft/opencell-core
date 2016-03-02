package org.meveo.service.script.service;

import java.util.Map;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidPermissionException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.scripts.ServiceModelScript;
import org.meveo.service.script.CustomScriptService;

@Stateless
public class ServiceScriptService extends CustomScriptService<ServiceModelScript, ServiceScriptInterface> {

	public void activate(String scriptCode, Map<String, Object> scriptContext, User currentUser, Provider provider)
			throws ElementNotFoundException, InvalidScriptException, InvalidPermissionException, BusinessException {
		execute(scriptCode, scriptContext, currentUser, provider);
	}

	public void terminate(String scriptCode, Map<String, Object> scriptContext, User currentUser, Provider provider) throws ElementNotFoundException, InvalidScriptException, InvalidPermissionException, BusinessException {
		execute(scriptCode, scriptContext, currentUser, provider);
	}

	public void suspended(String scriptCode, Map<String, Object> scriptContext, User currentUser, Provider provider) throws ElementNotFoundException, InvalidScriptException, InvalidPermissionException, BusinessException {
		execute(scriptCode, scriptContext, currentUser, provider);
	}

	public void instantiated(String scriptCode, Map<String, Object> scriptContext, User currentUser, Provider provider)
			throws ElementNotFoundException, InvalidScriptException, InvalidPermissionException, BusinessException {
		execute(scriptCode, scriptContext, currentUser, provider);
	}

}
