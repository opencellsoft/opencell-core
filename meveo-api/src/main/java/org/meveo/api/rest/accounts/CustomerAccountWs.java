package org.meveo.api.rest.accounts;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.CustomerAccountApi;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.CustomerAccountResponse;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.BaseWs;
import org.meveo.api.rest.security.WSSecured;

/**
 * @author R.AITYAAZZA
 * 
 */
@Path("/customerAccount")
@RequestScoped
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Interceptors({ LoggingInterceptor.class })
@WSSecured
public class CustomerAccountWs extends BaseWs {

	@Inject
	private CustomerAccountApi customerAccountapi;

	@GET
	@Path("/")
	public CustomerAccountResponse getCustomerAccount(
			@QueryParam("customerAccountCode") String customerAccountCode)
			throws Exception {
		
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
