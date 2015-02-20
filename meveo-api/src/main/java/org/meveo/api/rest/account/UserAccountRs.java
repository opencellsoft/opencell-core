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
import org.meveo.api.dto.account.UserAccountDto;
import org.meveo.api.dto.response.account.GetUserAccountResponse;
import org.meveo.api.dto.response.account.ListUserAccountResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * @author Edward P. Legaspi
 **/
@Path("/account/userAccount")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface UserAccountRs extends IBaseRs {

	@POST
	@Path("/")
	ActionStatus create(UserAccountDto postData);

	@PUT
	@Path("/")
	ActionStatus update(UserAccountDto postData);

	/**
	 * Search for a user account with a given code.
	 * 
	 * @param userAccountCode
	 * @return
	 */
	@GET
	@Path("/")
	GetUserAccountResponse find(@QueryParam("userAccountCode") String userAccountCode);

	@DELETE
	@Path("/{userAccountCode}")
	ActionStatus remove(@PathParam("userAccountCode") String userAccountCode);

	/**
	 * List UserAccount filter by billingAccountCode.
	 * 
	 * @param billingAccountCode
	 * @return
	 */
	@GET
	@Path("/list")
	ListUserAccountResponseDto listByBillingAccount(@QueryParam("billingAccountCode") String billingAccountCode);

}
