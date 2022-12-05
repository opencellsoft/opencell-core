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

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.SellerDto;
import org.meveo.api.dto.response.GetSellerResponse;
import org.meveo.api.dto.response.SellerCodesResponseDto;
import org.meveo.api.dto.response.SellerResponseDto;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

/**
 * Web service for managing {@link org.meveo.model.admin.Seller}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/seller")
@Tag(name = "Seller", description = "@%Seller")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface SellerRs extends IBaseRs {

    /**
     * Create seller.
     * 
     * @param postData posted data to API containing information of seller
     * @return action status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create seller.  ",
			description=" Create seller.  ",
			operationId="    POST_Seller_create",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(SellerDto postData);

    /**
     * Update seller.
     * 
     * @param postData posted data
     * @return action status.
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update seller.  ",
			description=" Update seller.  ",
			operationId="    PUT_Seller_update",
			responses= {
				@ApiResponse(description=" action status. ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(SellerDto postData);

    /**
     * Search for seller with a given code.
     * 
     * @param sellerCode seller code
     * @param inheritCF Should inherited custom fields be retrieved. Defaults to INHERIT_NO_MERGE.
     * @return found seller.
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search for seller with a given code.  ",
			description=" Search for seller with a given code.  ",
			operationId="    GET_Seller_search",
			responses= {
				@ApiResponse(description=" found seller. ",
						content=@Content(
									schema=@Schema(
											implementation= GetSellerResponse.class
											)
								)
				)}
	)
    GetSellerResponse find(@QueryParam("sellerCode") String sellerCode, @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF);

    /**
     * Remove seller with a given code.
     * 
     * @param sellerCode code of seller
     * @return action status.
     */
    @DELETE
    @Path("/{sellerCode}")
	@Operation(
			summary=" Remove seller with a given code.  ",
			description=" Remove seller with a given code.  ",
			operationId="    DELETE_Seller_{sellerCode}",
			responses= {
				@ApiResponse(description=" action status. ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("sellerCode") String sellerCode);

    /**
     * Search for seller with a given code.
     * 
     * @return list of seller
     */
    @GET
    @Path("/list")
	@Operation(
			summary="List all sellers.",
			description="List all sellers.",
			operationId="    GET_Seller_list",
			responses= {
				@ApiResponse(description=" list of sellers ",
						content=@Content(
									schema=@Schema(
											implementation= SellerResponseDto.class
											)
								)
				)}
	)
    SellerResponseDto list();

    /**
     * Search for all seller's code.
     *
     * @return list of seller's code.
     */
    @GET
    @Path("/listSellerCodes")
	@Operation(
			summary=" Search for all seller's code. ",
			description=" Search for all seller's code. ",
			operationId="    GET_Seller_listSellerCodes",
			responses= {
				@ApiResponse(description=" list of seller's code. ",
						content=@Content(
									schema=@Schema(
											implementation= SellerCodesResponseDto.class
											)
								)
				)}
	)
    SellerCodesResponseDto listSellerCodes();

    /**
     * Create or update a seller.
     *
     * @param postData posted data
     * @return created or updated seller.
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create or update a seller. ",
			description=" Create or update a seller. ",
			operationId="    POST_Seller_createOrUpdate",
			responses= {
				@ApiResponse(description=" created or updated seller. ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(SellerDto postData);

}
