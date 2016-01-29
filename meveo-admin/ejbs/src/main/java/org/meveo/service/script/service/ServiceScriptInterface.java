package org.meveo.service.script.service;

import java.util.Map;

import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.service.script.ScriptInterface;

public interface ServiceScriptInterface extends ScriptInterface{
	
	public void create(Map<String, Object> methodContext, Provider provider, User user);
	public void update(Map<String, Object> methodContext, Provider provider, User user);
	public void instantiate(Map<String, Object> methodContext, Provider provider, User user);
	public void activate(Map<String, Object> methodContext, Provider provider, User user);
	public void suspend(Map<String, Object> methodContext, Provider provider, User user);
	public void reactivate(Map<String, Object> methodContext, Provider provider, User user);
	public void terminate(Map<String, Object> methodContext, Provider provider, User user);

}
