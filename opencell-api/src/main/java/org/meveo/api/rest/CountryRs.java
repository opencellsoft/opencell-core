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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.CountryDto;
import org.meveo.api.dto.response.TradingCountriesResponseDto;
import org.meveo.api.dto.response.GetTradingCountryResponse;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * Web service for managing {@link org.meveo.model.billing.Country} and {@link org.meveo.model.billing.TradingCountry}.
 * 
 * @author Edward P. Legaspi
 * 
 **/
@Path("/country")
@Tag(name = "Country", description = "@%Country")
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
	@Operation(
			summary=" Search for list of trading countries. ",
			description=" Search for list of trading countries. ",
			operationId="    GET_Country_list",
			responses= {
				@ApiResponse(description=" list of trading countries ",
						content=@Content(
									schema=@Schema(
											implementation= TradingCountriesResponseDto.class
											)
								)
				)}
	)
    TradingCountriesResponseDto list();

    /**
     * Creates a Trading Country base from the supplied country code. If the country code does not exists, a country and tradingCountry records are created
     * 
     * @param countryDto country
     * @return action status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Creates a Trading Country base from the supplied country code",
			description=" Creates a Trading Country base from the supplied country code. If the country code does not exists, a country and tradingCountry records are created  ",
			operationId="    POST_Country_create",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(CountryDto countryDto);

    /**
     * Search Trading country with a given country code.
     * 
     * @param countryCode country code
     * @return {@link org.meveo.api.dto.response.GetCountryResponse}.
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search Trading country with a given country code.  ",
			description=" Search Trading country with a given country code.  ",
			operationId="    GET_Country_search",
			responses= {
				@ApiResponse(description="link org.meveo.api.dto.response.GetCountryResponse}. ",
						content=@Content(
									schema=@Schema(
											implementation= GetTradingCountryResponse.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" Does not delete a country but the tradingCountry associated to it.  ",
			description=" Does not delete a country but the tradingCountry associated to it.  ",
			operationId="    DELETE_Country_{countryCode}_{currencyCode}",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("countryCode") String countryCode, @PathParam("currencyCode") String currencyCode);

    /**
     * Does not delete a country but the tradingCountry associated to it.
     *
     * @param countryCode country code
     * @return action status
     */
    @DELETE
    @Path("/{countryCode}")
	@Operation(
			summary=" Does not delete a country but the tradingCountry associated to it. ",
			description=" Does not delete a country but the tradingCountry associated to it. ",
			operationId="    DELETE_Country_{countryCode}",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("countryCode") String countryCode);

    /**
     * Modify a country. Same input parameter as create. The country and tradingCountry are created if they don't exists. The operation fails if the tradingCountry is null.
     * 
     * @param countryDto country
     * @return action status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Modify a country",
			description=" Modify a country. Same input parameter as create. The country and tradingCountry are created if they don't exists. The operation fails if the tradingCountry is null.  ",
			operationId="    PUT_Country_update",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(CountryDto countryDto);

    /**
     * Create or update a Trading Country base from the supplied country code. If the country code does not exists, a country and tradingCountry records are created
     *
     * @param countryDto country
     * @return action status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create or update a Trading Country base from the supplied country code",
			description=" Create or update a Trading Country base from the supplied country code. If the country code does not exists, a country and tradingCountry records are created ",
			operationId="    POST_Country_createOrUpdate",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(CountryDto countryDto);

    /**
     * Enable a Trading country with a given country code
     * 
     * @param code Country code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
	@Operation(
			summary=" Enable a Trading country with a given country code  ",
			description=" Enable a Trading country with a given country code  ",
			operationId="    POST_Country_{code}_enable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Trading country with a given country code
     * 
     * @param code Country code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
	@Operation(
			summary=" Disable a Trading country with a given country code  ",
			description=" Disable a Trading country with a given country code  ",
			operationId="    POST_Country_{code}_disable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus disable(@PathParam("code") String code);
}
