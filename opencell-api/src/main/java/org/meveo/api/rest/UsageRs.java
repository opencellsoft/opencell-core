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

import java.util.Date;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.meveo.api.dto.usage.UsageChargeAggregateResponseDto;
import org.meveo.api.dto.usage.UsageResponseDto;
import org.meveo.api.serialize.RestDateParam;


@Path("/usage")
@Tag(name = "Usage", description = "@%Usage")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface UsageRs extends IBaseRs {



    /**
     * Search for all opened ratedTransactions with a given userAccountCode,fromDate and toDate .
     * 
     * @param userAccountCode user account's code
     * @param fromDate from date
     * @param toDate to date
     * @return usage
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search for all opened ratedTransactions with a given userAccountCode,fromDate and toDate .  ",
			description=" Search for all opened ratedTransactions with a given userAccountCode,fromDate and toDate .  ",
			operationId="    GET_Usage_search",
			responses= {
				@ApiResponse(description=" usage ",
						content=@Content(
									schema=@Schema(
											implementation= UsageResponseDto.class
											)
								)
				)}
	)
    UsageResponseDto find(@QueryParam("userAccountCode") String userAccountCode, @QueryParam("fromDate") @RestDateParam Date fromDate,
            @QueryParam("toDate") @RestDateParam Date toDate);

    /**
     * Search for charge aggregate isage from a user account code during a period of time.
     *
     * @param userAccountCode user account's code
     * @param fromDate from date
     * @param toDate to date
     * @return usage charge aggregate
     */
    @GET
    @Path("/chargeAggregate")
	@Operation(
			summary=" Search for charge aggregate isage from a user account code during a period of time. ",
			description=" Search for charge aggregate isage from a user account code during a period of time. ",
			operationId="    GET_Usage_chargeAggregate",
			responses= {
				@ApiResponse(description=" usage charge aggregate ",
						content=@Content(
									schema=@Schema(
											implementation= UsageChargeAggregateResponseDto.class
											)
								)
				)}
	)
    UsageChargeAggregateResponseDto chargeAggregate(@QueryParam("userAccountCode") String userAccountCode, @QueryParam("fromDate") @RestDateParam Date fromDate,
            @QueryParam("toDate") @RestDateParam Date toDate);

}
