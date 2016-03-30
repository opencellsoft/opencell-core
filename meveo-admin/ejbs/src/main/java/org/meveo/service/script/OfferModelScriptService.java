package org.meveo.service.script;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidPermissionException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.model.admin.User;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.scripts.CustomScript;
import org.meveo.model.scripts.OfferModelScript;
import org.meveo.model.scripts.ScriptSourceTypeEnum;
import org.meveo.service.script.offer.OfferScriptInterface;

/**
 * @author Edward P. Legaspi
 **/
@Singleton
@Startup
public class OfferModelScriptService extends CustomScriptService<OfferModelScript, OfferScriptInterface> {

	@Inject
	private ResourceBundle resourceMessages;

	@Override
	public void create(OfferModelScript offerModelScript, User creator) throws BusinessException {
		String packageName = getPackageName(offerModelScript.getScript());
		String className = getClassName(offerModelScript.getScript());
		if (packageName == null || className == null) {
			throw new RuntimeException(resourceMessages.getString("message.OfferModelScript.sourceInvalid"));
		}
		offerModelScript.setCode(packageName + "." + className);

		super.create(offerModelScript, creator);
	}

	@Override
	public OfferModelScript update(OfferModelScript offerModelScript, User updater) throws BusinessException {

		String packageName = getPackageName(offerModelScript.getScript());
		String className = getClassName(offerModelScript.getScript());
		if (packageName == null || className == null) {
			throw new RuntimeException(resourceMessages.getString("message.OfferModelScript.sourceInvalid"));
		}
		offerModelScript.setCode(packageName + "." + className);

		offerModelScript = super.update(offerModelScript, updater);

		return offerModelScript;
	}

	/**
	 * Get all OfferModelScripts with error for a provider
	 * 
	 * @param provider
	 * @return
	 */
	public List<CustomScript> getOfferModelScriptsWithError(Provider provider) {
		return ((List<CustomScript>) getEntityManager().createNamedQuery("CustomScript.getOfferModelScriptOnError", CustomScript.class).setParameter("isError", Boolean.TRUE)
				.setParameter("provider", provider).getResultList());
	}

	/**
	 * Compile all OfferModelScripts
	 */
	@PostConstruct
	void compileAll() {
		List<OfferModelScript> offerModelScripts = findByType(ScriptSourceTypeEnum.JAVA);
		compile(offerModelScripts);
	}

	/**
	 * Execute the script identified by a script code. No init nor finalize
	 * methods are called.
	 * 
	 * @param scriptCode
	 *            OfferModelScriptCode
	 * @param context
	 *            Context parameters (optional)
	 * @param currentUser
	 *            User executor
	 * @param currentProvider
	 *            Provider
	 * @return Context parameters. Will not be null even if "context" parameter
	 *         is null.
	 * @throws InvalidPermissionException
	 *             Insufficient access to run the script
	 * @throws ElementNotFoundException
	 *             Script not found
	 * @throws BusinessException
	 *             Any execution exception
	 */
	@Override
	public Map<String, Object> execute(String scriptCode, Map<String, Object> context, User currentUser) throws ElementNotFoundException, InvalidScriptException,
			InvalidPermissionException, BusinessException {
		return super.execute(scriptCode, context, currentUser);
	}

	public String getDerivedCode(String script) {
		return getPackageName(script) + "." + getClassName(script);
	}

	// Interface methods

	public void subscribeInterface(Subscription entity, String scriptCode, User currentUser) throws InvalidPermissionException, ElementNotFoundException, BusinessException {
		OfferScriptInterface scriptInterface = getScriptInstance(currentUser.getProvider(), scriptCode);
		if (scriptInterface != null) {
			Map<String, Object> scriptContext = new HashMap<String, Object>();
			scriptContext.put(Script.CONTEXT_ENTITY, entity);
			scriptInterface.subscribe(scriptContext, currentUser.getProvider(), currentUser);
		}
	}

	public void terminateInterface(Subscription entity, String scriptCode, Map<String, Object> scriptContext, User currentUser) throws InvalidPermissionException,
			ElementNotFoundException, BusinessException {
		OfferScriptInterface scriptInterface = getScriptInstance(currentUser.getProvider(), scriptCode);
		if (scriptInterface != null) {
			scriptContext.put(Script.CONTEXT_ENTITY, entity);
			scriptInterface.terminateSubscription(scriptContext, currentUser.getProvider(), currentUser);
		}
	}

	public void suspendInterface(Subscription entity, String scriptCode, Map<String, Object> scriptContext, User currentUser) throws InvalidPermissionException,
			ElementNotFoundException, BusinessException {
		OfferScriptInterface scriptInterface = getScriptInstance(currentUser.getProvider(), scriptCode);
		if (scriptInterface != null) {
			scriptContext.put(Script.CONTEXT_ENTITY, entity);
			scriptInterface.suspendSubscription(scriptContext, currentUser.getProvider(), currentUser);
		}
	}

	public void reactivateInterface(Subscription entity, String scriptCode, Map<String, Object> scriptContext, User currentUser) throws ElementNotFoundException,
			InvalidScriptException {
		OfferScriptInterface scriptInterface = getScriptInstance(currentUser.getProvider(), scriptCode);
		if (scriptInterface != null) {
			scriptContext.put(Script.CONTEXT_ENTITY, entity);
			scriptInterface.reactivateSubscription(scriptContext, currentUser.getProvider(), currentUser);
		}
	}

	public void createOfferTemplateInterface(OfferTemplate entity, String scriptCode, User currentUser) throws InvalidPermissionException, ElementNotFoundException, BusinessException {
		OfferScriptInterface scriptInterface = getScriptInstance(currentUser.getProvider(), scriptCode);
		if (scriptInterface != null) {
			Map<String, Object> scriptContext = new HashMap<String, Object>();
			scriptContext.put(Script.CONTEXT_ENTITY, entity);
			scriptInterface.createOfferTemplate(scriptContext, currentUser.getProvider(), currentUser);
		}
	}

	public void updateOfferTemplateInterface(OfferTemplate entity, String scriptCode, User currentUser) throws ElementNotFoundException, InvalidScriptException {
		OfferScriptInterface scriptInterface = getScriptInstance(currentUser.getProvider(), scriptCode);
		if (scriptInterface != null) {
			Map<String, Object> scriptContext = new HashMap<String, Object>();
			scriptContext.put(Script.CONTEXT_ENTITY, entity);
			scriptInterface.updateOfferTemplate(scriptContext, currentUser.getProvider(), currentUser);
		}
	}

}
