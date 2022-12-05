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
import org.meveo.api.dto.InvoiceCategoryDto;
import org.meveo.api.dto.response.GetInvoiceCategoryResponse;
import org.meveo.api.dto.response.InvoiceCategoryResponseDto;
import org.meveo.api.dto.response.PagingAndFiltering;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * Web service for managing {@link org.meveo.model.billing.InvoiceCategory}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/invoiceCategory")
@Tag(name = "InvoiceCategory", description = "@%InvoiceCategory")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface InvoiceCategoryRs extends IBaseRs {

    /**
     * Search for list of invoice categories.
     *
     * @return list of invoice categories
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" Search for list of invoice categories. ",
			description=" Search for list of invoice categories. ",
			operationId="    GET_InvoiceCategory_list",
			responses= {
				@ApiResponse(description=" list of invoice categories ",
						content=@Content(
									schema=@Schema(
											implementation= InvoiceCategoryResponseDto.class
											)
								)
				)}
	)
    InvoiceCategoryResponseDto list();

    /**
     * Create invoice category. Description per language can be defined
     * 
     * @param postData invoice category to be created
     * @return action status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create invoice category. Description per language can be defined  ",
			description=" Create invoice category. Description per language can be defined  ",
			operationId="    POST_InvoiceCategory_create",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(InvoiceCategoryDto postData);

    /**
     * Update invoice category.
     * 
     * @param postData invoice category to be updated
     * @return action status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update invoice category.  ",
			description=" Update invoice category.  ",
			operationId="    PUT_InvoiceCategory_update",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(InvoiceCategoryDto postData);

    /**
     * Search invoice with a given code.
     * 
     * @param invoiceCategoryCode invoice category code
     * @return invoice category
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search invoice with a given code.  ",
			description=" Search invoice with a given code.  ",
			operationId="    GET_InvoiceCategory_search",
			responses= {
				@ApiResponse(description=" invoice category ",
						content=@Content(
									schema=@Schema(
											implementation= GetInvoiceCategoryResponse.class
											)
								)
				)}
	)
    GetInvoiceCategoryResponse find(@QueryParam("invoiceCategoryCode") String invoiceCategoryCode);

    /**
     * Remove invoice with a given code.
     * 
     * @param invoiceCategoryCode invoice category code
     * @return action status
     */
    @DELETE
    @Path("/{invoiceCategoryCode}")
	@Operation(
			summary=" Remove invoice with a given code.  ",
			description=" Remove invoice with a given code.  ",
			operationId="    DELETE_InvoiceCategory_{invoiceCategoryCode}",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("invoiceCategoryCode") String invoiceCategoryCode);

    /**
     * Create or update invoice with a given code.
     * 
     * @param postData invoice category
     * @return action status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create or update invoice with a given code.  ",
			description=" Create or update invoice with a given code.  ",
			operationId="    POST_InvoiceCategory_createOrUpdate",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(InvoiceCategoryDto postData);
    
    /**
     * List InvoiceCategory matching a given criteria
     *
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of InvoiceCategory
     */
    @POST
    @Path("/list")
	@Operation(
			summary=" List InvoiceCategory matching a given criteria ",
			description=" List InvoiceCategory matching a given criteria ",
			operationId="    POST_InvoiceCategory_list",
			responses= {
				@ApiResponse(description=" List of InvoiceCategory ",
						content=@Content(
									schema=@Schema(
											implementation= InvoiceCategoryResponseDto.class
											)
								)
				)}
	)
    InvoiceCategoryResponseDto listPost(PagingAndFiltering pagingAndFiltering);

}
