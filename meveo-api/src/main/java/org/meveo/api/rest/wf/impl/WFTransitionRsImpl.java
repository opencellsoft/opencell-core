package org.meveo.api.rest.wf.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.payment.WFTransitionDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.wf.WFTransitionRs;
import org.meveo.api.wf.WFTransitionApi;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class WFTransitionRsImpl extends BaseRs implements WFTransitionRs {
	@Inject
	private WFTransitionApi dunningPlanTransitionApi;

	@Override
	public ActionStatus create(WFTransitionDto wfTransitionDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
        	dunningPlanTransitionApi.create(wfTransitionDto, getCurrentUser());
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
	public ActionStatus update(WFTransitionDto wfTransitionDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
        	dunningPlanTransitionApi.update(wfTransitionDto, getCurrentUser());
        }  catch (Exception e) {
            super.processException(e, result);
         }

        return result;
	}

	@Override
	public ActionStatus createOrUpdate(WFTransitionDto wfTransitionDto) {
		 ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

	        try {
	        	dunningPlanTransitionApi.createOrUpdate(wfTransitionDto, getCurrentUser());
	        }  catch (Exception e) {
	            super.processException(e, result);
	         }
	        return result;
	}
	
	

}

