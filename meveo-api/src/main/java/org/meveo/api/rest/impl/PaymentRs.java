package org.meveo.api.rest.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.PaymentApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.PaymentDto;
import org.meveo.api.dto.response.CustomerPaymentsResponse;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.security.RSSecured;

/**
 * @author R.AITYAAZZA
 * 
 */
@RequestScoped
@Path("/payment")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Interceptors({ LoggingInterceptor.class })
@RSSecured
public class PaymentRs extends BaseRs {

	@Inject
	private PaymentApi paymentApi;

	@POST
	@Path("/create")
	public ActionStatus create(PaymentDto paymentDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			paymentApi.createPayment(paymentDto, getCurrentUser());
		} catch (BusinessException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			e.printStackTrace();
		}

		return result;
	}

	@GET
	@Path("/customerPayment")
	public CustomerPaymentsResponse list(
			@QueryParam("customerAccountCode") String customerAccountCode) {
		CustomerPaymentsResponse result = new CustomerPaymentsResponse();
		result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

		try {
			result.setCustomerPaymentDtoList(paymentApi.getPaymentList(
					customerAccountCode, getCurrentUser()));
			result.setBalance(paymentApi.getBalance(customerAccountCode,
					getCurrentUser()));
		} catch (Exception e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}

		return result;
	}
}
