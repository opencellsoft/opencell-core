package org.meveo.api.rest.payment.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.payment.DunningPlanTransitionDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.DunningPlanTransitionApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.payment.DunningPlanTransitionRs;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class DunningPlanTransitionRsImpl extends BaseRs implements DunningPlanTransitionRs {
	@Inject
	private DunningPlanTransitionApi dunningPlanTransitionApi;

	@Override
	public ActionStatus create(DunningPlanTransitionDto dunningPlanTransitionDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
        	dunningPlanTransitionApi.create(dunningPlanTransitionDto, getCurrentUser());
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
	public ActionStatus update(DunningPlanTransitionDto dunningPlanTransitionDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
        	dunningPlanTransitionApi.update(dunningPlanTransitionDto, getCurrentUser());
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
	public ActionStatus createOrUpdate(DunningPlanTransitionDto dunningPlanTransitionDto) {
		 ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

	        try {
	        	dunningPlanTransitionApi.createOrUpdate(dunningPlanTransitionDto, getCurrentUser());
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

