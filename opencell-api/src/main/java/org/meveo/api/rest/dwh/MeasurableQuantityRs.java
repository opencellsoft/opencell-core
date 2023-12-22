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

package org.meveo.api.rest.dwh;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.dwh.GetListMeasurableQuantityResponse;
import org.meveo.api.dto.dwh.GetMeasurableQuantityResponse;
import org.meveo.api.dto.dwh.MeasurableQuantityDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.serialize.RestDateParam;
import org.meveo.model.dwh.MeasurementPeriodEnum;

@Path("/measurableQuantity")
@Tag(name = "MeasurableQuantity", description = "@%MeasurableQuantity")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Deprecated
public interface MeasurableQuantityRs extends IBaseRs {

    /**
     * Create a Measurable quantity.
     *
     * @param postData posted data to API
     * @return action status.
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a Measurable quantity. ",
			description=" Create a Measurable quantity. ",
			operationId="    POST_MeasurableQuantity_create",
			responses= {
				@ApiResponse(description=" action status. ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(MeasurableQuantityDto postData);

    /**
     * Update Measurable quantity from mesearable quantities.
     * 
     * @param postData posted data.
     * @return actions status.
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update Measurable quantity from mesearable quantities.  ",
			description=" Update Measurable quantity from mesearable quantities.  ",
			operationId="    PUT_MeasurableQuantity_update",
			responses= {
				@ApiResponse(description=" actions status. ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(MeasurableQuantityDto postData);

    /**
     * Get Measurable quantity from a given code.
     * 
     * @param code Measureable quantity's code
     * @return Measurable Quantity Response data
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Get Measurable quantity from a given code.  ",
			description=" Get Measurable quantity from a given code.  ",
			operationId="    GET_MeasurableQuantity_search",
			responses= {
				@ApiResponse(description=" Measurable Quantity Response data ",
						content=@Content(
									schema=@Schema(
											implementation= GetMeasurableQuantityResponse.class
											)
								)
				)}
	)
    GetMeasurableQuantityResponse find(@QueryParam("code") String code);

    /**
     * Find a Measurable value during a period of date and period
     *
     * @param code code of mesurable value.
     * @param fromDate format yyyy-MM-dd'T'HH:mm:ss or yyyy-MM-dd
     * @param toDate format yyyy-MM-dd'T'HH:mm:ss or yyyy-MM-dd
     * @param period period in which mesurable value is calculated.
     * @param mqCode Measureable quantity's code
     * @return mesurable value by date and period.
     */
    @GET
    @Path("/findMVByDateAndPeriod")
	@Operation(
			summary=" Find a Measurable value during a period of date and period ",
			description=" Find a Measurable value during a period of date and period ",
			operationId="    GET_MeasurableQuantity_findMVByDateAndPeriod",
			responses= {
				@ApiResponse(description=" mesurable value by date and period. ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    Response findMVByDateAndPeriod(@QueryParam("code") String code, @QueryParam("fromDate") @RestDateParam Date fromDate, @QueryParam("toDate") @RestDateParam Date toDate,
            @QueryParam("period") MeasurementPeriodEnum period, @QueryParam("mqCode") String mqCode);

    /**
     * Remove Measurable quantity with a given code.
     * 
     * @param code Measurable quantity's code
     * @return action status.
     */
    @DELETE
    @Path("/{code}")
	@Operation(
			summary=" Remove Measurable quantity with a given code.  ",
			description=" Remove Measurable quantity with a given code.  ",
			operationId="    DELETE_MeasurableQuantity_{code}",
			responses= {
				@ApiResponse(description=" action status. ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("code") String code);

    /**
     * List Measurable quantity with a given code.
     * 
     * @return A list of measurable quantities
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" List Measurable quantity with a given code.  ",
			description=" List Measurable quantity with a given code.  ",
			operationId="    GET_MeasurableQuantity_list",
			responses= {
				@ApiResponse(description=" A list of measurable quantities ",
						content=@Content(
									schema=@Schema(
											implementation= GetListMeasurableQuantityResponse.class
											)
								)
				)}
	)
    GetListMeasurableQuantityResponse list();

    /**
     * Enable a Measurable quantity with a given code
     * 
     * @param code Measurable quantity code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
	@Operation(
			summary=" Enable a Measurable quantity with a given code  ",
			description=" Enable a Measurable quantity with a given code  ",
			operationId="    POST_MeasurableQuantity_{code}_enable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Measurable quantity with a given code
     * 
     * @param code Measurable quantity code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
	@Operation(
			summary=" Disable a Measurable quantity with a given code  ",
			description=" Disable a Measurable quantity with a given code  ",
			operationId="    POST_MeasurableQuantity_{code}_disable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus disable(@PathParam("code") String code);

}
