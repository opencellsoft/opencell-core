package org.meveo.service.script;

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
import org.meveo.model.crm.Provider;
import org.meveo.model.scripts.CustomScript;
import org.meveo.model.scripts.ScriptSourceTypeEnum;
import org.meveo.model.scripts.ServiceModelScript;
import org.meveo.service.script.service.ServiceScriptInterface;

/**
 * @author Edward P. Legaspi
 **/
@Singleton
@Startup
public class ServiceModelScriptService extends CustomScriptService<ServiceModelScript, ServiceScriptInterface> {

	@Inject
	private ResourceBundle resourceMessages;

	@Override
	public void create(ServiceModelScript serviceModelScript, User creator, Provider provider) {
		String packageName = getPackageName(serviceModelScript.getScript());
		String className = getClassName(serviceModelScript.getScript());
		if (packageName == null || className == null) {
			throw new RuntimeException(resourceMessages.getString("message.ServiceModelScript.sourceInvalid"));
		}
		serviceModelScript.setCode(packageName + "." + className);

		super.create(serviceModelScript, creator, provider);
	}

	@Override
	public ServiceModelScript update(ServiceModelScript serviceModelScript, User updater) {

		String packageName = getPackageName(serviceModelScript.getScript());
		String className = getClassName(serviceModelScript.getScript());
		if (packageName == null || className == null) {
			throw new RuntimeException(resourceMessages.getString("message.ServiceModelScript.sourceInvalid"));
		}
		serviceModelScript.setCode(packageName + "." + className);

		serviceModelScript = super.update(serviceModelScript, updater);

		return serviceModelScript;
	}

	/**
	 * Get all ServiceModelScripts with error for a provider
	 * 
	 * @param provider
	 * @return
	 */
	public List<CustomScript> getServiceModelScriptsWithError(Provider provider) {
		return ((List<CustomScript>) getEntityManager()
				.createNamedQuery("CustomScript.getServiceModelScriptOnError", CustomScript.class)
				.setParameter("isError", Boolean.TRUE).setParameter("provider", provider).getResultList());
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
	public Map<String, Object> execute(String scriptCode, Map<String, Object> context, User currentUser,
			Provider currentProvider) throws ElementNotFoundException, InvalidScriptException,
			InvalidPermissionException, BusinessException {
		return super.execute(scriptCode, context, currentUser, currentProvider);
	}
	
	public String getDerivedCode(String script) {
		return getPackageName(script) + "." + getClassName(script);
	}

}
