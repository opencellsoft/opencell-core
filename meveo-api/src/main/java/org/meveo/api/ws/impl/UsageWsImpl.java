package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.UsageApi;
import org.meveo.api.dto.usage.UsageChargeAggregateResponseDto;
import org.meveo.api.dto.usage.UsageRequestDto;
import org.meveo.api.dto.usage.UsageResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.ws.UsageWs;

/**
 * @author Mbarek
 **/
@WebService(serviceName = "UsageWs", endpointInterface = "org.meveo.api.ws.UsageWs")
@Interceptors({ WsRestApiInterceptor.class })
public class UsageWsImpl extends BaseWs implements UsageWs {

	@Inject
	private UsageApi usageApi;

	@Override
	public UsageResponseDto findUsage(UsageRequestDto usageRequestDto) {
		UsageResponseDto result = new UsageResponseDto();
		try {
			result = usageApi.find(usageRequestDto, getCurrentUser());
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}

		return result;
	}

	@Override
	public UsageChargeAggregateResponseDto chargeAggregate(UsageRequestDto usageRequestDto) {
		UsageChargeAggregateResponseDto result = new UsageChargeAggregateResponseDto();
		try {
			result = usageApi.chargeAggregate(usageRequestDto, getCurrentUser());
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}

		return result;
	}

}
