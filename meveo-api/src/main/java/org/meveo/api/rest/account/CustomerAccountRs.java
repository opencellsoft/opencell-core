package org.meveo.api.rest.account;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.account.CustomerAccountDto;
import org.meveo.api.dto.payment.DunningInclusionExclusionDto;
import org.meveo.api.dto.response.account.GetCustomerAccountResponse;
import org.meveo.api.dto.response.account.ListCustomerAccountResponseDto;
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

	@POST
	@Path("/")
	ActionStatus create(CustomerAccountDto postData);

	@PUT
	@Path("/")
	ActionStatus update(CustomerAccountDto postData);

	/**
	 * Search for a customer account with a given code.
	 * 
	 * @param customerAccountCode
	 * @return
	 */
	@GET
	@Path("/")
	GetCustomerAccountResponse find(@QueryParam("customerAccountCode") String customerAccountCode);

	@DELETE
	@Path("/{customerAccountCode}")
	ActionStatus remove(@PathParam("customerAccountCode") String customerAccountCode);

	/**
	 * List CustomerAccount filter by customerCode.
	 * 
	 * @param customerCode
	 * @return
	 */
	@GET
	@Path("/list")
	ListCustomerAccountResponseDto listByCustomer(@QueryParam("customerCode") String customerCode);
	

	@PUT
	@Path("/dunningInclusionExclusion")
	ActionStatus dunningInclusionExclusion(DunningInclusionExclusionDto DunningInclusionExclusionDto);

}
