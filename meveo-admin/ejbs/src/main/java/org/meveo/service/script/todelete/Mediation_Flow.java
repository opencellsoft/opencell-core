package org.meveo.service.script.todelete;

import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.parse.csv.CDR;
import org.meveo.model.crm.Provider;
import org.meveo.model.mediation.Access;
import org.meveo.service.script.JavaCompilerManager;
import org.meveo.service.script.ScriptInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mediation_Flow extends org.meveo.service.script.Script {
	
	private static final Logger log = LoggerFactory.getLogger(Mediation_Flow.class);
	
	public void execute(Map<String, Object> initContext, Provider provider) throws BusinessException {
		log.info("Execute...");
		JavaCompilerManager javaCompilerManager = (JavaCompilerManager) getServiceInterface("JavaCompilerManager");

		Class<ScriptInterface> mediation_DuplicatedCheck = javaCompilerManager.getScriptInterface(provider, "Mediation_DuplicatedCheck");
		Class<ScriptInterface> mediation_RoutingSub = javaCompilerManager.getScriptInterface(provider, "Mediation_RoutingSub");
		Class<ScriptInterface> mediation_CreateEdr = javaCompilerManager.getScriptInterface(provider, "Mediation_CreateEdr");
		CDR cdr = (CDR) initContext.get("record");

		try {
			mediation_DuplicatedCheck.newInstance().execute(initContext, provider);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new BusinessException("ErrTech");
		}
		Boolean duplicateFound = (Boolean) initContext.get("duplicateFound");
		log.info("duplicateFound:" + duplicateFound);

		if (duplicateFound != null && duplicateFound.booleanValue()) {
			throw new BusinessException("DuplicateFound");
		}
		initContext.put("accessUserId", cdr.getAccess_id());
		try {
			mediation_RoutingSub.newInstance().execute(initContext, provider);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new BusinessException("ErrTech");
		}

		List<Access> accesses = (List<Access>) initContext.get("accesses");
		if (accesses == null || accesses.isEmpty()) {
			throw new BusinessException("InvalidAccess");
		}
		try {
			mediation_CreateEdr.newInstance().execute(initContext, provider);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new BusinessException("ErrTech");
		}

		log.info("Execute update entity OK");
	}

}