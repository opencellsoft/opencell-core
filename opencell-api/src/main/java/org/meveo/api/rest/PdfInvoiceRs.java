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
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.meveo.api.dto.response.PdfInvoiceResponse;

/**
 * @author Edward P. Legaspi
 **/
@Path("/PdfInvoice")
@Tag(name = "PdfInvoice", description = "@%PdfInvoice")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface PdfInvoiceRs extends IBaseRs {

    /**
     * Find a PDF invoice with a given invoice number and a customer account code.
     * 
     * @param invoiceNumber The invoice number
     * @param customerAccountCode The customer account's number
     * @return invoice's pdf
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Find a PDF invoice with a given invoice number and a customer account code.  ",
			description=" Find a PDF invoice with a given invoice number and a customer account code.  ",
			operationId="    GET_PdfInvoice_search",
			responses= {
				@ApiResponse(description=" invoice's pdf ",
						content=@Content(
									schema=@Schema(
											implementation= PdfInvoiceResponse.class
											)
								)
				)}
	)
    PdfInvoiceResponse getPDFInvoice(@QueryParam("invoiceNumber") String invoiceNumber, @QueryParam("customerAccountCode") String customerAccountCode);
}
