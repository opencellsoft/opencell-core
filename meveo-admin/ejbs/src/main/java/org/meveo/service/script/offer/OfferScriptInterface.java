package org.meveo.service.script.offer;

import java.util.Map;

import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.service.script.ScriptInterface;

public interface OfferScriptInterface extends ScriptInterface {

	public void createOfferTemplate(Map<String, Object> methodContext, Provider provider, User currentUser);

	public void updateOfferTemplate(Map<String, Object> methodContext, Provider provider, User currentUser);

	public void subscribe(Map<String, Object> methodContext, Provider provider, User currentUser);

	public void suspendSubscription(Map<String, Object> methodContext, Provider provider, User currentUser);

	public void reactivateSubscription(Map<String, Object> methodContext, Provider provider, User currentUser);

	public void terminateSubscription(Map<String, Object> methodContext, Provider provider, User currentUser);

}
