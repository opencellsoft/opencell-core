package org.meveo.api.rest.account;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.response.account.CustomerAccountResponse;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * Web service for managing customer account.
 * 
 * @author R.AITYAAZZA
 */
@Path("/account/customerAccount")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface CustomerAccountRs extends IBaseRs {

	/**
	 * Search for a customer account with a given code.
	 * 
	 * @param customerAccountCode
	 * @return
	 */
	@GET
	@Path("/")
	public CustomerAccountResponse getCustomerAccount(
			@QueryParam("customerAccountCode") String customerAccountCode);

}
