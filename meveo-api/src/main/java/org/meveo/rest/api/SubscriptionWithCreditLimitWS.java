package org.meveo.rest.api;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.commons.utils.ParamBean;
import org.meveo.util.MeveoParamBean;

/**
 * @author Edward P. Legaspi
 * @since Nov 13, 2013
 **/
@Stateless
@Path("/subscriptionWithCreditLimit")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class SubscriptionWithCreditLimitWS {

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;

}
