package org.meveo.api.rest.tmforum.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.billing.OrderApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.tmforum.OrderRs;
import org.slf4j.Logger;
import org.tmf.dsmapi.catalog.resource.order.ProductOrder;

@RequestScoped
@Interceptors({ LoggingInterceptor.class })
public class OrderRsImpl extends BaseRs implements OrderRs {

	@Inject
	private Logger log;

	@Inject
	private OrderApi orderApi;

	@Override
	public Response createOrder(ProductOrder productOrder) {
		Response.ResponseBuilder responseBuilder = null;
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			orderApi.create(productOrder, getCurrentUser());
			responseBuilder = Response.ok();
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			responseBuilder = Response.status(Response.Status.BAD_REQUEST);
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			responseBuilder = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
		}

		responseBuilder.entity(result);

		log.debug("RESPONSE={}", result);
		return responseBuilder.build();
	}

}
