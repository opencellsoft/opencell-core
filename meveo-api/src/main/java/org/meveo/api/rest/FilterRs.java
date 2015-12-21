package org.meveo.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.rest.security.RSSecured;

/**
 * @author Tyshan Shi
 *
**/
@Path("/filter")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface FilterRs extends IBaseRs{

	@Path("/createOrUpdate")
	@POST
	public ActionStatus createOrUpdate(FilterDto postData);
}
