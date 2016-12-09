package org.meveo.api.rest.dwh.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.dwh.MeasurableQuantityDto;
import org.meveo.api.dto.dwh.MeasuredValueDto;
import org.meveo.api.dwh.MeasurableQuantityApi;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.dwh.MeasurableQuantityRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.dwh.MeasurementPeriodEnum;
import org.meveo.model.shared.DateUtils;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class MeasurableQuantityRsImpl extends BaseRs implements MeasurableQuantityRs {

    @Inject
    private MeasurableQuantityApi measurableQuantityApi;

    @Override
    public ActionStatus create(MeasurableQuantityDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            measurableQuantityApi.create(postData, getCurrentUser());
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
    public ActionStatus update(MeasurableQuantityDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            measurableQuantityApi.update(postData, getCurrentUser());
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

	/**
	 * 
	 * @param code
	 * @param fromDate format yyyy-MM-dd'T'hh:mm:ss or yyyy-MM-dd
	 * @param toDate   format yyyy-MM-dd'T'hh:mm:ss or yyyy-MM-dd
	 * @param period
	 * @param mqCode
	 * @return
	 */    
	@Override
	public Response findMVByDateAndPeriod(String code, String fromDate, String toDate, MeasurementPeriodEnum period, String mqCode) {
		Response.ResponseBuilder responseBuilder = null;
		List<MeasuredValueDto> result = new ArrayList<>();
		
        try {
        	Date from = null,to = null;
        	if(!StringUtils.isBlank(fromDate)){        		
        		from =DateUtils.guessDate(fromDate, "yyyy-MM-dd","yyyy-MM-dd'T'hh:mm:ss");     		
        	}
        	if(!StringUtils.isBlank(toDate)){
        		to =DateUtils.guessDate(toDate, "yyyy-MM-dd","yyyy-MM-dd'T'hh:mm:ss");
        	}
        		
        	result = measurableQuantityApi.findMVByDateAndPeriod(code,from , to, period, mqCode, getCurrentUser());
        	responseBuilder = Response.ok();
			responseBuilder.entity(result);
        } catch (MeveoApiException e) {
        	log.error(e.getLocalizedMessage());
			responseBuilder = Response.status(Response.Status.BAD_REQUEST).entity(result);
			responseBuilder.entity(e.getLocalizedMessage());
        } catch (Exception e) {
        	log.error(e.getLocalizedMessage());
			responseBuilder = Response.status(Response.Status.BAD_REQUEST).entity(result);
			responseBuilder.entity(e.getLocalizedMessage());
        }

        Response response = responseBuilder.build();
		log.debug("RESPONSE={}", response.getEntity());
		return response;
	}
}