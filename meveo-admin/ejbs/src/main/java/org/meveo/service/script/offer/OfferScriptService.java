package org.meveo.service.script.offer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidPermissionException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.model.IEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.scripts.OfferModelScript;
import org.meveo.model.scripts.ScriptSourceTypeEnum;
import org.meveo.service.script.CustomScriptService;
import org.meveo.service.script.Script;

@Singleton
@Startup
public class OfferScriptService extends CustomScriptService<OfferModelScript, OfferScriptInterface> {

	public void subscribe(IEntity entity, String scriptCode, User currentUser, Provider provider)
			throws InvalidPermissionException, ElementNotFoundException, BusinessException {
		OfferScriptInterface scriptInterface = getScriptInstance(provider, scriptCode);
		if (scriptInterface != null) {
			Map<String, Object> scriptContext = new HashMap<String, Object>();
			scriptContext.put(Script.CONTEXT_ENTITY, entity);
			scriptInterface.subscribe(scriptContext, provider, currentUser);
		}
	}

	public void terminate(IEntity entity, String scriptCode, Map<String, Object> scriptContext, User currentUser,
			Provider provider) throws InvalidPermissionException, ElementNotFoundException, BusinessException {
		OfferScriptInterface scriptInterface = getScriptInstance(provider, scriptCode);
		if (scriptInterface != null) {
			scriptContext.put(Script.CONTEXT_ENTITY, entity);
			scriptInterface.terminate(scriptContext, provider, currentUser);
		}
	}

	public void create(IEntity entity, String scriptCode, User currentUser, Provider provider)
			throws InvalidPermissionException, ElementNotFoundException, BusinessException {
		OfferScriptInterface scriptInterface = getScriptInstance(provider, scriptCode);
		if (scriptInterface != null) {
			Map<String, Object> scriptContext = new HashMap<String, Object>();
			scriptContext.put(Script.CONTEXT_ENTITY, entity);
			scriptInterface.create(scriptContext, provider, currentUser);
		}
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
	public Map<String, Object> execute(String scriptCode, Map<String, Object> context, User currentUser,
			Provider currentProvider) throws ElementNotFoundException, InvalidScriptException,
			InvalidPermissionException, BusinessException {
		return super.execute(scriptCode, context, currentUser, currentProvider);
	}

}
