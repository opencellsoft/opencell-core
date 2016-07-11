package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.payment.DunningPlanDto;
import org.meveo.api.dto.response.payment.DunningPlanResponseDto;
import org.meveo.api.dto.response.payment.DunningPlansResponseDto;
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
	        	dunningPlanApi.remove(dunningPlancode, getCurrentUser());
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
			ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
			try {
				dunningPlanApi.createOrUpdate(postData, getCurrentUser());
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
		public DunningPlanResponseDto find(String dunningPlanCode) {
			DunningPlanResponseDto dunningPlanResponseDto = new DunningPlanResponseDto();
			try {
				dunningPlanResponseDto.setDunningPlan(dunningPlanApi.find(dunningPlanCode, getCurrentUser()));
			} catch (MeveoApiException e) {
				dunningPlanResponseDto.getActionStatus().setErrorCode(e.getErrorCode());
	            dunningPlanResponseDto.getActionStatus().setStatus(ActionStatusEnum.FAIL);
	            dunningPlanResponseDto.getActionStatus().setMessage(e.getMessage());
	        } catch (Exception e) {
	            log.error("Failed to execute API", e);
	            dunningPlanResponseDto.getActionStatus().setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
	            dunningPlanResponseDto.getActionStatus().setStatus(ActionStatusEnum.FAIL);
	            dunningPlanResponseDto.getActionStatus().setMessage(e.getMessage());
	        }
			
			return dunningPlanResponseDto;
		}
		
		@Override
		public DunningPlansResponseDto list() {
			DunningPlansResponseDto dunningPlanResponseDtos = new DunningPlansResponseDto();
			try {
				dunningPlanResponseDtos.setDunningPlans(dunningPlanApi.list(getCurrentUser()));
			} catch (MeveoApiException e) {
				dunningPlanResponseDtos.getActionStatus().setErrorCode(e.getErrorCode());
				dunningPlanResponseDtos.getActionStatus().setStatus(ActionStatusEnum.FAIL);
	            dunningPlanResponseDtos.getActionStatus().setMessage(e.getMessage());
	        } catch (Exception e) {
	            log.error("Failed to execute API", e);
	            dunningPlanResponseDtos.getActionStatus().setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
	            dunningPlanResponseDtos.getActionStatus().setStatus(ActionStatusEnum.FAIL);
	            dunningPlanResponseDtos.getActionStatus().setMessage(e.getMessage());
	        }
			
			return dunningPlanResponseDtos;
		}
		
}