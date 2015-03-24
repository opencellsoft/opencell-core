package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.job.TimerInfoDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.job.JobApi;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.ws.JobWs;
import org.slf4j.Logger;

@WebService(serviceName = "JobWs", endpointInterface = "org.meveo.api.ws.JobWs")
@Interceptors({ LoggingInterceptor.class })
public class JobWsImpl extends BaseWs implements JobWs {

	@Inject
	private Logger log;

	@Inject
	private JobApi jobApi;

	@Override
	public ActionStatus executeTimer(TimerInfoDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			jobApi.executeTimer(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error(e.getMessage());
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

}
