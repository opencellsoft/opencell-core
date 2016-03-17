package org.meveo.service.script.service;

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
import org.meveo.model.scripts.ScriptSourceTypeEnum;
import org.meveo.model.scripts.ServiceModelScript;
import org.meveo.service.script.CustomScriptService;
import org.meveo.service.script.Script;

@Singleton
@Startup
public class ServiceScriptService extends CustomScriptService<ServiceModelScript, ServiceScriptInterface> {

	public void activate(IEntity entity, String scriptCode, Map<String, Object> scriptContext, User currentUser,
			Provider provider) throws ElementNotFoundException, InvalidScriptException, InvalidPermissionException,
			BusinessException {
		ServiceScriptInterface scriptInterface = getScriptInstance(provider, scriptCode);
		if (scriptInterface != null) {
			scriptContext.put(Script.CONTEXT_ENTITY, entity);
			scriptInterface.activate(scriptContext, provider, currentUser);
		}
	}

	public void terminate(IEntity entity, String scriptCode, Map<String, Object> scriptContext, User currentUser,
			Provider provider) throws ElementNotFoundException, InvalidScriptException, InvalidPermissionException,
			BusinessException {
		ServiceScriptInterface scriptInterface = getScriptInstance(provider, scriptCode);
		if (scriptInterface != null) {
			scriptContext.put(Script.CONTEXT_ENTITY, entity);
			scriptInterface.activate(scriptContext, provider, currentUser);
		}
	}

	public void suspended(IEntity entity, String scriptCode, Map<String, Object> scriptContext, User currentUser,
			Provider provider) throws ElementNotFoundException, InvalidScriptException, InvalidPermissionException,
			BusinessException {
		ServiceScriptInterface scriptInterface = getScriptInstance(provider, scriptCode);
		if (scriptInterface != null) {
			scriptContext.put(Script.CONTEXT_ENTITY, entity);
			scriptInterface.activate(scriptContext, provider, currentUser);
		}
	}

	public void instantiated(IEntity entity, String scriptCode, Map<String, Object> scriptContext, User currentUser,
			Provider provider) throws ElementNotFoundException, InvalidScriptException, InvalidPermissionException,
			BusinessException {
		ServiceScriptInterface scriptInterface = getScriptInstance(provider, scriptCode);
		if (scriptInterface != null) {
			scriptContext.put(Script.CONTEXT_ENTITY, entity);
			scriptInterface.activate(scriptContext, provider, currentUser);
		}
	}

	/**
	 * Compile all ServiceModelScripts
	 */
	@PostConstruct
	void compileAll() {
		List<ServiceModelScript> ServiceModelScripts = findByType(ScriptSourceTypeEnum.JAVA);
		compile(ServiceModelScripts);
	}

	/**
	 * Execute the script identified by a script code. No init nor finalize
	 * methods are called.
	 * 
	 * @param scriptCode
	 *            ServiceModelScriptCode
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
	public Map<String, Object> execute(String scriptCode, Map<String, Object> context, User currentUser
			) throws ElementNotFoundException, InvalidScriptException,
			InvalidPermissionException, BusinessException {
		return super.execute(scriptCode, context, currentUser);
	}

}
