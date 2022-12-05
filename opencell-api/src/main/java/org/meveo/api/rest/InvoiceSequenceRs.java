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
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.billing.InvoiceSequenceDto;
import org.meveo.api.dto.response.GetInvoiceSequenceResponse;
import org.meveo.api.dto.response.GetInvoiceSequencesResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Web service for managing {@link org.meveo.model.billing.InvoiceSequence}.
 * 
 * @author akadid abdelmounaim
 **/
@Path("/invoiceSequence")
@Tag(name = "InvoiceSequence", description = "@%InvoiceSequence")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface InvoiceSequenceRs extends IBaseRs {

    /**
     * Create invoiceSequence.
     * 
     * @param invoiceSequenceDto invoice Sequence to be created
     * @return action status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create invoiceSequence.  ",
			description=" Create invoiceSequence.  ",
			operationId="    POST_InvoiceSequence_create",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(InvoiceSequenceDto invoiceSequenceDto);

    /**
     * Update invoiceSequence.
     * 
     * @param invoiceSequenceDto invoice Sequence to be updated
     * @return action status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update invoiceSequence.  ",
			description=" Update invoiceSequence.  ",
			operationId="    PUT_InvoiceSequence_update",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(InvoiceSequenceDto invoiceSequenceDto);

    /**
     * Search invoiceSequence with a given code.
     * 
     * @param invoiceSequenceCode invoice type's code
     * @return invoice sequence
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search invoiceSequence with a given code.  ",
			description=" Search invoiceSequence with a given code.  ",
			operationId="    GET_InvoiceSequence_search",
			responses= {
				@ApiResponse(description=" invoice sequence ",
						content=@Content(
									schema=@Schema(
											implementation= GetInvoiceSequenceResponse.class
											)
								)
				)}
	)
    GetInvoiceSequenceResponse find(@QueryParam("invoiceSequenceCode") String invoiceSequenceCode);

    /**
     * Create new or update an existing invoiceSequence with a given code.
     * 
     * @param invoiceSequenceDto The invoiceSequence's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing invoiceSequence with a given code.  ",
			description=" Create new or update an existing invoiceSequence with a given code.  ",
			operationId="    POST_InvoiceSequence_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(InvoiceSequenceDto invoiceSequenceDto);

    /**
     * List of invoiceSequence.
     * 
     * @return A list of invoiceSequence
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" List of invoiceSequence.  ",
			description=" List of invoiceSequence.  ",
			operationId="    GET_InvoiceSequence_list",
			responses= {
				@ApiResponse(description=" A list of invoiceSequence ",
						content=@Content(
									schema=@Schema(
											implementation= GetInvoiceSequencesResponse.class
											)
								)
				)}
	)
    GetInvoiceSequencesResponse list();
}
