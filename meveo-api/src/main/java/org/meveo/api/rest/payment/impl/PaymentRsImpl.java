package org.meveo.api.rest.payment.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.QueryParam;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.payment.PaymentDto;
import org.meveo.api.dto.response.CustomerPaymentsResponse;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.payment.PaymentApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.payment.PaymentRs;
import org.slf4j.Logger;

/**
 * @author R.AITYAAZZA
 * 
 */
@RequestScoped
@Interceptors({ LoggingInterceptor.class })
public class PaymentRsImpl extends BaseRs implements PaymentRs {

	@Inject
	private PaymentApi paymentApi;

	@Inject
	private Logger log;

	@Override
	public ActionStatus create(PaymentDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			paymentApi.createPayment(postData, getCurrentUser());
		} catch (BusinessException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error(e.getMessage());
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public CustomerPaymentsResponse list(@QueryParam("customerAccountCode") String customerAccountCode) {
		CustomerPaymentsResponse result = new CustomerPaymentsResponse();
		result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

		try {
			result.setCustomerPaymentDtoList(paymentApi.getPaymentList(customerAccountCode, getCurrentUser()));
			result.setBalance(paymentApi.getBalance(customerAccountCode, getCurrentUser()));
		} catch (Exception e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

}
