package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.ScriptInstanceApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.ScriptInstanceDto;
import org.meveo.api.dto.response.GetScriptInstanceResponseDto;
import org.meveo.api.dto.response.ScriptInstanceReponseDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidEnumValue;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.ws.ScriptInstanceWs;

@WebService(serviceName = "ScriptInstanceWs", endpointInterface = "org.meveo.api.ws.ScriptInstanceWs")
@Interceptors({ LoggingInterceptor.class })
public class ScriptInstanceWsImpl extends BaseWs implements ScriptInstanceWs {


	@Inject
	private ScriptInstanceApi scriptInstanceApi;

	@Override
	public ScriptInstanceReponseDto create(ScriptInstanceDto postData) {
		ScriptInstanceReponseDto result = new ScriptInstanceReponseDto();
			try {
				result.setCompilationErrors(scriptInstanceApi.create(postData, getCurrentUser()));
				result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
			} catch (MissingParameterException e) {
				result.getActionStatus().setErrorCode(e.getErrorCode());
				result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
				result.getActionStatus().setMessage(e.getMessage());
			} catch (EntityAlreadyExistsException e) {
				result.getActionStatus().setErrorCode(e.getErrorCode());
				result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
				result.getActionStatus().setMessage(e.getMessage());
			} catch (InvalidEnumValue e) {
				result.getActionStatus().setErrorCode(e.getErrorCode());
				result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
				result.getActionStatus().setMessage(e.getMessage());
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
	public ScriptInstanceReponseDto update(ScriptInstanceDto postData) {
		ScriptInstanceReponseDto result = new ScriptInstanceReponseDto();		
			try {
				result.setCompilationErrors(scriptInstanceApi.update(postData, getCurrentUser()));
				result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
			} catch (MissingParameterException e) {
				result.getActionStatus().setErrorCode(e.getErrorCode());
				result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
				result.getActionStatus().setMessage(e.getMessage());
			} catch (EntityDoesNotExistsException e) {
				result.getActionStatus().setErrorCode(e.getErrorCode());
				result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
				result.getActionStatus().setMessage(e.getMessage());
			} catch (InvalidEnumValue e) {
				result.getActionStatus().setErrorCode(e.getErrorCode());
				result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
				result.getActionStatus().setMessage(e.getMessage());
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
	public ActionStatus remove(String scriptInstanceCode) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
			try {
				scriptInstanceApi.remove(scriptInstanceCode, getCurrentUser().getProvider());
			} catch (EntityDoesNotExistsException e) {
				result.setErrorCode(e.getErrorCode());
				result.setStatus(ActionStatusEnum.FAIL);
				result.setMessage(e.getMessage());
			} catch (MissingParameterException e) {
				result.setErrorCode(e.getErrorCode());
				result.setStatus(ActionStatusEnum.FAIL);
				result.setMessage(e.getMessage());
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
	public GetScriptInstanceResponseDto find(String scriptInstanceCode) {
		GetScriptInstanceResponseDto result = new GetScriptInstanceResponseDto();
		try {
			result.setScriptInstance(scriptInstanceApi.find(scriptInstanceCode, getCurrentUser().getProvider()));
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
	public ScriptInstanceReponseDto createOrUpdate(ScriptInstanceDto postData) {
		ScriptInstanceReponseDto result = new ScriptInstanceReponseDto();
		try {
			result.setCompilationErrors(scriptInstanceApi.createOrUpdate(postData, getCurrentUser()));
			result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
		} catch (MissingParameterException e) {
			result.getActionStatus().setErrorCode(e.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		} catch (EntityAlreadyExistsException e) {
			result.getActionStatus().setErrorCode(e.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		} catch (InvalidEnumValue e) {
			result.getActionStatus().setErrorCode(e.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
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
