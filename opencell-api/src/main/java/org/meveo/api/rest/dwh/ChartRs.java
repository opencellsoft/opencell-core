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

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.dwh.BarChartDto;
import org.meveo.api.dto.dwh.ChartDto;
import org.meveo.api.dto.dwh.LineChartDto;
import org.meveo.api.dto.dwh.PieChartDto;
import org.meveo.api.dto.response.ChartsResponseDto;
import org.meveo.api.dto.response.dwh.GetChartResponse;
import org.meveo.api.rest.IBaseRs;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/chart")
@Tag(name = "Chart", description = "@%Chart")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Deprecated
@ApiOperation(value = "Obsolete. Use ChartApi instead")
public interface ChartRs extends IBaseRs {

    /**
     * Create a new chart
     * 
     * @param postData The chart's data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a new chart  ",
			description=" Create a new chart  ",
			operationId="    POST_Chart_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(ChartDto postData);

    /**
     * Create a new bar chart
     * 
     * @param postData The bar chart's data
     * @return Request processing status
     */
    @POST
    @Path("/bar")
	@Operation(
			summary=" Create a new bar chart  ",
			description=" Create a new bar chart  ",
			operationId="    POST_Chart_bar",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createBarChart(BarChartDto postData);

    /**
     * Update an existing bar chart
     * 
     * @param postData The bar chart's data
     * @return Request processing status
     */
    @PUT
    @Path("/bar")
	@Operation(
			summary=" Update an existing bar chart  ",
			description=" Update an existing bar chart  ",
			operationId="    PUT_Chart_bar",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus updateBarChart(BarChartDto postData);

    /**
     * Create a new pie chart
     * 
     * @param postData The pie chart's data
     * @return Request processing status
     */
    @POST
    @Path("/pie")
	@Operation(
			summary=" Create a new pie chart  ",
			description=" Create a new pie chart  ",
			operationId="    POST_Chart_pie",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createPieChart(PieChartDto postData);

    /**
     * Update an existing pie chart
     * 
     * @param postData The pie chart's data
     * @return Request processing status
     */
    @PUT
    @Path("/pie")
	@Operation(
			summary=" Update an existing pie chart  ",
			description=" Update an existing pie chart  ",
			operationId="    PUT_Chart_pie",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus updatePieChart(PieChartDto postData);

    /**
     * Create a new line chart
     * 
     * @param postData The line chart's data
     * @return Request processing status
     */
    @POST
    @Path("/line")
	@Operation(
			summary=" Create a new line chart  ",
			description=" Create a new line chart  ",
			operationId="    POST_Chart_line",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createLineChart(LineChartDto postData);

    /**
     * Update an existing line chart
     * 
     * @param postData The line chart's data
     * @return Request processing status
     */
    @PUT
    @Path("/line")
	@Operation(
			summary=" Update an existing line chart  ",
			description=" Update an existing line chart  ",
			operationId="    PUT_Chart_line",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus updateLineChart(LineChartDto postData);

    /**
     * Update an existing chart
     * 
     * @param postData The chart's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing chart  ",
			description=" Update an existing chart  ",
			operationId="    PUT_Chart_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(ChartDto postData);

    /**
     * Remove an existing chart with a given code
     * 
     * @param chartCode The chart's code
     * @return Request processing status
     */
    @DELETE
    @Path("/")
	@Operation(
			summary=" Remove an existing chart with a given code  ",
			description=" Remove an existing chart with a given code  ",
			operationId="    DELETE_Chart_delete",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@QueryParam("chartCode") String chartCode);

    /**
     * Find a chart with a given code
     * 
     * @param chartCode The chart's code
     * @return Get Chart Response
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Find a chart with a given code  ",
			description=" Find a chart with a given code  ",
			operationId="    GET_Chart_search",
			responses= {
				@ApiResponse(description=" Get Chart Response ",
						content=@Content(
									schema=@Schema(
											implementation= GetChartResponse.class
											)
								)
				)}
	)
    GetChartResponse find(@QueryParam("chartCode") String chartCode);

    /**
     * List Calendars matching a given criteria
     *
     * @return List of Calendars
     */
    @GET
    @Path("/listGetAll")
	@Operation(
			summary=" List Calendars matching a given criteria ",
			description=" List Calendars matching a given criteria ",
			operationId="    GET_Chart_listGetAll",
			responses= {
				@ApiResponse(description=" List of Calendars ",
						content=@Content(
									schema=@Schema(
											implementation= ChartsResponseDto.class
											)
								)
				)}
	)
    ChartsResponseDto listGetAll();

    /**
     * Create new or update an existing chart with a given code
     * 
     * @param postData The chart's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing chart with a given code  ",
			description=" Create new or update an existing chart with a given code  ",
			operationId="    POST_Chart_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(ChartDto postData);

    /**
     * Enable a Chart with a given code
     * 
     * @param code Chart code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
	@Operation(
			summary=" Enable a Chart with a given code  ",
			description=" Enable a Chart with a given code  ",
			operationId="    POST_Chart_{code}_enable",
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
     * Disable a Chart with a given code
     * 
     * @param code Chart code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
	@Operation(
			summary=" Disable a Chart with a given code  ",
			description=" Disable a Chart with a given code  ",
			operationId="    POST_Chart_{code}_disable",
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
