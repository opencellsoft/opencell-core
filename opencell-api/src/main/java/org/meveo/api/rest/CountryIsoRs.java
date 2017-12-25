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
import org.meveo.api.dto.CountryIsoDto;
import org.meveo.api.dto.response.GetCountriesIsoResponse;
import org.meveo.api.dto.response.GetCountryIsoResponse;

/**
 * Web service for managing {@link org.meveo.model.billing.Country}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/countryIso")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface CountryIsoRs extends IBaseRs {

    /**
     * Creates a tradingCountry base from the supplied country code. If the country code does not exists, a country and tradingCountry records are created
     * 
     * @param countryIsoDto country iso.
     * @return action status
     */
    @POST
    @Path("/")
    ActionStatus create(CountryIsoDto countryIsoDto);

    /**
     * Search country with a given country code.
     * 
     * @param countryCode country code
     * @return {@link org.meveo.api.dto.response.GetCountryIsoResponse}.
     */
    @GET
    @Path("/") GetCountryIsoResponse find(@QueryParam("countryCode") String countryCode);

    /**
     * Does not delete a country but the tradingCountry associated to it.
     * 
     * @param countryCode country code
     * @return action status
     */
    @DELETE
    @Path("/")
    ActionStatus remove(@PathParam("countryCode") String countryCode);

    /**
     * Modify a country. Same input parameter as create. The country and tradingCountry are created if they don't exists. The operation fails if the tradingCountry is null.
     * 
     * @param countryIsoDto country iso
     * @return action status
     */
    @PUT
    @Path("/")
    ActionStatus update(CountryIsoDto countryIsoDto);

    /**
     * @param countryIsoDto country iso
     * @return action status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(CountryIsoDto countryIsoDto);
    
    /**
     * List all countries.
     * @return list of countries
     */
    @GET
    @Path("/list")
    GetCountriesIsoResponse list();

}
