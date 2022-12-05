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

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ConfigurationDto;
import org.meveo.api.dto.PropertiesDto;
import org.meveo.api.dto.response.GetConfigurationResponse;

/**
 * Manages system configuration.
 * 
 * @author Edward P. Legaspi
 * @author Khalid HORRI
 * @lastModifiedVersion 7.1
 */
@Path("/configurations")
@Tag(name = "Configuration", description = "@%Configuration")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface ConfigurationRs extends IBaseRs {

    /**
     * Converts system properties into json string.
     * 
     * @return system properties
     */
    @GET
    @Path("/properties")
	@Operation(
			summary=" Converts system properties into json string.  ",
			description=" Converts system properties into json string.  ",
			operationId="    GET_Configuration_properties",
			responses= {
				@ApiResponse(description=" system properties ",
						content=@Content(
									schema=@Schema(
											implementation= GetConfigurationResponse.class
											)
								)
				)}
	)
    GetConfigurationResponse getSystemProperties();

    /**
     * set configuration property
     * @param configuration
     * @return
     */
    @POST
    @Path("/")
	@Operation(
			summary=" set configuration property ",
			description=" set configuration property ",
			operationId="    POST_Configuration_create",
			responses= {
				@ApiResponse(description="ActionStatus response",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus setConfigurationProperty(ConfigurationDto configuration);

    /**
     * set configuration property
     * 
     * @param properties
     * @return
     */
    @POST
    @Path("/properties")
	@Operation(
			summary=" set configuration property  ",
			description=" set configuration property  ",
			operationId="    POST_Configuration_properties",
			responses= {
				@ApiResponse(description="ActionStatus response",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus setConfigurationProperty(PropertiesDto properties);
}
