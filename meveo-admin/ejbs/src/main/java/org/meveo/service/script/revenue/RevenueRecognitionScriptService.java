package org.meveo.service.script.revenue;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.model.admin.User;
import org.meveo.model.scripts.RevenueRecognitionScript;
import org.meveo.model.scripts.ScriptSourceTypeEnum;
import org.meveo.service.script.CustomScriptService;

@Singleton
@Startup
public class RevenueRecognitionScriptService
		extends CustomScriptService<RevenueRecognitionScript, RevenueRecognitionScriptInterface> {

	@Inject
	private ResourceBundle resourceMessages;


	@Override
	public void create(RevenueRecognitionScript revenueRecognitionScript, User creator) throws BusinessException {
		String className = getClassName(revenueRecognitionScript.getScript());
		if (className == null) {
			throw new BusinessException(resourceMessages.getString("message.OfferModelScript.sourceInvalid"));
		}
		revenueRecognitionScript.setCode(getFullClassname(revenueRecognitionScript.getScript()));

		super.create(revenueRecognitionScript, creator);
	}

	public RevenueRecognitionScript update(RevenueRecognitionScript revenueRecognitionScript, User updater)
			throws BusinessException {
		String className = getClassName(revenueRecognitionScript.getScript());
		if (className == null) {
			throw new BusinessException(resourceMessages.getString("message.OfferModelScript.sourceInvalid"));
		}
		revenueRecognitionScript.setCode(getFullClassname(revenueRecognitionScript.getScript()));

		revenueRecognitionScript = super.update(revenueRecognitionScript, updater);

		return revenueRecognitionScript;
	}

	@PostConstruct
	void compileAll() {
		List<RevenueRecognitionScript> revenueRecognitionScripts = findByType(ScriptSourceTypeEnum.JAVA);
		compile(revenueRecognitionScripts);
	}

}
