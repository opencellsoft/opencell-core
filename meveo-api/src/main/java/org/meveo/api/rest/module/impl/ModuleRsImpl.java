package org.meveo.api.rest.module.impl;

import javax.inject.Inject;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.module.ModuleDto;
import org.meveo.api.dto.response.module.MeveoModuleDtoResponse;
import org.meveo.api.dto.response.module.MeveoModuleDtosResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.module.ModuleApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.module.ModuleRs;

/**
 * @author Tyshan Shi(tyshan@manaty.net)
 *
**/
public class ModuleRsImpl extends BaseRs implements ModuleRs {
	
	@Inject
	private ModuleApi moduleApi;

	@Override
	public ActionStatus create(ModuleDto moduleData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try {
			moduleApi.create(moduleData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("Error when create meveoModule ",e);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage()); 
			log.error("Unknown exception when create meveoModule ",e);
		}
		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public ActionStatus update(ModuleDto moduleDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try {
			moduleApi.update(moduleDto, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("Error when update meveoModule ",e);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage()); 
			log.error("Unknown error when update meveoModule ",e);
		}
		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public ActionStatus delete(String code) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try {
			moduleApi.delete(code, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("Error when delete meveoModule ",e);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage()); 
			log.error("Unknown exception when delete meveoModule ",e);
		}
		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public MeveoModuleDtosResponse list() {
		MeveoModuleDtosResponse result = new MeveoModuleDtosResponse();
		result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
		try {
			result.setModuleDtoList(moduleApi.list(getCurrentUser()));
		} catch (Exception e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
			log.error("Error when list meveoModule ",e);
		}
		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public MeveoModuleDtoResponse get(String code) {
		MeveoModuleDtoResponse result = new MeveoModuleDtoResponse();
		result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
		try {
			result.setModuleDto(moduleApi.get(code, getCurrentUser()));
		} catch (Exception e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
			log.error("Error when get a meveoModule by code {}",code,e);
		}
		log.debug("RESPONSE={}", result);
		return result;
	}
}
