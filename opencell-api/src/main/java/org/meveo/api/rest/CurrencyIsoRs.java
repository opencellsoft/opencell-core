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
import org.meveo.api.dto.CurrencyIsoDto;
import org.meveo.api.dto.response.GetCurrenciesIsoResponse;
import org.meveo.api.dto.response.GetCurrencyIsoResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Web service for managing Currency.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/currencyIso")
@Tag(name = "CurrencyIso", description = "@%CurrencyIso")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface CurrencyIsoRs extends IBaseRs {

    /**
     * Creates currency base on currency code. If the currency code does not exists, a currency record is created
     * 
     * @param currencyIsoDto currency iso
     * @return action status.
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Creates currency base on currency code. If the currency code does not exists, a currency record is created  ",
			description=" Creates currency base on currency code. If the currency code does not exists, a currency record is created  ",
			operationId="    POST_CurrencyIso_ ",
			responses= {
				@ApiResponse(description=" action status. ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(CurrencyIsoDto currencyIsoDto);

    /**
     * Search currency with a given currency code.
     * 
     * @param currencyCode currency code
     * @return currency iso if found.
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search currency with a given currency code.  ",
			description=" Search currency with a given currency code.  ",
			operationId="    GET_CurrencyIso_ ",
			responses= {
				@ApiResponse(description=" currency iso if found. ",
						content=@Content(
									schema=@Schema(
											implementation= GetCurrencyIsoResponse.class
											)
								)
				)}
	)
    GetCurrencyIsoResponse find(@QueryParam("currencyCode") String currencyCode);

    /**
     * Remove currency with a given currency code.
     * 
     * @param currencyCode currency code
     * @return action status.
     */
    @DELETE
    @Path("/{currencyCode}")
	@Operation(
			summary=" Remove currency with a given currency code.  ",
			description=" Remove currency with a given currency code.  ",
			operationId="    DELETE_CurrencyIso_{currencyCode} ",
			responses= {
				@ApiResponse(description=" action status. ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("currencyCode") String currencyCode);

    /**
     * Modify a currency. Same input parameter as create. The operation fails if the currency is null
     * 
     * @param currencyIsoDto currency iso
     * @return action status.
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Modify a currency",
			description=" Modify a currency. Same input parameter as create. The operation fails if the currency is null  ",
			operationId="    PUT_CurrencyIso_update",
			responses= {
				@ApiResponse(description=" action status. ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(CurrencyIsoDto currencyIsoDto);

    /**
     * Creates or modify a currency base on currency code. 
     * @param currencyIsoDto currency iso to create or update
     * @return action status.
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Creates or modify a currency base on currency code.  ",
			description=" Creates or modify a currency base on currency code.  ",
			operationId="    POST_CurrencyIso_createOrUpdate ",
			responses= {
				@ApiResponse(description=" action status. ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(CurrencyIsoDto currencyIsoDto);

    /**
     * List all currencies.
     * @return list of all currency iso/
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" List all currencies. ",
			description=" List all currencies. ",
			operationId="    GET_CurrencyIso_list",
			responses= {
				@ApiResponse(description=" list of all currency iso/ ",
						content=@Content(
									schema=@Schema(
											implementation= GetCurrenciesIsoResponse.class
											)
								)
				)}
	)
    GetCurrenciesIsoResponse list();

    /**
     * List currencies ISO matching a given criteria
     *
     * @return List of currencies ISO
     */
    @GET
    @Path("/listGetAll")
	@Operation(
			summary=" List currencies ISO matching a given criteria ",
			description=" List currencies ISO matching a given criteria ",
			operationId="    GET_CurrencyIso_listGetAll",
			responses= {
				@ApiResponse(description=" List of currencies ISO ",
						content=@Content(
									schema=@Schema(
											implementation= GetCurrenciesIsoResponse.class
											)
								)
				)}
	)
    GetCurrenciesIsoResponse listGetAll();
    
}
