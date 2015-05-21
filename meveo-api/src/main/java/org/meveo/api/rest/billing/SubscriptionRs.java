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
import org.meveo.api.dto.account.ApplyOneShotChargeInstanceRequestDto;
import org.meveo.api.dto.billing.ActivateServicesRequestDto;
import org.meveo.api.dto.billing.InstantiateServicesRequestDto;
import org.meveo.api.dto.billing.SubscriptionDto;
import org.meveo.api.dto.billing.TerminateSubscriptionRequestDto;
import org.meveo.api.dto.billing.TerminateSubscriptionServicesRequestDto;
import org.meveo.api.dto.response.billing.GetSubscriptionResponseDto;
import org.meveo.api.dto.response.billing.SubscriptionsResponseDto;
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
	@Path("/instantiateServices")
	ActionStatus instantiateServices(InstantiateServicesRequestDto postData);

	@POST
	@Path("/activateServices")
	ActionStatus activateServices(ActivateServicesRequestDto postData);

	@POST
	@Path("/applyOneShotChargeInstance")
	ActionStatus applyOneShotChargeInstance(ApplyOneShotChargeInstanceRequestDto postData);

	@POST
	@Path("/terminate")
	ActionStatus terminateSubscription(TerminateSubscriptionRequestDto postData);

	@POST
	@Path("/terminateServices")
	ActionStatus terminateServices(TerminateSubscriptionServicesRequestDto postData);

	/**
	 * List Subscription filter by userAccountCode.
	 * 
	 * @param userAccountCode
	 * @return
	 */
	@GET
	@Path("/list")
	SubscriptionsResponseDto listByUserAccount(@QueryParam("userAccountCode") String userAccountCode);

	@GET
	@Path("/")
	GetSubscriptionResponseDto findSubscription(@QueryParam("subscriptionCode") String subscriptionCode);

}
