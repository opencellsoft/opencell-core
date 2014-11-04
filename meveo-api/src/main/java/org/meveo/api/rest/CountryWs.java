package org.meveo.api.rest;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.response.GetCountryResponse;

/**
 * @author Edward P. Legaspi
 **/
@Path("/country")
@RequestScoped
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface CountryWs {

	@GET
	@Path("/")
	public GetCountryResponse find(@QueryParam("countryCode") String countryCode);

}
