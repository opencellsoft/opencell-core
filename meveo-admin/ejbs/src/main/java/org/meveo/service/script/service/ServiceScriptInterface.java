package org.meveo.service.script.service;

import java.util.Map;

import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.service.script.ScriptInterface;

public interface ServiceScriptInterface extends ScriptInterface {

	public void createServiceInstance(Map<String, Object> methodContext, Provider provider, User currentUser);

	public void updateServiceInstance(Map<String, Object> methodContext, Provider provider, User currentUser);

	public void instantiateServiceInstance(Map<String, Object> methodContext, Provider provider, User currentUser);

	public void activateServiceInstance(Map<String, Object> methodContext, Provider provider, User currentUser);

	public void suspendServiceInstance(Map<String, Object> methodContext, Provider provider, User currentUser);

	public void reactivateServiceInstance(Map<String, Object> methodContext, Provider provider, User currentUser);

	public void terminateServiceInstance(Map<String, Object> methodContext, Provider provider, User currentUser);

}
