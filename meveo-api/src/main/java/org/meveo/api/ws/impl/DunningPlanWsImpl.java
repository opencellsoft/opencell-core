package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.elasticsearch.index.engine.Engine.Get;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.payment.DunningPlanDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.DunningPlanApi;
import org.meveo.api.ws.DunningPlanWs;

@WebService(serviceName = "DunningPlanWs", endpointInterface = "org.meveo.api.ws.DunningPlanWs")
@Interceptors({ WsRestApiInterceptor.class })
public class DunningPlanWsImpl extends BaseWs implements DunningPlanWs {

	 @Inject
	 private DunningPlanApi dunningPlanApi;

	    @Override
	    public ActionStatus create(DunningPlanDto dunningPlanDto) {
	        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

	        try {
	        	dunningPlanApi.create(dunningPlanDto, getCurrentUser());
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
	    public ActionStatus update(DunningPlanDto dunningPlanDto) {
	        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

	        try {
	        	dunningPlanApi.update(dunningPlanDto, getCurrentUser());
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
	    public ActionStatus remove(String dunningPlancode) {
	        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	        try {
	        	dunningPlanApi.remove(dunningPlancode, getCurrentUser().getProvider());
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
		public ActionStatus createOrUpdate(DunningPlanDto postData) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Get find(String dunningPlanCode) {
			// TODO Auto-generated method stub
			return null;
		}
	    
	 
}