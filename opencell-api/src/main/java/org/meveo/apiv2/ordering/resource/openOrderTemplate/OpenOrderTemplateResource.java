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

package org.meveo.apiv2.ordering.resource.openOrderTemplate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.models.ApiException;
import org.meveo.apiv2.ordering.resource.order.OpenOrderTemplateInput;
import org.meveo.apiv2.ordering.resource.order.Order;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

@Path("ordering/openOrderTemplates")
@Produces({ "application/json"})
@Consumes({ "application/json"})
public interface OpenOrderTemplateResource {

    @POST
    @Operation(summary = "create open order template",
            tags = { "Open Orders Templates" },
            description = "create open order template",
            responses = {
                    @ApiResponse(
                            description = "the created open order template",
                            responseCode = "200"
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid inputs supplied", content = @Content(schema = @Schema(implementation = ApiException.class)))
            })
    Response createOpenOrderTemplate(@Parameter(description = "open order template object to be created", required = true) OpenOrderTemplateInput openOrderTemplateInput);


    @PUT
    @Path("/{code}")
    @Operation(summary = "update  open order template",
            tags = { "Open Orders Templates" },
            description = "Returns the updated open order template",
            responses = {
                    @ApiResponse(
                            description = "the updated open order template",
                            responseCode = "200"
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid inputs supplied", content = @Content(schema = @Schema(implementation = ApiException.class)))
            })
    Response updateOpenOrderTemplate(@Parameter(description = "code of the open order template ", required = true) @PathParam("code") String code, @Parameter(description = "open order template object to be updated", required = true) OpenOrderTemplateInput openOrderTemplateInput);

    @PUT
    @Path("/{code}/disable")
    @Operation(summary = "disable open order template",
            tags = { "Open Orders Templates" },
            description = "disable the specified open order template",
            responses = {
                    @ApiResponse(responseCode = "400", description = "Invalid inputs supplied", content = @Content(schema = @Schema(implementation = ApiException.class)))
            })
    Response disableOpenOrderTemplate(@Parameter(description = "code of the open order template ", required = true) @PathParam("code") String code);

    @PUT
    @Path("/{code}/status/{status}")
    @Operation(summary = "change status open order template",
            tags = { "Open Orders Templates" },
            description = "change status the specified open order template",
            responses = {
                    @ApiResponse(responseCode = "400", description = "Invalid inputs supplied", content = @Content(schema = @Schema(implementation = ApiException.class)))
            })
    Response changeStatusOpenOrderTemplate(@Parameter(description = "code of the open order template ", required = true) @PathParam("code") String code,
            @Parameter(description = "status of the open order template ", required = true) @PathParam("status") String status);

}
