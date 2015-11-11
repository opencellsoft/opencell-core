package org.meveo.api.tmforum.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.rest.security.RSSecured;
import org.tmf.dsmapi.catalog.resource.order.ProductOrder;

@Path("/orderManagement/productOrder")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface OrderRs {
	
	@POST
	ActionStatus createOrder(ProductOrder productOrder);
}
