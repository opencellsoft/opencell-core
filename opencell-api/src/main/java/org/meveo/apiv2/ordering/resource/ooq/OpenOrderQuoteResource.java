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

package org.meveo.apiv2.ordering.resource.ooq;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.model.ordering.OpenOrderQuoteStatusEnum;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("ordering/open-order-quote")
@Produces({"application/json"})
@Consumes({"application/json"})
public interface OpenOrderQuoteResource {

    @POST
    @Path("/")
    @Operation(
            summary = "Create Open Order Quote",
            description = "Create Open Order Quote",
            operationId = "POST_Open-Order-Quote",
            responses = {
                    @ApiResponse(description = "Id of created Open Order Quote"
                    )}
    )
    Response create(OpenOrderQuoteDto dto);

    @POST
    @Path("/{id}/duplicate")
    @Operation(
            summary = "Duplicate Open Order Quote from existing one",
            description = "Duplicate Open Order Quote",
            operationId = "POST_DUPLICATE-Open-Order-Quote",
            responses = {
                    @ApiResponse(description = "Id of created Open Order Quote"
                    )}
    )
    Response duplicate(@Parameter(description = "Source Open Order Quote id", required = true) @PathParam("id") Long iqOOQ);

    @PUT
    @Path("/{id}")
    @Operation(
            summary = "Update Open Order Quote",
            description = "Update Open Order Quote",
            operationId = "PUT_Open-Order-Quote",
            responses = {
                    @ApiResponse(description = "Id of updated Open Order Quote"
                    )}
    )
    Response update(@Parameter(description = "Open Order Quote id", required = true) @PathParam("id") Long id, OpenOrderQuoteDto dto);

    @PUT
    @Path("/{code}/status/{status}")
    @Operation(summary = "update  open order quote status", tags = {"Open Orders Quote"})
    Response changeStatus(@Parameter(description = "code of the open order template ", required = true)
                          @PathParam("code") String code,
                          @Parameter(description = "open order template object to be updated", required = true)
                          @PathParam("status") OpenOrderQuoteStatusEnum status);


}
