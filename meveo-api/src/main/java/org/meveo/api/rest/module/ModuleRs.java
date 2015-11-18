package org.meveo.api.rest.module;

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
import org.meveo.api.dto.module.ModuleDto;
import org.meveo.api.dto.response.module.MeveoModuleDtoResponse;
import org.meveo.api.dto.response.module.MeveoModuleDtosResponse;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

@Path("/module")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface ModuleRs extends IBaseRs {
	
	@POST
	@Path("/")
	ActionStatus create(ModuleDto moduleDto);

	@PUT
	@Path("/")
	ActionStatus update(ModuleDto moduleDto);

	@DELETE
	@Path("/{code}")
	ActionStatus delete(@PathParam("code") String code);
	
	@GET
	@Path("/list")
	MeveoModuleDtosResponse list();
	
	@GET
	@Path("/")
	MeveoModuleDtoResponse get(@QueryParam("code")String code);

}
