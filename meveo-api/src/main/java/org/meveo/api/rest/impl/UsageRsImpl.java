package org.meveo.api.rest.impl;

import java.util.Date;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.UsageApi;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.usage.UsageRequestDto;
import org.meveo.api.dto.usage.UsageResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.UsageRs;


@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class UsageRsImpl extends BaseRs implements UsageRs {

    @Inject UsageApi usageApi;

	@Override
	public UsageResponseDto find(String userAccountCode, Date fromDate, Date toDate) {
		UsageResponseDto result = new UsageResponseDto();

	        try {
	        	UsageRequestDto usageRequestDto = new UsageRequestDto();
	        	usageRequestDto.setFromDate(fromDate);
	        	usageRequestDto.setToDate(toDate);
	        	usageRequestDto.setUserAccountCode(userAccountCode);
	            result = usageApi.find(usageRequestDto, getCurrentUser());
	        } catch (MeveoApiException e) {
	            result.getActionStatus().setErrorCode(e.getErrorCode());
	            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
	            result.getActionStatus().setMessage(e.getMessage());
	        } catch (Exception e) {
	            log.error("Failed to execute API", e);
	            result.getActionStatus().setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
	            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
	            result.getActionStatus().setMessage(e.getMessage());
	        }

	        return result;
	    }
}
