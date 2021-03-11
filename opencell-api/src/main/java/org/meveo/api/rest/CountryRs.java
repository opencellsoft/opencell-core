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
import org.meveo.api.dto.CountryDto;
import org.meveo.api.dto.response.TradingCountriesResponseDto;
import org.meveo.api.dto.response.GetTradingCountryResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Web service for managing {@link org.meveo.model.billing.Country} and {@link org.meveo.model.billing.TradingCountry}.
 * 
 * @author Edward P. Legaspi
 * 
 **/
@Path("/country")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface CountryRs extends IBaseRs {

    /**
     * Search for list of trading countries.
     *
     * @return list of trading countries
     */
    @GET
    @Path("/list")
    TradingCountriesResponseDto list();

    /**
     * Creates a Trading Country base from the supplied country code. If the country code does not exists, a country and tradingCountry records are created
     * 
     * @param countryDto country
     * @return action status
     */
    @POST
    @Path("/")
    ActionStatus create(CountryDto countryDto);

    /**
     * Search Trading country with a given country code.
     * 
     * @param countryCode country code
     * @return {@link org.meveo.api.dto.response.GetCountryResponse}.
     */
    @GET
    @Path("/")
    GetTradingCountryResponse find(@QueryParam("countryCode") String countryCode);

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
     * @param countryDto country
     * @return action status
     */
    @PUT
    @Path("/")
    ActionStatus update(CountryDto countryDto);

    /**
     * Create or update a Trading Country base from the supplied country code. If the country code does not exists, a country and tradingCountry records are created
     *
     * @param countryDto country
     * @return action status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(CountryDto countryDto);

    /**
     * Enable a Trading country with a given country code
     * 
     * @param code Country code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Trading country with a given country code
     * 
     * @param code Country code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    ActionStatus disable(@PathParam("code") String code);
}