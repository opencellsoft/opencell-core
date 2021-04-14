/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.rest;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.CountryIsoDto;
import org.meveo.api.dto.response.GetCountriesIsoResponse;
import org.meveo.api.dto.response.GetCountryIsoResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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
    @Path("/") 
    GetCountryIsoResponse find(@QueryParam("countryCode") String countryCode);

    /**
     * Does not delete a country but the tradingCountry associated to it.
     * 
     * @param countryCode country code
     * @return action status
     */
    @DELETE
    @Path("/{countryCode}")
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
     * Creates or modify a tradingCountry base from the supplied country code.
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

    /**
     * List countries ISO matching a given criteria
     *
     * @return List of countries ISO
     */
    @GET
    @Path("/listGetAll")
    GetCountriesIsoResponse listGetAll();

}
