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

/**
 * Web service for managing {@link org.meveo.model.billing.Country} and {@link org.meveo.model.billing.TradingCountry}.
 * 
 * @author Edward P. Legaspi
 * 
 * @deprecated will be renammed to  TradingCountryRs
 **/
@Path("/country")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface CountryRs extends IBaseRs {

    /**
     * Creates a tradingCountry base from the supplied country code. If the country code does not exists, a country and tradingCountry records are created
     * 
     * @param countryDto country
     * @return action status
     */
    @POST
    @Path("/")
    ActionStatus create(CountryDto countryDto); 

    /**
     * Search country with a given country code.
     * 
     * @param countryCode country code
     * @return {@link org.meveo.api.dto.response.GetCountryResponse}.
     */
    @GET
    @Path("/")
    GetCountryResponse find(@QueryParam("countryCode") String countryCode);

    /**
     * Does not delete a country but the tradingCountry associated to it.
     * 
     * @param countryCode country code
     * @param currencyCode currency code
     * @return action status
     */
    @DELETE
    @Path("/{countryCode}/{currencyCode}")
    ActionStatus remove(@PathParam("countryCode") String countryCode, @PathParam("currencyCode") String currencyCode);

    /**
     * Modify a country. Same input parameter as create. The country and tradingCountry are created if they don't exists. The operation fails if the tradingCountry is null.
     * 
     * @param countryDto country 
     * @return action status
     */
    @PUT
    @Path("/")
    ActionStatus update(CountryDto countryDto);

    /**
     * @param countryDto country
     * @return action status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(CountryDto countryDto);

}
