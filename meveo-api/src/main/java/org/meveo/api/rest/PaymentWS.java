package org.meveo.api.rest;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.ActionStatus;
import org.meveo.api.ActionStatusEnum;
import org.meveo.api.PaymentApi;
import org.meveo.api.dto.PaymentDto;
import org.meveo.api.rest.response.CustomerPaymentsResponse;
import org.slf4j.Logger;

/**
 * @author R.AITYAAZZA
 * 
 */
@Stateless
@Path("/payment")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class PaymentWS extends BaseWS {

	@Inject
	private Logger log;

	@Inject
	private PaymentApi paymentApi;

	@POST
	@Path("/")
	public ActionStatus create(PaymentDto paymentDto) {
		log.debug("payment.create={}", paymentDto);

		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			paymentDto.setCurrentUserId(Long.valueOf(paramBean.getProperty(
					"asp.api.userId", "1")));
			paymentDto.setProviderId(Long.valueOf(paramBean.getProperty(
					"asp.api.providerId", "1")));

			paymentApi.createPayment(paymentDto);
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
	@Path("/")
	public CustomerPaymentsResponse list(@QueryParam("customerAccountCode") String customerAccountCode) {
		log.debug("payment.list={}", customerAccountCode);

		CustomerPaymentsResponse result = new CustomerPaymentsResponse();
		result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

		try {
			result.setCustomerPaymentDtoList(paymentApi.getPaymentList(
					customerAccountCode));
		} catch (Exception e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}

		return result;
	}
}
