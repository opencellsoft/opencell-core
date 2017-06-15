package org.meveo.api.rest.dwh.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.dwh.GetListMeasurableQuantityResponse;
import org.meveo.api.dto.dwh.GetMeasurableQuantityResponse;
import org.meveo.api.dto.dwh.MeasurableQuantityDto;
import org.meveo.api.dto.dwh.MeasuredValueDto;
import org.meveo.api.dwh.MeasurableQuantityApi;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.dwh.MeasurableQuantityRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.model.dwh.MeasurementPeriodEnum;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class MeasurableQuantityRsImpl extends BaseRs implements MeasurableQuantityRs {

	@Inject
	private MeasurableQuantityApi measurableQuantityApi;

	@Override
	public ActionStatus create(MeasurableQuantityDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			measurableQuantityApi.create(postData);
		} catch (Exception e) {
			processException(e, result);
		}

		return result;
	}

	@Override
	public ActionStatus update(MeasurableQuantityDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			measurableQuantityApi.update(postData);
		} catch (Exception e) {
			processException(e, result);
		}

		return result;
	}

	/**
	 * 
	 * @param code
	 * @param fromDate
	 *            format yyyy-MM-dd'T'HH:mm:ss or yyyy-MM-dd
	 * @param toDate
	 *            format yyyy-MM-dd'T'HH:mm:ss or yyyy-MM-dd
	 * @param period
	 * @param mqCode
	 * @return
	 */
	@Override
	public Response findMVByDateAndPeriod(String code, Date fromDate, Date toDate, MeasurementPeriodEnum period, String mqCode) {
		Response.ResponseBuilder responseBuilder = null;
		List<MeasuredValueDto> result = new ArrayList<>();

		try {
			result = measurableQuantityApi.findMVByDateAndPeriod(code, fromDate, toDate, period, mqCode);
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

	@Override
	public GetMeasurableQuantityResponse find(String code) {
		GetMeasurableQuantityResponse result = new GetMeasurableQuantityResponse();
		try {
			result.setMeasurableQuantityDto(measurableQuantityApi.find(code));
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}

		return result;
	}

	@Override
	public ActionStatus remove(String code) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try {
			measurableQuantityApi.remove(code);
		} catch (Exception e) {
			processException(e, result);
		}

		return result;
	}

	@Override
	public GetListMeasurableQuantityResponse list() {
		GetListMeasurableQuantityResponse result = new GetListMeasurableQuantityResponse();
		try {
			result.setListMeasurableQuantityDto(measurableQuantityApi.list(null));
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}
		return result;
	}
}