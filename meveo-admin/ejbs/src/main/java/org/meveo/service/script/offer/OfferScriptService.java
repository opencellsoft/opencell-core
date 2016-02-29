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

	public void subscribe(String code, Map<String, Object> scriptContext, User creator, Provider provider)
			throws InvalidPermissionException, ElementNotFoundException, BusinessException {
		// TODO Auto-generated method stub

	}

	public void terminate(String code, Map<String, Object> scriptContext, User user, Provider provider)
			throws InvalidPermissionException, ElementNotFoundException, BusinessException {
		// TODO Auto-generated method stub

	}

	public void onCreated(String code, Map<String, Object> scriptContext, User user, Provider provider)
			throws InvalidPermissionException, ElementNotFoundException, BusinessException {
		// TODO Auto-generated method stub

	}

}
