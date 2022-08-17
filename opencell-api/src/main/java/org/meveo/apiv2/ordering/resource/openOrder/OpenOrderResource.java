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

package org.meveo.apiv2.ordering.resource.openOrder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.models.ApiException;
import org.meveo.apiv2.ordering.resource.oo.OpenOrderDto;
import org.meveo.apiv2.ordering.resource.order.OpenOrderTemplateInput;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("ordering/openOrder")
@Produces({ "application/json"})
@Consumes({ "application/json"})
public interface OpenOrderResource {


    @PUT
    @Path("/{code}")
    @Operation(summary = "update  open order",
            tags = { "Open Orders" },
            description = "Returns the updated open order",
            responses = {
                    @ApiResponse(
                            description = "the updated open order",
                            responseCode = "200"
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid inputs supplied", content = @Content(schema = @Schema(implementation = ApiException.class)))
            })
    Response updateOpenOrder(@Parameter(description = "code of the open order ", required = true) @PathParam("code") String code,
            @Parameter(description = "open order object to be updated", required = true) OpenOrderDto OpenOrderDto);

    @POST
    @Path("/{code}/cancel")
    @Operation(summary = "cancel open order",
            tags = { "Open Orders" },
            description = "cancel the specified open order",
            responses = {
                    @ApiResponse(responseCode = "400", description = "Invalid inputs supplied", content = @Content(schema = @Schema(implementation = ApiException.class)))
            })
    Response cancelOpenOrderTemplate(@Parameter(description = "code of the open order template ", required = true) @PathParam("code") String code, @Parameter(description = "open order object to be updated", required = true) OpenOrderDto OpenOrderDto);


}
