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

package org.meveo.api.rest.metrics.configuration;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.metrics.configuration.MetricsConfigurationDto;
import org.meveo.api.dto.response.GetMetricsConfigurationResponse;
import org.meveo.api.rest.IBaseRs;
import org.meveo.model.metrics.configuration.MetricsConfiguration;

/**
 * Web service for managing {@link MetricsConfiguration}.
 *
 * @author mohamed STITANE
 **/
@Path("/metrics/config")
@Tag(name = "MetricsConfiguration", description = "@%MetricsConfiguration")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface MetricsConfigurationRs extends IBaseRs {
    /**
     * Creates a MetricConfiguration.
     *
     * If the MetricConfiguration code does not exists, a metrics configuration record is created
     *
     * @param metricsConfigurationDto a metrics configuration dto.
     * @return {@link org.meveo.api.dto.ActionStatus}.
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Creates a MetricConfiguration. If the MetricConfiguration code does not exists, a metrics configuration record is created ",
			description=" Creates a MetricConfiguration. If the MetricConfiguration code does not exists, a metrics configuration record is created ",
			operationId="    POST_MetricsConfiguration_create",
			responses= {
				@ApiResponse(description="link org.meveo.api.dto.ActionStatus}. ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(MetricsConfigurationDto metricsConfigurationDto);

    /**
     * Search metrics configuration with a given code.
     *
     * @param code MetricConfiguration code
     * @return {@link GetMetricsConfigurationResponse}.
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search metrics configuration with a given code. ",
			description=" Search metrics configuration with a given code. ",
			operationId="    GET_MetricsConfiguration_search",
			responses= {
				@ApiResponse(description="link GetMetricsConfigurationResponse}. ",
						content=@Content(
									schema=@Schema(
											implementation= GetMetricsConfigurationResponse.class
											)
								)
				)}
	)
    GetMetricsConfigurationResponse find(@QueryParam("code") String code);

    /**
     * Update an existing MetricConfiguration.
     *
     * If the MetricsConfiguration code exists, a metrics configuration record is updated
     *
     * @param metricsConfigurationDto a metrics configuration dto.
     * @return {@link org.meveo.api.dto.ActionStatus}.
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing MetricConfiguration. If the MetricsConfiguration code exists, a metrics configuration record is updated ",
			description=" Update an existing MetricConfiguration. If the MetricsConfiguration code exists, a metrics configuration record is updated ",
			operationId="    PUT_MetricsConfiguration_update",
			responses= {
				@ApiResponse(description="link org.meveo.api.dto.ActionStatus}. ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(MetricsConfigurationDto metricsConfigurationDto);

    /**
     * Delete metrics configuration with a given code.
     *
     * @param code MetricConfiguration code
     * @return {@link org.meveo.api.dto.ActionStatus}.
     */
    @DELETE
    @Path("/")
	@Operation(
			summary=" Delete metrics configuration with a given code. ",
			description=" Delete metrics configuration with a given code. ",
			operationId="    DELETE_MetricsConfiguration_delete",
			responses= {
				@ApiResponse(description="link org.meveo.api.dto.ActionStatus}. ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@QueryParam("code") String code);
}
