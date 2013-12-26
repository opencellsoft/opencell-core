package org.meveo.rest.api;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.SubscriptionWithCreditLimitDto;
import org.meveo.commons.utils.ParamBean;
import org.meveo.rest.api.response.TerminateCustomerSubscriptionResponse;
import org.meveo.util.MeveoParamBean;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
@Path("/terminateCustomerSubscription")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class TerminateCustomerSubscriptionWS {

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;

	@Inject
	private Logger log;

	@POST
	@Path("/")
	public TerminateCustomerSubscriptionResponse create(
			SubscriptionWithCreditLimitDto subscriptionDto) {
		return null;
	}

}
