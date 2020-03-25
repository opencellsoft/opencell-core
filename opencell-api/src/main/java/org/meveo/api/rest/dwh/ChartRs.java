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

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.dwh.BarChartDto;
import org.meveo.api.dto.dwh.ChartDto;
import org.meveo.api.dto.dwh.LineChartDto;
import org.meveo.api.dto.dwh.PieChartDto;
import org.meveo.api.dto.response.dwh.GetChartResponse;
import org.meveo.api.rest.IBaseRs;

@Path("/chart")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface ChartRs extends IBaseRs {

    /**
     * Create a new chart
     * 
     * @param postData The chart's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(ChartDto postData);

    /**
     * Create a new bar chart
     * 
     * @param postData The bar chart's data
     * @return Request processing status
     */
    @POST
    @Path("/bar")
    ActionStatus createBarChart(BarChartDto postData);

    /**
     * Update an existing bar chart
     * 
     * @param postData The bar chart's data
     * @return Request processing status
     */
    @PUT
    @Path("/bar")
    ActionStatus updateBarChart(BarChartDto postData);

    /**
     * Create a new pie chart
     * 
     * @param postData The pie chart's data
     * @return Request processing status
     */
    @POST
    @Path("/pie")
    ActionStatus createPieChart(PieChartDto postData);

    /**
     * Update an existing pie chart
     * 
     * @param postData The pie chart's data
     * @return Request processing status
     */
    @PUT
    @Path("/pie")
    ActionStatus updatePieChart(PieChartDto postData);

    /**
     * Create a new line chart
     * 
     * @param postData The line chart's data
     * @return Request processing status
     */
    @POST
    @Path("/line")
    ActionStatus createLineChart(LineChartDto postData);

    /**
     * Update an existing line chart
     * 
     * @param postData The line chart's data
     * @return Request processing status
     */
    @PUT
    @Path("/line")
    ActionStatus updateLineChart(LineChartDto postData);

    /**
     * Update an existing chart
     * 
     * @param postData The chart's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(ChartDto postData);

    /**
     * Remove an existing chart with a given code
     * 
     * @param chartCode The chart's code
     * @return Request processing status
     */
    @DELETE
    @Path("/")
    ActionStatus remove(@QueryParam("chartCode") String chartCode);

    /**
     * Find a chart with a given code
     * 
     * @param chartCode The chart's code
     * @return Get Chart Response
     */
    @GET
    @Path("/")
    GetChartResponse find(@QueryParam("chartCode") String chartCode);

    /**
     * Create new or update an existing chart with a given code
     * 
     * @param postData The chart's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(ChartDto postData);

    /**
     * Enable a Chart with a given code
     * 
     * @param code Chart code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Chart with a given code
     * 
     * @param code Chart code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    ActionStatus disable(@PathParam("code") String code);
}