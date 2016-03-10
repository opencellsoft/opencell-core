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
	public void create(OfferModelScript offerModelScript, User creator, Provider provider) {
		String packageName = getPackageName(offerModelScript.getScript());
		String className = getClassName(offerModelScript.getScript());
		if (packageName == null || className == null) {
			throw new RuntimeException(resourceMessages.getString("message.OfferModelScript.sourceInvalid"));
		}
		offerModelScript.setCode(packageName + "." + className);

		super.create(offerModelScript, creator, provider);
	}

	@Override
	public OfferModelScript update(OfferModelScript offerModelScript, User updater) {

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
		return ((List<CustomScript>) getEntityManager()
				.createNamedQuery("CustomScript.getOfferModelScriptOnError", CustomScript.class)
				.setParameter("isError", Boolean.TRUE).setParameter("provider", provider).getResultList());
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
