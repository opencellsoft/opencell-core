package org.meveo.service.script.instances;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.account.CRMAccountHierarchyDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.service.crm.impl.AccountHierarchyApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FF_Hierarchy extends org.meveo.service.script.Script {

	private static final Logger log = LoggerFactory.getLogger(FF_Hierarchy.class);
	private Map<String, Object> context = null;

	AccountHierarchyApiService acccountHierarchyApiService = (AccountHierarchyApiService) getServiceInterface("AccountHierarchyApiService");

	public void init(Map<String, Object> scriptContext, Provider provider, User user) {
		log.debug("init  scriptContext {}, Provider {}, User {} ", scriptContext,  provider,  user);
		log.debug("File imported :{}",scriptContext.get("fileName"));
		context = scriptContext;
	}

	public void execute(Map<String, Object> methodContext, Provider provider, User user) throws BusinessException {
		log.debug("execute  methodContext {}, Provider {}, User {} ", methodContext,  provider,  user);
		CRMAccountHierarchyDto cRMAccountHierarchyDto = (CRMAccountHierarchyDto) methodContext.get("theDto");
		try {
			acccountHierarchyApiService.createCRMAccountHierarchy(cRMAccountHierarchyDto, user);
			log.debug("Hierarchy {} creation done",cRMAccountHierarchyDto.getCode());
		} catch (EntityAlreadyExistsException e) {
			log.warn("Hierarchy {} already exist, update in progress...",cRMAccountHierarchyDto.getCode());
			try {
				acccountHierarchyApiService.updateCRMAccountHierarchy(cRMAccountHierarchyDto, user);
				log.debug("Hierarchy {} update done",cRMAccountHierarchyDto.getCode());
			} catch (MeveoApiException e1) {
				log.error("Hierarchy "+cRMAccountHierarchyDto.getCode()+" update failed",e);
				throw new BusinessException(e.getMessage());
			}
		}catch (MeveoApiException e) {
			log.error("Hierarchy "+cRMAccountHierarchyDto.getCode()+" creation failed",e);
			throw new BusinessException(e.getMessage());
		}
	}

	public void finalize(Map<String, Object> methodContext, Provider provider, User user) {
		log.debug("finalize  methodContext {}, Provider {}, User {} ", methodContext,  provider,  user);
	}

}