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

import java.util.Date;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.payment.DDRequestLotOpDto;
import org.meveo.api.dto.response.payment.DDRequestLotOpsResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.serialize.RestDateParam;
import org.meveo.model.payments.DDRequestOpStatusEnum;

@Path("/payment/ddrequestLotOp")
@Tag(name = "DDRequestLotOp", description = "@%DDRequestLotOp")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface DDRequestLotOpRs extends IBaseRs {

	/**
	 * Create a ddrequestLotOp by dto
     *
	 * @param dto DDRequestLotOp Dto
	 * @return Action status
	 */
    @POST
    @Path("/")
	@Operation(
			summary="	  Create a ddrequestLotOp by dto	  ",
			description="	  Create a ddrequestLotOp by dto	  ",
			operationId="    POST_DDRequestLotOp_create",
			responses= {
				@ApiResponse(description=" Action status	  ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(DDRequestLotOpDto dto);

    /**
     * List ddrequestLotOps by fromDueDate,toDueDate,status
     *
     * @param fromDueDate Start of search due date interval
     * @param toDueDate End of search due date interval
     * @param status DDRequestOp status
     * @return DDRequestLotOps response 
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" List ddrequestLotOps by fromDueDate,toDueDate,status ",
			description=" List ddrequestLotOps by fromDueDate,toDueDate,status ",
			operationId="    GET_DDRequestLotOp_list",
			responses= {
				@ApiResponse(description=" DDRequestLotOps response  ",
						content=@Content(
									schema=@Schema(
											implementation= DDRequestLotOpsResponseDto.class
											)
								)
				)}
	)
    DDRequestLotOpsResponseDto list(@QueryParam("fromDueDate") @RestDateParam Date fromDueDate,@QueryParam("toDueDate") @RestDateParam Date toDueDate,@QueryParam("status") DDRequestOpStatusEnum status);

}
