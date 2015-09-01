package org.meveo.api.rest;

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
import org.meveo.api.dto.ScriptInstanceDto;
import org.meveo.api.dto.response.GetScriptInstanceResponseDto;
import org.meveo.api.rest.security.RSSecured;

/**
 * @author Edward P. Legaspi
 **/
@Path("/scriptInstance")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface ScriptInstanceRs extends IBaseRs {

	@POST
	@Path("/")
	ActionStatus create(ScriptInstanceDto postData);

	@PUT
	@Path("/")
	ActionStatus update(ScriptInstanceDto postData);

	@DELETE
	@Path("/{scriptInstanceCode}")
	ActionStatus remove(@PathParam("scriptInstanceCode") String scriptInstanceCode);

	@GET
	@Path("/")
	GetScriptInstanceResponseDto find(@QueryParam("scriptInstanceCode") String scriptInstanceCode);
	
	@POST
	@Path("/createOrUpdate")
	ActionStatus createOrUpdate(ScriptInstanceDto postData);
}
