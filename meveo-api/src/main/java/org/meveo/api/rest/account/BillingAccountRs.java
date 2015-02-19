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
import org.meveo.api.dto.account.BillingAccountDto;
import org.meveo.api.dto.response.account.GetBillingAccountResponse;
import org.meveo.api.dto.response.account.ListBillingAccountResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * @author Edward P. Legaspi
 **/
@Path("/account/billingAccount")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface BillingAccountRs extends IBaseRs {

	@POST
	@Path("/")
	ActionStatus create(BillingAccountDto postData);

	@PUT
	@Path("/")
	ActionStatus update(BillingAccountDto postData);

	/**
	 * Search for a billing account with a given code.
	 * 
	 * @param billingAccountCode
	 * @return
	 */
	@GET
	@Path("/")
	GetBillingAccountResponse find(@QueryParam("billingAccountCode") String billingAccountCode);

	@DELETE
	@Path("/{billingAccountCode}")
	ActionStatus remove(@PathParam("billingAccountCode") String billingAccountCode);

	/**
	 * List BillingAccount filter by customerAccountCode.
	 * 
	 * @param customerAccountCode
	 * @return
	 */
	@GET
	@Path("/list")
	ListBillingAccountResponseDto listByCustomerAccount(@QueryParam("customerAccountCode") String customerAccountCode);

}
