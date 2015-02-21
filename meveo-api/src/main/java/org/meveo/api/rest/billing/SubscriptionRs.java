package org.meveo.api.rest.billing;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.account.ActivateServicesDto;
import org.meveo.api.dto.account.ApplyOneShotChargeInstanceDto;
import org.meveo.api.dto.billing.SubscriptionDto;
import org.meveo.api.dto.billing.TerminateSubscriptionDto;
import org.meveo.api.dto.billing.TerminateSubscriptionServicesDto;
import org.meveo.api.dto.response.billing.ListSubscriptionResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * @author Edward P. Legaspi
 **/
@Path("/billing/subscription")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface SubscriptionRs extends IBaseRs {

	@POST
	@Path("/")
	ActionStatus create(SubscriptionDto postData);

	@PUT
	@Path("/")
	ActionStatus update(SubscriptionDto postData);

	@POST
	@Path("/activateServices")
	ActionStatus activateServices(ActivateServicesDto postData);

	@POST
	@Path("/applyOneShotChargeInstance")
	ActionStatus applyOneShotChargeInstance(ApplyOneShotChargeInstanceDto postData);

	@POST
	@Path("/terminate")
	ActionStatus terminateSubscription(TerminateSubscriptionDto postData);

	@POST
	@Path("/terminateServices")
	ActionStatus terminateServices(TerminateSubscriptionServicesDto postData);

	/**
	 * List Subscription filter by userAccountCode.
	 * 
	 * @param userAccountCode
	 * @return
	 */
	@GET
	@Path("/list")
	ListSubscriptionResponseDto listByUserAccount(@QueryParam("userAccountCode") String userAccountCode);

}
