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
import org.meveo.api.dto.CountryIsoDto;
import org.meveo.api.dto.response.GetCountriesIsoResponse;
import org.meveo.api.dto.response.GetCountryIsoResponse;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * Web service for managing {@link org.meveo.model.billing.Country}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/countryIso")
@Tag(name = "CountryIso", description = "@%CountryIso")
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
	@Operation(
			summary=" Creates a tradingCountry base from the supplied country code",
			description=" Creates a tradingCountry base from the supplied country code. If the country code does not exists, a country and tradingCountry records are created  ",
			operationId="    POST_CountryIso_create",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(CountryIsoDto countryIsoDto);

    /**
     * Search country with a given country code.
     * 
     * @param countryCode country code
     * @return {@link org.meveo.api.dto.response.GetCountryIsoResponse}.
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search country with a given country code.  ",
			description=" Search country with a given country code.  ",
			operationId="    GET_CountryIso_ ",
			responses= {
				@ApiResponse(description="link org.meveo.api.dto.response.GetCountryIsoResponse}. ",
						content=@Content(
									schema=@Schema(
											implementation= GetCountryIsoResponse.class
											)
								)
				)}
	)
    GetCountryIsoResponse find(@QueryParam("countryCode") String countryCode);

    /**
     * Does not delete a country but the tradingCountry associated to it.
     * 
     * @param countryCode country code
     * @return action status
     */
    @DELETE
    @Path("/{countryCode}")
	@Operation(
			summary=" Does not delete a country but the tradingCountry associated to it.  ",
			description=" Does not delete a country but the tradingCountry associated to it.  ",
			operationId="    DELETE_CountryIso_{countryCode}",
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
     * @param countryIsoDto country iso
     * @return action status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Modify a country",
			description=" Modify a country. Same input parameter as create. The country and tradingCountry are created if they don't exists. The operation fails if the tradingCountry is null.  ",
			operationId="    PUT_CountryIso_update",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(CountryIsoDto countryIsoDto);

    /**
     * Creates or modify a tradingCountry base from the supplied country code.
     * @param countryIsoDto country iso
     * @return action status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Creates or modify a tradingCountry base from the supplied country code. ",
			description=" Creates or modify a tradingCountry base from the supplied country code. ",
			operationId="    POST_CountryIso_createOrUpdate",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(CountryIsoDto countryIsoDto);
    
    /**
     * List all countries.
     * @return list of countries
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" List all countries. ",
			description=" List all countries. ",
			operationId="    GET_CountryIso_list",
			responses= {
				@ApiResponse(description=" list of countries ",
						content=@Content(
									schema=@Schema(
											implementation= GetCountriesIsoResponse.class
											)
								)
				)}
	)
    GetCountriesIsoResponse list();

    /**
     * List countries ISO matching a given criteria
     *
     * @return List of countries ISO
     */
    @GET
    @Path("/listGetAll")
	@Operation(
			summary=" List countries ISO matching a given criteria ",
			description=" List countries ISO matching a given criteria ",
			operationId="    GET_CountryIso_listGetAll",
			responses= {
				@ApiResponse(description=" List of countries ISO ",
						content=@Content(
									schema=@Schema(
											implementation= GetCountriesIsoResponse.class
											)
								)
				)}
	)
    GetCountriesIsoResponse listGetAll();

}
