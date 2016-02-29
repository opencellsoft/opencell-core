package org.meveo.service.script.service;

import java.util.Map;

import javax.ejb.Stateless;

import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.scripts.ServiceModelScript;
import org.meveo.service.script.CustomScriptService;

@Stateless
public class ServiceScriptService extends CustomScriptService<ServiceModelScript, ServiceScriptInterface> {

	public void activate(String code, Map<String, Object> scriptContext, User creator, Provider provider) {
		// TODO Auto-generated method stub

	}

	public void terminate(String code, Map<String, Object> scriptContext, User user, Provider provider) {
		// TODO Auto-generated method stub

	}

	public void suspended(String code, Map<String, Object> scriptContext, User user, Provider provider) {
		// TODO Auto-generated method stub

	}

	public void instantiated(String code, Map<String, Object> scriptContext, User user, Provider provider) {
		// TODO Auto-generated method stub

	}

}
