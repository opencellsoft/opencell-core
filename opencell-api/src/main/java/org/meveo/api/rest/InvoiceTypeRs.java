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

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.billing.InvoiceTypeDto;
import org.meveo.api.dto.response.GetInvoiceTypeResponse;
import org.meveo.api.dto.response.GetInvoiceTypesResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Web service for managing {@link org.meveo.model.billing.InvoiceType}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/invoiceType")
@Tag(name = "InvoiceType", description = "@%InvoiceType")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface InvoiceTypeRs extends IBaseRs {

    /**
     * Create invoiceType. Description per language can be defined
     * 
     * @param invoiceTypeDto invoice type to be created
     * @return action status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create invoiceType. Description per language can be defined  ",
			description=" Create invoiceType. Description per language can be defined  ",
			operationId="    POST_InvoiceType_create",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(InvoiceTypeDto invoiceTypeDto);

    /**
     * Update invoiceType. Description per language can be defined
     * 
     * @param invoiceTypeDto invoice type to be updated
     * @return action status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update invoiceType. Description per language can be defined  ",
			description=" Update invoiceType. Description per language can be defined  ",
			operationId="    PUT_InvoiceType_update",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(InvoiceTypeDto invoiceTypeDto);

    /**
     * Search invoiceType with a given code.
     * 
     * @param invoiceTypeCode invoice type's code
     * @return invoice type
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search invoiceType with a given code.  ",
			description=" Search invoiceType with a given code.  ",
			operationId="    GET_InvoiceType_search",
			responses= {
				@ApiResponse(description=" invoice type ",
						content=@Content(
									schema=@Schema(
											implementation= GetInvoiceTypeResponse.class
											)
								)
				)}
	)
    GetInvoiceTypeResponse find(@QueryParam("invoiceTypeCode") String invoiceTypeCode);

    /**
     * Remove invoiceType with a given code.
     * 
     * @param invoiceTypeCode invoice type's code
     * @return action status
     */
    @DELETE
    @Path("/{invoiceTypeCode}")
	@Operation(
			summary=" Remove invoiceType with a given code.  ",
			description=" Remove invoiceType with a given code.  ",
			operationId="    DELETE_InvoiceType_{invoiceTypeCode}",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("invoiceTypeCode") String invoiceTypeCode);

    /**
     * Create new or update an existing invoiceType with a given code.
     * 
     * @param invoiceTypeDto The invoiceType's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing invoiceType with a given code.  ",
			description=" Create new or update an existing invoiceType with a given code.  ",
			operationId="    POST_InvoiceType_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(InvoiceTypeDto invoiceTypeDto);

    /**
     * List of invoiceType.
     * 
     * @return A list of invoiceType
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" List of invoiceType.  ",
			description=" List of invoiceType.  ",
			operationId="    GET_InvoiceType_list",
			responses= {
				@ApiResponse(description=" A list of invoiceType ",
						content=@Content(
									schema=@Schema(
											implementation= GetInvoiceTypesResponse.class
											)
								)
				)}
	)
    GetInvoiceTypesResponse list();
}
