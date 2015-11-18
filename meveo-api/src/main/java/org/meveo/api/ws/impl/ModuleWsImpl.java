package org.meveo.api.ws.impl;

import java.util.List;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.module.ModuleDto;
import org.meveo.api.dto.response.module.MeveoModuleDtoResponse;
import org.meveo.api.dto.response.module.MeveoModuleDtosResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.module.ModuleApi;
import org.meveo.api.ws.ModuleWs;

/**
 * @author Tyshan Shi(tyshan@manaty.net)
 *
**/
@WebService(serviceName="ModuleWs",endpointInterface="org.meveo.api.ws.ModuleWs")
@Interceptors({LoggingInterceptor.class})
public class ModuleWsImpl extends BaseWs implements ModuleWs {
	
	@Inject
	private ModuleApi moduleApi;

	@Override
	public ActionStatus create(ModuleDto moduleDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			moduleApi.create(moduleDto, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
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
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
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
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public MeveoModuleDtosResponse list() {
		MeveoModuleDtosResponse result=new MeveoModuleDtosResponse();
		result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
		result.getActionStatus().setMessage("");
		try {
			List<ModuleDto> dtos=moduleApi.list( getCurrentUser());
			result.setModuleDtoList(dtos);
		} catch (MeveoApiException e) {
			result.getActionStatus().setErrorCode(e.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		} catch (Exception e) {
			result.getActionStatus().setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public MeveoModuleDtoResponse get(String code) {
		MeveoModuleDtoResponse result=new MeveoModuleDtoResponse();
		result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
		result.getActionStatus().setMessage("");
		try {
			ModuleDto dto=moduleApi.get(code, getCurrentUser());
			result.setModuleDto(dto);
		} catch (MeveoApiException e) {
			result.getActionStatus().setErrorCode(e.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		} catch (Exception e) {
			result.getActionStatus().setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

}
