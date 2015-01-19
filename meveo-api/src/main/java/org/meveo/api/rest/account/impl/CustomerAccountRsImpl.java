package org.meveo.api.rest.account.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.QueryParam;

import org.meveo.api.account.CustomerAccountApi;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.account.CustomerAccountResponse;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.account.CustomerAccountRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * @author R.AITYAAZZA
 * 
 */
@RequestScoped
@Interceptors({ LoggingInterceptor.class })
public class CustomerAccountRsImpl extends BaseRs implements CustomerAccountRs {

	@Inject
	private CustomerAccountApi customerAccountapi;

	@Override
	public CustomerAccountResponse getCustomerAccount(
			@QueryParam("customerAccountCode") String customerAccountCode) {

		CustomerAccountResponse result = new CustomerAccountResponse();
		result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

		try {
			result.setCustomerAccountDto(customerAccountapi.getCustomerAccount(
					customerAccountCode, getCurrentUser()));
		} catch (Exception e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}

		return result;
	}

}
