package org.meveo.api.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.ActionStatus;
import org.meveo.api.ActionStatusEnum;
import org.meveo.api.CustomerAccountApi;
import org.meveo.api.rest.response.CustomerAccountResponse;
import org.slf4j.Logger;

/**
 * @author R.AITYAAZZA
 * 
 */
@Path("/customerAccount")
@RequestScoped
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class CustomerAccountWS {

	@Inject
	private Logger log;

	@Inject
	private CustomerAccountApi customerAccountapi;

	@GET
	@Path("/index")
	public ActionStatus index() {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS,
				"MEVEO API Rest Web Service");

		return result;
	}

	@GET
	@Path("/")
	public CustomerAccountResponse getCustomerAccount(
			@QueryParam("customerAccountCode") String customerAccountCode,
			@QueryParam("providerCode") String providerCode) throws Exception {
		log.debug(
				"customerAccount.getCustomer customerAccountCode={}, providerCode={}",
				customerAccountCode, providerCode);

		CustomerAccountResponse result = new CustomerAccountResponse();
		result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

		try {
			result.setCustomerAccountDto(customerAccountapi.getCustomerAccount(
					customerAccountCode, providerCode));
		} catch (Exception e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}

		return result;
	}
	
}
