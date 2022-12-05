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

package org.meveo.api.rest.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.account.CreditCategoryDto;
import org.meveo.api.dto.response.payment.CreditCategoriesResponseDto;
import org.meveo.api.dto.response.payment.CreditCategoryResponseDto;
import org.meveo.api.rest.IBaseRs;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * @author Edward P. Legaspi
 * @since 22 Aug 2017
 */
@Path("/payment/creditCategory")
@Tag(name = "CreditCategory", description = "@%CreditCategory")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface CreditCategoryRs extends IBaseRs {

    /**
     * Create a new credit category 
     *
     * @param postData The credit payment category data 
     * @return Request processing status
     */
	@POST
	@Path("/")
	@Operation(
			summary=" Create a new credit category  ",
			description=" Create a new credit category  ",
			operationId="POST_CreditCategory_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
	ActionStatus create(CreditCategoryDto postData);

    /**
     * Update a credit category payment 
     *
     * @param postData The credit payment category data 
     * @return Request processing status
     */
	@PUT
	@Path("/")
	@Operation(
			summary=" Update a credit category payment  ",
			description=" Update a credit category payment  ",
			operationId="PUT_CreditCategory_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
	ActionStatus update(CreditCategoryDto postData);

    /**
     * Create or update a credit category payment 
     *
     * @param postData The credit payment category data 
     * @return Request processing status
     */
	@POST
	@Path("/createOrUpdate")
	@Operation(
			summary=" Create or update a credit category payment  ",
			description=" Create or update a credit category payment  ",
			operationId="POST_CreditCategory_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
	ActionStatus createOrUpdate(CreditCategoryDto postData);

    /**
     * Get a credit category payment with a credit category code
     *
     * @param creditCategoryCode The creditCategory code 
     * @return Credit Category Response data
     */
	@GET
	@Path("/")
	@Operation(
			summary=" Get a credit category payment with a credit category code ",
			description=" Get a credit category payment with a credit category code ",
			operationId="GET_CreditCategory_search",
			responses= {
				@ApiResponse(description=" Credit Category Response data ",
						content=@Content(
									schema=@Schema(
											implementation= CreditCategoryResponseDto.class
											)
								)
				)}
	)
	CreditCategoryResponseDto find(@QueryParam("creditCategoryCode") String creditCategoryCode);

    /**
     * Retrieve the list of credit category paiement 
     *
     * @param postData The contact data 
     * @return List of Credit Categories
     */
	@GET
	@Path("/list")
	@Operation(
			summary=" Retrieve the list of credit category paiement  ",
			description=" Retrieve the list of credit category paiement  ",
			operationId="GET_CreditCategory_list",
			responses= {
				@ApiResponse(description=" List of Credit Categories ",
						content=@Content(
									schema=@Schema(
											implementation= CreditCategoriesResponseDto.class
											)
								)
				)}
	)
	CreditCategoriesResponseDto list();

	/**
	 * List creditCategories matching a given criteria
	 *
	 * @return List of creditCategories
	 */
	@GET
	@Path("/listGetAll")
	@Operation(
			summary="	  List creditCategories matching a given criteria	 	  ",
			description="	  List creditCategories matching a given criteria	 	  ",
			operationId="GET_CreditCategory_listGetAll",
			responses= {
				@ApiResponse(description=" List of creditCategories	  ",
						content=@Content(
									schema=@Schema(
											implementation= CreditCategoriesResponseDto.class
											)
								)
				)}
	)
	CreditCategoriesResponseDto listGetAll();

    /**
     * Delete a credit category with his given code 
     *
     * @param creditCategoryCode The creditCategory code 
     * @return Request processing status
     */
	@DELETE
	@Path("/{creditCategoryCode}")
	@Operation(
			summary=" Delete a credit category with his given code  ",
			description=" Delete a credit category with his given code  ",
			operationId="DELETE_CreditCategory_{creditCategoryCode}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
	ActionStatus remove(@PathParam("creditCategoryCode") String creditCategoryCode);

}
