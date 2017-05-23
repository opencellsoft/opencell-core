package org.meveo.api.rest.impl;

import java.util.Date;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.UsageApi;
import org.meveo.api.dto.usage.UsageChargeAggregateResponseDto;
import org.meveo.api.dto.usage.UsageRequestDto;
import org.meveo.api.dto.usage.UsageResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.UsageRs;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class UsageRsImpl extends BaseRs implements UsageRs {

    @Inject
    UsageApi usageApi;

    @Override
    public UsageResponseDto find(String userAccountCode, Date fromDate, Date toDate) {
        UsageResponseDto result = new UsageResponseDto();

        try {
            UsageRequestDto usageRequestDto = new UsageRequestDto();
            usageRequestDto.setFromDate(fromDate);
            usageRequestDto.setToDate(toDate);
            usageRequestDto.setUserAccountCode(userAccountCode);
            result = usageApi.find(usageRequestDto);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public UsageChargeAggregateResponseDto chargeAggregate(String userAccountCode, Date fromDate, Date toDate) {
        UsageChargeAggregateResponseDto result = new UsageChargeAggregateResponseDto();
        try {
            UsageRequestDto usageRequestDto = new UsageRequestDto();
            usageRequestDto.setFromDate(fromDate);
            usageRequestDto.setToDate(toDate);
            usageRequestDto.setUserAccountCode(userAccountCode);
            result = usageApi.chargeAggregate(usageRequestDto);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }
}
