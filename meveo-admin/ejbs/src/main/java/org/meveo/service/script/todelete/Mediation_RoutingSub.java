package org.meveo.service.script.todelete;

import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.cache.CdrEdrProcessingCacheContainerProvider;
import org.meveo.model.crm.Provider;
import org.meveo.model.mediation.Access;
import org.meveo.service.script.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mediation_RoutingSub extends Script {

	private static final Logger log = LoggerFactory.getLogger(Mediation_RoutingSub.class);

	public void execute(Map<String, Object> initContext,Provider provider)throws BusinessException {
		log.debug("Execute...");
		String accessUserId = (String) initContext.get("accessUserId");
		CdrEdrProcessingCacheContainerProvider cdrEdrProcessingCacheContainerProvider = (CdrEdrProcessingCacheContainerProvider) getServiceInterface("CdrEdrProcessingCacheContainerProvider");
		List<Access> accesses = cdrEdrProcessingCacheContainerProvider.getAccessesByAccessUserId(provider.getId(), accessUserId);
		initContext.put("accesses", accesses);
		log.info("Execute accesses size:"+((accesses == null)?"null":accesses.size()));
	}
}