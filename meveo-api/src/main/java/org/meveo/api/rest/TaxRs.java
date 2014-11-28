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
import org.meveo.api.dto.TaxDto;
import org.meveo.api.dto.response.GetTaxResponse;
import org.meveo.api.rest.security.RSSecured;

/**
 * @author Edward P. Legaspi
 **/
@Path("/tax")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface TaxRs extends IBaseRs {

	@Path("/")
	@POST
	public ActionStatus create(TaxDto postData);

	@Path("/")
	@PUT
	public ActionStatus update(TaxDto postData);

	@Path("/")
	@GET
	public GetTaxResponse find(@QueryParam("taxCode") String taxCode);

	@Path("/{taxCode}")
	@DELETE
	public ActionStatus remove(@PathParam("taxCode") String taxCode);

}
