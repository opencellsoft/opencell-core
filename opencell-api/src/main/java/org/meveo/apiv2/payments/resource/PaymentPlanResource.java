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

package org.meveo.apiv2.payments.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.meveo.api.dto.PaymentActionStatus;
import org.meveo.apiv2.payments.PaymentPlanDto;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/payment-plan")
@Tag(name = "PaymentPlan")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public interface PaymentPlanResource {

    @POST
    @Path("/")
    @Operation(
            summary = "Create Payment plan",
            description = "Create Payment plan",
            operationId = "POST_Payment-Plan",
            responses = {
                    @ApiResponse(description = "Id of created Payment plan"
                    )}
    )
    Response create(PaymentPlanDto paymentPlanDto);

    @PUT
    @Path("/{id}")
    @Operation(
            summary = "Update Payment plan",
            description = "Update Payment plan",
            operationId = "PUT_Payment-Plan",
            responses = {
                    @ApiResponse(description = "Id of updated Payment plan"
                    )}
    )
    Response update(@Parameter(description = "Payment plan id", required = true) @PathParam("id") Long id, PaymentPlanDto paymentPlanDto);

    @DELETE
    @Path("/{id}")
    @Operation(
            summary = "Delete Payment plan by id",
            description = "Delete Payment plan by id",
            operationId = "DELETE_Payment-Plan"
    )
    Response delete(@Parameter(description = "Payment plan id", required = true) @PathParam("id") Long id);

    @PUT
    @Path("/{id}/activate")
    @Operation(
            summary = "Activate Payment plan by Id",
            description = "Activate Payment plan by Id",
            operationId = "PUT_Activate-Payment-Plan",
            responses = {
                    @ApiResponse(description = " payment action status ",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = PaymentActionStatus.class
                                    )
                            )
                    )}
    )
    Response activate(@Parameter(description = "Payment plan id", required = true) @PathParam("id") Long id);

}
