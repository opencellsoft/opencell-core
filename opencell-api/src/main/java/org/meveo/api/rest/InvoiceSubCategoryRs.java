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
import org.meveo.api.dto.InvoiceSubCategoryDto;
import org.meveo.api.dto.response.GetInvoiceSubCategoryResponse;
import org.meveo.api.dto.response.InvoiceSubCategoryResponseDto;
import org.meveo.api.dto.response.PagingAndFiltering;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * Web service for managing {@link org.meveo.model.billing.InvoiceSubCategory}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/invoiceSubCategory")
@Tag(name = "InvoiceSubCategory", description = "@%InvoiceSubCategory")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface InvoiceSubCategoryRs extends IBaseRs {

    /**
     * Search for list of invoiceSubCategories.
     *
     * @return list of invoiceSubCategories
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" Search for list of invoiceSubCategories. ",
			description=" Search for list of invoiceSubCategories. ",
			operationId="    GET_InvoiceSubCategory_list",
			responses= {
				@ApiResponse(description=" list of invoiceSubCategories ",
						content=@Content(
									schema=@Schema(
											implementation= InvoiceSubCategoryResponseDto.class
											)
								)
				)}
	)
    InvoiceSubCategoryResponseDto list();

    /**
     * Create invoice sub category.
     * 
     * @param postData invoice sub category to be created
     * @return action status.
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create invoice sub category.  ",
			description=" Create invoice sub category.  ",
			operationId="    POST_InvoiceSubCategory_create",
			responses= {
				@ApiResponse(description=" action status. ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(InvoiceSubCategoryDto postData);

    /**
     * Update invoice sub category.
     * 
     * @param postData invoice sub category to be created
     * @return action status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update invoice sub category.  ",
			description=" Update invoice sub category.  ",
			operationId="    PUT_InvoiceSubCategory_update",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(InvoiceSubCategoryDto postData);

    /**
     * Create or update invoice sub category.
     * 
     * @param postData invoice sub category
     * @return action status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create or update invoice sub category.  ",
			description=" Create or update invoice sub category.  ",
			operationId="    POST_InvoiceSubCategory_createOrUpdate",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(InvoiceSubCategoryDto postData);

    /**
     * Search for invoice sub category with a given code.
     * 
     * @param invoiceSubCategoryCode invoice sub category code
     * @return invoice sub category
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search for invoice sub category with a given code.  ",
			description=" Search for invoice sub category with a given code.  ",
			operationId="    GET_InvoiceSubCategory_search",
			responses= {
				@ApiResponse(description=" invoice sub category ",
						content=@Content(
									schema=@Schema(
											implementation= GetInvoiceSubCategoryResponse.class
											)
								)
				)}
	)
    GetInvoiceSubCategoryResponse find(@QueryParam("invoiceSubCategoryCode") String invoiceSubCategoryCode);

    /**
     * Remove invoice sub category with a given code.
     * 
     * @param invoiceSubCategoryCode invoice sub category
     * @return action status
     */
    @DELETE
    @Path("/{invoiceSubCategoryCode}")
	@Operation(
			summary=" Remove invoice sub category with a given code.  ",
			description=" Remove invoice sub category with a given code.  ",
			operationId="    DELETE_InvoiceSubCategory_{invoiceSubCategoryCode}",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("invoiceSubCategoryCode") String invoiceSubCategoryCode);
    
    /**
     * List InvoiceSubCategory matching a given criteria
     *
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of InvoiceSubCategory
     */
    @POST
    @Path("/list")
	@Operation(
			summary=" List InvoiceSubCategory matching a given criteria ",
			description=" List InvoiceSubCategory matching a given criteria ",
			operationId="    POST_InvoiceSubCategory_list",
			responses= {
				@ApiResponse(description=" List of InvoiceSubCategory ",
						content=@Content(
									schema=@Schema(
											implementation= InvoiceSubCategoryResponseDto.class
											)
								)
				)}
	)
    InvoiceSubCategoryResponseDto listPost(PagingAndFiltering pagingAndFiltering);

}
