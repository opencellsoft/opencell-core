package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.payment.PaymentDto;
import org.meveo.api.dto.response.CustomerPaymentsResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.PaymentApi;
import org.meveo.api.ws.PaymentWs;
import org.meveo.model.admin.User;

@WebService(serviceName = "PaymentWs", endpointInterface = "org.meveo.api.ws.PaymentWs")
@Interceptors({ WsRestApiInterceptor.class })
public class PaymentWsImpl extends BaseWs implements PaymentWs {

	@Inject
	private PaymentApi paymentApi;

	@Override
	public ActionStatus create(PaymentDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			User user = getCurrentUser();
			paymentApi.createPayment(postData,user);
		} catch (BusinessException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("error occurred while creating payment ",e);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("error generated while creating payment ",e);
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public CustomerPaymentsResponse list(String customerAccountCode) {
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
