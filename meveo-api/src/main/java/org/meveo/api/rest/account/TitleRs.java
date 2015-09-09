package org.meveo.api.rest.account;

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
import org.meveo.api.dto.response.TitleDto;
import org.meveo.api.dto.response.account.TitleResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

@Path("/account/title")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface TitleRs extends IBaseRs {
	
	@POST
	@Path("/")
	ActionStatus create(TitleDto postData);
	
	@GET
	@Path("/")
	TitleResponseDto find(@QueryParam("titleCode") String titleCode);
	
	@PUT
	@Path("/")
	ActionStatus update(TitleDto postData);
	
	@DELETE
	@Path("/{titleCode}")
	public ActionStatus remove(@PathParam("titleCode") String titleCode);
}
