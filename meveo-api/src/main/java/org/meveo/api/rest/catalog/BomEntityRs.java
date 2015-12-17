package org.meveo.api.rest.catalog;

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
import org.meveo.api.dto.catalog.BomEntityDto;
import org.meveo.api.dto.response.catalog.GetBomEntityResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * @author Edward P. Legaspi
 **/
@Path("/catalog/bomEntity")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface BomEntityRs extends IBaseRs {

	@Path("/")
	@POST
	ActionStatus create(BomEntityDto postData);

	@Path("/")
	@PUT
	ActionStatus update(BomEntityDto postData);

	@Path("/")
	@GET
	GetBomEntityResponseDto find(@QueryParam("bomEntityCode") String bomEntityCode);

	@Path("/{bomEntityCode}")
	@DELETE
	ActionStatus remove(@PathParam("bomEntityCode") String bomEntityCode);

	@Path("/createOrUpdate")
	@POST
	ActionStatus createOrUpdate(BomEntityDto postData);

}
