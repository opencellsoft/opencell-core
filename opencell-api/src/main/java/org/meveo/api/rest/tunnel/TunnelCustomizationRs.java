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

package org.meveo.api.rest.tunnel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.tunnel.TunnelCustomizationDto;
import org.meveo.api.exception.*;
import org.meveo.api.rest.IBaseRs;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


/**
 * @author Ilham CHAFIK
 */
@Path("/tunnel/customization")
@Tag(name = "TunnelCustomization", description = "@%TunnelCustomization")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface TunnelCustomizationRs extends IBaseRs {

    /**
     * Create a new tunnel Customization
     * @param tunnelCustomizationDto Tunnel data to be created
     * @return action status
     */
    @POST
    @Path("/")
    @Operation(
            summary=" Create tunnel customization.  ",
            tags = { "TunnelCustomization" },
            description=" Create tunnel customization.  ",
            operationId="    POST_TunnelCustomization_create",
            responses= {
                    @ApiResponse(
                            responseCode = "200",
                            description=" Succeed creating tunnel customization.  ",
                            content=@Content(
                                    schema=@Schema(
                                            implementation= ActionStatus.class
                                    )
                            )
                    )
            }
    )
    ActionStatus create(@Parameter(description = "tunnel dto for a new insertion", required = true)
                                  TunnelCustomizationDto tunnelCustomizationDto);


    /**
     * Update the tunnel customization
     * @param tunnelCustomizationDto Tunnel data to be updated
     * @return action status
     */
    @PUT
    @Path("/")
    @Operation(
            summary=" Update tunnel customization.  ",
            tags = { "TunnelCustomization" },
            description=" Update tunnel customization.  ",
            operationId="    PUT_TunnelCustomization_update",
            responses= {
                    @ApiResponse(
                            responseCode = "200",
                            description=" Succeed updating tunnel customization.  ",
                            content=@Content(
                                    schema=@Schema(
                                            implementation= ActionStatus.class
                                    )
                            )
                    )
            }
    )
    ActionStatus update(TunnelCustomizationDto tunnelCustomizationDto);


}
