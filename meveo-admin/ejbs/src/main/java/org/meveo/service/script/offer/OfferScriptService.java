package org.meveo.service.script.offer;

import java.util.Map;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidPermissionException;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.scripts.OfferModelScript;
import org.meveo.service.script.CustomScriptService;

@Stateless
public class OfferScriptService extends CustomScriptService<OfferModelScript, OfferScriptInterface> {

	public void subscribe(String scriptCode, Map<String, Object> scriptContext, User currentUser, Provider provider)
			throws InvalidPermissionException, ElementNotFoundException, BusinessException {
		execute(scriptCode, scriptContext, currentUser, provider);
	}

	public void terminate(String scriptCode, Map<String, Object> scriptContext, User currentUser, Provider provider)
			throws InvalidPermissionException, ElementNotFoundException, BusinessException {
		execute(scriptCode, scriptContext, currentUser, provider);
	}

	public void onCreated(String scriptCode, Map<String, Object> scriptContext, User currentUser, Provider provider)
			throws InvalidPermissionException, ElementNotFoundException, BusinessException {
		execute(scriptCode, scriptContext, currentUser, provider);
	}

}
