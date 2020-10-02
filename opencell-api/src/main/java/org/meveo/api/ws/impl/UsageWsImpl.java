/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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
@Deprecated
public class UsageWsImpl extends BaseWs implements UsageWs {

	@Inject
	private UsageApi usageApi;

	@Override
	public UsageResponseDto findUsage(UsageRequestDto usageRequestDto) {
		UsageResponseDto result = new UsageResponseDto();
		try {
			result = usageApi.find(usageRequestDto);
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}

		return result;
	}

	@Override
	public UsageChargeAggregateResponseDto chargeAggregate(UsageRequestDto usageRequestDto) {
		UsageChargeAggregateResponseDto result = new UsageChargeAggregateResponseDto();
		try {
			result = usageApi.chargeAggregate(usageRequestDto);
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}

		return result;
	}

}
