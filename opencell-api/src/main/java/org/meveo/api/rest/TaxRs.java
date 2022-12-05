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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.TaxDto;
import org.meveo.api.dto.response.GetTaxResponse;
import org.meveo.api.dto.response.GetTaxesResponse;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * Web service for managing {@link org.meveo.model.billing.Tax}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/tax")
@Tag(name = "Tax", description = "@%Tax")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface TaxRs extends IBaseRs {

    /**
     * Create tax. Description per language can be defined
     * 
     * @param postData tax to be created
     * @return action status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create tax. Description per language can be defined  ",
			description=" Create tax. Description per language can be defined  ",
			operationId="    POST_Tax_create",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(TaxDto postData);

    /**
     * Update tax. Description per language can be defined
     * 
     * @param postData tax to be updated
     * @return action status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update tax. Description per language can be defined  ",
			description=" Update tax. Description per language can be defined  ",
			operationId="    PUT_Tax_update",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(TaxDto postData);

    /**
     * Search tax with a given code.
     * 
     * @param taxCode tax's
     * @return tax if exists
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search tax with a given code.  ",
			description=" Search tax with a given code.  ",
			operationId="    GET_Tax_search",
			responses= {
				@ApiResponse(description=" tax if exists ",
						content=@Content(
									schema=@Schema(
											implementation= GetTaxResponse.class
											)
								)
				)}
	)
    GetTaxResponse find(@QueryParam("taxCode") String taxCode);

    /**
     * Remove tax with a given code.
     * 
     * @param taxCode tax's code
     * @return action status
     */
    @DELETE
    @Path("/{taxCode}")
	@Operation(
			summary=" Remove tax with a given code.  ",
			description=" Remove tax with a given code.  ",
			operationId="    DELETE_Tax_{taxCode}",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("taxCode") String taxCode);

    /**
     * Create or uptadate a tax. 
     *
     * @param postData tax to be created or updated
     * @return action status
     */
    @POST 
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create or uptadate a tax.  ",
			description=" Create or uptadate a tax.  ",
			operationId="    POST _Tax_createOrUpdate",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(TaxDto postData);

    /**
     * Search for the list of taxes.
     *
     * @return list of all taxes.
     */
    @GET 
    @Path("/list")
	@Operation(
			summary=" Search for the list of taxes. ",
			description=" Search for the list of taxes. ",
			operationId="    GET _Tax_list",
			responses= {
				@ApiResponse(description=" list of all taxes. ",
						content=@Content(
									schema=@Schema(
											implementation= GetTaxesResponse.class
											)
								)
				)}
	)
    GetTaxesResponse list();

    /**
     * List taxes matching a given criteria
     *
     * @return List of taxes
     */
    @GET
    @Path("/listGetAll")
	@Operation(
			summary=" List taxes matching a given criteria ",
			description=" List taxes matching a given criteria ",
			operationId="    GET_Tax_listGetAll",
			responses= {
				@ApiResponse(description=" List of taxes ",
						content=@Content(
									schema=@Schema(
											implementation= GetTaxesResponse.class
											)
								)
				)}
	)
    GetTaxesResponse listGetAll();
}
