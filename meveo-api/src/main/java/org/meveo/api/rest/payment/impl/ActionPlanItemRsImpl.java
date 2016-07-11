package org.meveo.api.rest.payment.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.payment.ActionPlanItemDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.ActionPlanItemApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.payment.ActionPlanItemRs;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class ActionPlanItemRsImpl extends BaseRs implements ActionPlanItemRs {
	@Inject
	private ActionPlanItemApi actionPlanItemApi;

	@Override
	public ActionStatus create(ActionPlanItemDto actionPlanItemDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
        	actionPlanItemApi.create(actionPlanItemDto, getCurrentUser());
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
	public ActionStatus update(ActionPlanItemDto actionPlanItemDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
        	actionPlanItemApi.update(actionPlanItemDto, getCurrentUser());
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
	public ActionStatus createOrUpdate(ActionPlanItemDto actionPlanItemDto) {
		 ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

	        try {
	        	actionPlanItemApi.createOrUpdate(actionPlanItemDto, getCurrentUser());
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

