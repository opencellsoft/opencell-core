package org.meveo.api.ws.account;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.meveo.api.dto.response.CustomerAccountResponse;
import org.meveo.api.ws.IBaseWs;

@WebService
public interface CustomerAccountWs extends IBaseWs {

	@WebMethod
	public CustomerAccountResponse getCustomerAccount(String customerAccountCode);

}
