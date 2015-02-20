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
import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.response.account.GetAccessResponse;
import org.meveo.api.dto.response.account.ListAccessResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * @author Edward P. Legaspi
 **/
@Path("/account/access")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface AccessRs extends IBaseRs {

	@POST
	@Path("/")
	ActionStatus create(AccessDto postData);

	@PUT
	@Path("/")
	ActionStatus update(AccessDto postData);

	/**
	 * Search for a user account with a given code.
	 * 
	 * @param userAccountCode
	 * @return
	 */
	@GET
	@Path("/")
	GetAccessResponse find(@QueryParam("accessCode") String accessCode,
			@QueryParam("subscriptionCode") String subscriptionCode);

	@DELETE
	@Path("/{accessCode}/{subscriptionCode}")
	ActionStatus remove(@PathParam("accessCode") String accessCode,
			@PathParam("subscriptionCode") String subscriptionCode);

	/**
	 * List Access filter by subscriptionCode.
	 * 
	 * @param customerAccountCode
	 * @return
	 */
	@GET
	@Path("/list")
	ListAccessResponseDto listBySubscription(@QueryParam("subscriptionCode") String subscriptionCode);

}
