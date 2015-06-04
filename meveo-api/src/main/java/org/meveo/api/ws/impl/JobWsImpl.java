package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.job.JobApi;
import org.meveo.api.job.TimerEntityApi;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.ws.JobWs;
import org.meveo.model.jobs.TimerInfoDto;

@WebService(serviceName = "JobWs", endpointInterface = "org.meveo.api.ws.JobWs")
@Interceptors({ LoggingInterceptor.class })
public class JobWsImpl extends BaseWs implements JobWs {

	@Inject
	private JobApi jobApi;
	
	@Inject
	private TimerEntityApi timerEntityApi;

	@Override
	public ActionStatus executeTimer(TimerInfoDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			jobApi.executeTimer(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("error occured while executing timer ",e);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("error generated while executing timer ",e);
		}

		log.debug("RESPONSE={}", result);
		return result;
	}
	
	@Override
	public ActionStatus create(TimerEntityDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			timerEntityApi.create(postData, getCurrentUser());
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
	

}
