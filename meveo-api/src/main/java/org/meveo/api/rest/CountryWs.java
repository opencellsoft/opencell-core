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
import org.meveo.api.dto.CountryDto;
import org.meveo.api.dto.response.GetCountryResponse;
import org.meveo.api.rest.security.WSSecured;

/**
 * @author Edward P. Legaspi
 **/
@Path("/country")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@WSSecured
public interface CountryWs extends IBaseWs {

	@POST
	@Path("/")
	public ActionStatus create(CountryDto countryDto);

	@GET
	@Path("/")
	public GetCountryResponse find(@QueryParam("countryCode") String countryCode);

	@DELETE
	@Path("/{countryCode}/{currencyCode}")
	public ActionStatus remove(@PathParam("countryCode") String countryCode,
			@PathParam("currencyCode") String currencyCode);

	@PUT
	@Path("/")
	public ActionStatus update(CountryDto countryDto);

}
