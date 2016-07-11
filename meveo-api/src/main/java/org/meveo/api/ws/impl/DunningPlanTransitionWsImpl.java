package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.payment.DunningPlanTransitionDto;
import org.meveo.api.dto.response.payment.DunningPlanTransitionResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.DunningPlanTransitionApi;
import org.meveo.api.ws.DunningPlanTransitionWs;
import org.meveo.model.payments.DunningLevelEnum;

@WebService(serviceName = "DunningPlanTransitionWs", endpointInterface = "org.meveo.api.ws.DunningPlanTransitionWs")
@Interceptors({ WsRestApiInterceptor.class })
public class DunningPlanTransitionWsImpl extends BaseWs implements DunningPlanTransitionWs {

	 @Inject
	 private DunningPlanTransitionApi dunningPlanTransitionApi;
	 
		@Override
		public ActionStatus create(DunningPlanTransitionDto postData) {
			ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

	        try {
	        	dunningPlanTransitionApi.create(postData, getCurrentUser());
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
		public ActionStatus update(DunningPlanTransitionDto postData) {
			ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

	        try {
	        	dunningPlanTransitionApi.update(postData, getCurrentUser());
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
		public ActionStatus createOrUpdate(DunningPlanTransitionDto postData) {
			ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
			try {
				dunningPlanTransitionApi.createOrUpdate(postData, getCurrentUser());
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
		public DunningPlanTransitionResponseDto find(String dunningPlanCode, DunningLevelEnum dunningLevelFrom, DunningLevelEnum dunningLevelTo) {
			DunningPlanTransitionResponseDto dunningPlanTransitionResponseDto = new DunningPlanTransitionResponseDto();
			try {
				dunningPlanTransitionResponseDto.setDunningPlanTransition(dunningPlanTransitionApi.find(dunningPlanCode, dunningLevelFrom, dunningLevelTo, getCurrentUser()));
			} catch (MeveoApiException e) {
				dunningPlanTransitionResponseDto.getActionStatus().setErrorCode(e.getErrorCode());
				dunningPlanTransitionResponseDto.getActionStatus().setStatus(ActionStatusEnum.FAIL);
				dunningPlanTransitionResponseDto.getActionStatus().setMessage(e.getMessage());
	        } catch (Exception e) {
	            log.error("Failed to execute API", e);
	            dunningPlanTransitionResponseDto.getActionStatus().setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
	            dunningPlanTransitionResponseDto.getActionStatus().setStatus(ActionStatusEnum.FAIL);
	            dunningPlanTransitionResponseDto.getActionStatus().setMessage(e.getMessage());
	        }
			
			return dunningPlanTransitionResponseDto;
		}
		
		@Override
		public ActionStatus remove(String dunningPlanCode, DunningLevelEnum dunningLevelFrom, DunningLevelEnum dunningLevelTo) {
			ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	        try {
	        	dunningPlanTransitionApi.remove(dunningPlanCode, dunningLevelFrom, dunningLevelTo, getCurrentUser());
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