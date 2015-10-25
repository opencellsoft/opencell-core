package org.meveo.api.tmforum.rest.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.billing.OrderApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.tmforum.rest.OrderRs;
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
	public ActionStatus createOrder(ProductOrder productOrder) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			orderApi.create(productOrder, getCurrentUser());
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
