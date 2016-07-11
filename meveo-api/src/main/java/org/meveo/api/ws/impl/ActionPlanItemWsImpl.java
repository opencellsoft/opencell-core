package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.payment.ActionPlanItemDto;
import org.meveo.api.dto.response.payment.ActionPlanItemResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.ActionPlanItemApi;
import org.meveo.api.ws.ActionPlanItemWs;
import org.meveo.model.payments.DunningLevelEnum;

@WebService(serviceName = "ActionPlanItemWs", endpointInterface = "org.meveo.api.ws.ActionPlanItemWs")
@Interceptors({ WsRestApiInterceptor.class })
public class ActionPlanItemWsImpl extends BaseWs implements ActionPlanItemWs {

	@Inject
	private ActionPlanItemApi actionPlanItemApi;
	
	@Override
	public ActionStatus create(ActionPlanItemDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
        	actionPlanItemApi.create(postData, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        return result;
	}
	
	@Override
	public ActionStatus update(ActionPlanItemDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
        	actionPlanItemApi.update(postData, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        return result;
	}
	
	@Override
	public ActionStatus createOrUpdate(ActionPlanItemDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try {
			actionPlanItemApi.createOrUpdate(postData, getCurrentUser());
		} catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }
		
		return result;
	}
	
	@Override
	public ActionPlanItemResponseDto find(String dunningPlanCode, Integer itemlOrder, DunningLevelEnum dunningLevel) {
		ActionPlanItemResponseDto actionPlanItemResponseDto = new ActionPlanItemResponseDto();
		try {
			actionPlanItemResponseDto.setActionPlanItem(actionPlanItemApi.find(dunningPlanCode, dunningLevel, itemlOrder, getCurrentUser()));
		} catch (MeveoApiException e) {
			actionPlanItemResponseDto.getActionStatus().setErrorCode(e.getErrorCode());
			actionPlanItemResponseDto.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			actionPlanItemResponseDto.getActionStatus().setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            actionPlanItemResponseDto.getActionStatus().setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            actionPlanItemResponseDto.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            actionPlanItemResponseDto.getActionStatus().setMessage(e.getMessage());
        }
		
		return actionPlanItemResponseDto;
	}
	
	@Override
	public ActionStatus remove(String dunningPlanCode, Integer itemOrder, DunningLevelEnum dunningLevel) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
        	actionPlanItemApi.remove(dunningPlanCode, itemOrder, dunningLevel, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        return result;
	}
	    
}