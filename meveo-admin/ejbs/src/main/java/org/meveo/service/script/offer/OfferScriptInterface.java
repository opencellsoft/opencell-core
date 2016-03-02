package org.meveo.service.script.offer;

import java.util.Map;

import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.service.script.ScriptInterface;

public interface OfferScriptInterface extends ScriptInterface {

	public void create(Map<String, Object> methodContext, Provider provider, User currentUser);

	public void update(Map<String, Object> methodContext, Provider provider, User currentUser);

	public void subscribe(Map<String, Object> methodContext, Provider provider, User currentUser);

	public void suspend(Map<String, Object> methodContext, Provider provider, User currentUser);

	public void reactivate(Map<String, Object> methodContext, Provider provider, User currentUser);

	public void terminate(Map<String, Object> methodContext, Provider provider, User currentUser);

}
