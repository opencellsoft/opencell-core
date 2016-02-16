package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.dwh.MeasurableQuantityDto;
import org.meveo.api.dwh.MeasurableQuantityApi;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.ws.ReportingWs;

/**
 * @author Edward P. Legaspi
 **/
@Interceptors({ LoggingInterceptor.class })
public class ReportingWsImpl extends BaseWs implements ReportingWs {
	
	@Inject
	private MeasurableQuantityApi measurableQuantityApi;

	@Override
	public ActionStatus createMeasurableQuantity(MeasurableQuantityDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			measurableQuantityApi.create(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

}
