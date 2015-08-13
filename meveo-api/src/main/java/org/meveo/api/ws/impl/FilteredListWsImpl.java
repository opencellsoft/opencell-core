package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.response.billing.FilteredListResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.filter.FilteredListApi;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.ws.FilteredListWs;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@WebService(serviceName = "FilteredListWs", endpointInterface = "org.meveo.api.ws.FilteredListWs")
@Interceptors({ LoggingInterceptor.class })
public class FilteredListWsImpl extends BaseWs implements FilteredListWs {

	@Inject
	private Logger log;

	@Inject
	private FilteredListApi filteredListApi;

	@Override
	public FilteredListResponseDto list(String filter) {
		FilteredListResponseDto result = new FilteredListResponseDto();
		try {
			String response = filteredListApi.list(filter, getCurrentUser().getProvider());
			result.getActionStatus().setMessage(response);
		} catch (MeveoApiException e) {
			log.debug("RESPONSE={}", e);
			result.getActionStatus().setErrorCode(MeveoApiErrorCode.BUSINESS_API_EXCEPTION);
			result.getActionStatus().setMessage(e.getMessage());
		} catch (Exception e) {
			log.debug("RESPONSE={}", e);
			result.getActionStatus().setErrorCode(MeveoApiErrorCode.BUSINESS_API_EXCEPTION);
			result.getActionStatus().setMessage(e.getMessage());
		}

		return result;
	}

}
