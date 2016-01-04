package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.FilterApi;
import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.ws.FilterWs;

/**
 * @author Edward P. Legaspi
 **/
@WebService(serviceName = "FilterWs", endpointInterface = "org.meveo.api.ws.FilterWs")
@Interceptors({ LoggingInterceptor.class })
public class FilterWsImpl extends BaseWs implements FilterWs {

	@Inject
	private FilterApi filterApi;

	@Override
	public ActionStatus createOrUpdateFilter(FilterDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			filterApi.createOrUpdate(postData, this.getCurrentUser());
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
