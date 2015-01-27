package org.meveo.api.rest.billing;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.account.ActivateServicesDto;
import org.meveo.api.dto.account.ApplyOneShotChargeInstanceDto;
import org.meveo.api.dto.billing.SubscriptionDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * @author Edward P. Legaspi
 **/
@Path("/account/subscription")
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

}
