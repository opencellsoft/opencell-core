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
import org.meveo.api.dto.SellerDto;
import org.meveo.api.dto.response.GetSellerResponse;
import org.meveo.api.rest.security.RSSecured;

/**
 * @author Edward P. Legaspi
 **/
@Path("/seller")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface SellerRs extends IBaseRs {

	@Path("/")
	@POST
	public ActionStatus create(SellerDto postData);

	@Path("/")
	@PUT
	public ActionStatus update(SellerDto postData);

	@Path("/")
	@GET
	public GetSellerResponse find(@QueryParam("sellerCode") String sellerCode);

	@Path("/{sellerCode}")
	@DELETE
	public ActionStatus remove(@PathParam("sellerCode") String sellerCode);

}
