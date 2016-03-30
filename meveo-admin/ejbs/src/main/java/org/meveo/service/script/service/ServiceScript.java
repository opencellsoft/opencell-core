package org.meveo.service.script.service;

import java.util.Map;

import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.service.script.Script;

/**
 * @author Edward P. Legaspi
 **/
public class ServiceScript extends Script implements ServiceScriptInterface {
	
	public static String CONTEXT_REACTIVATION_DATE = "CONTEXT_REACTIVATION_DATE";
	public static String CONTEXT_SUSPENSION_DATE = "CONTEXT_SUSPENSION_DATE";
	public static String CONTEXT_TERMINATION_DATE = "CONTEXT_TERMINATION_DATE";
	public static String CONTEXT_TERMINATION_REASON = "CONTEXT_TERMINATION_REASON";

	@Override
	public void createServiceInstance(Map<String, Object> methodContext, Provider provider, User currentUser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateServiceInstance(Map<String, Object> methodContext, Provider provider, User currentUser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void instantiateServiceInstance(Map<String, Object> methodContext, Provider provider, User currentUser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void activateServiceInstance(Map<String, Object> methodContext, Provider provider, User currentUser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void suspendServiceInstance(Map<String, Object> methodContext, Provider provider, User currentUser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reactivateServiceInstance(Map<String, Object> methodContext, Provider provider, User currentUser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void terminateServiceInstance(Map<String, Object> methodContext, Provider provider, User currentUser) {
		// TODO Auto-generated method stub

	}

}
