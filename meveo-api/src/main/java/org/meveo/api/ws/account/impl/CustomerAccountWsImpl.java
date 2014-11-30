package org.meveo.api.ws.account.impl;

import javax.inject.Inject;
import javax.jws.WebService;
import javax.ws.rs.QueryParam;

import org.meveo.api.account.CustomerAccountApi;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.CustomerAccountResponse;
import org.meveo.api.ws.account.CustomerAccountWs;
import org.meveo.api.ws.impl.BaseWs;

@WebService(serviceName = "CustomerAccountWs", endpointInterface = "org.meveo.api.ws.account.CustomerAccountWs")
public class CustomerAccountWsImpl extends BaseWs implements CustomerAccountWs {

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
