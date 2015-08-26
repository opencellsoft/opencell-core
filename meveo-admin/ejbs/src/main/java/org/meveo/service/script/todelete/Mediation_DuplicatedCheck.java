package org.meveo.service.script.todelete;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.Provider;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.script.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mediation_DuplicatedCheck extends Script {

	private static final Logger log = LoggerFactory.getLogger(Mediation_DuplicatedCheck.class);

	public void execute(Map<String, Object> initContext,Provider provider)throws BusinessException{
		log.info("Execute...");
		EdrService edrService = (EdrService) getServiceInterface("EdrService");
		boolean result = edrService.duplicateFound(provider, (String) initContext.get("originBatch"), (String) initContext.get("originRecord"));
		initContext.put("duplicateFound", Boolean.valueOf(result));
		log.info("Execute result:"+result);
	}
}

