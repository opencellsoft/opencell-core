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
import org.meveo.api.dto.tunnel.ThemeDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.model.tunnel.Theme;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


/**
 * @author mohamed CHAOUKI
 */
@Path("/tunnel/theme")
@Tag(name = "Theme", description = "@%Theme")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface ThemeRs extends IBaseRs {

    /**
     * Create a new tunnel Theme
     * @param themeDto Tunnel data to be created
     * @return action status
     */
    @POST
    @Path("/")
    @Operation(
            summary=" Create tunnel theme.  ",
            tags = { "Theme" },
            description=" Create tunnel theme.  ",
            operationId="    POST_Theme_create",
            responses= {
                    @ApiResponse(
                            responseCode = "200",
                            description=" Succeed creating tunnel theme.  ",
                            content=@Content(
                                    schema=@Schema(
                                            implementation= ActionStatus.class
                                    )
                            )
                    )
            }
    )
    Theme create(@Parameter(description = "tunnel dto for a new insertion", required = true)
                                ThemeDto themeDto);


    /**
     * Update the tunnel theme
     * @param themeDto Tunnel data to be updated
     * @return action status
     */
    @PUT
    @Path("/")
    @Operation(
            summary=" Update tunnel theme.  ",
            tags = { "Theme" },
            description=" Update tunnel theme.  ",
            operationId="    PUT_Theme_update",
            responses= {
                    @ApiResponse(
                            responseCode = "200",
                            description=" Succeed updating tunnel theme.  ",
                            content=@Content(
                                    schema=@Schema(
                                            implementation= ActionStatus.class
                                    )
                            )
                    )
            }
    )
    Theme update(ThemeDto themeDto);


    /**
     * Deletes the theme with the specified code.
     * @param code Code of the theme.
     * @return Request processing status
     */
    @DELETE
    @Path("/{code}")
    @Operation(
            summary=" Deletes the theme with the specified code. ",
            description=" Deletes the theme with the specified code. ",
            operationId="    DELETE_Theme_{code}",
            responses= {
                    @ApiResponse(description=" Request processing status ",
                            content=@Content(
                                    schema=@Schema(
                                            implementation= ActionStatus.class
                                    )
                            )
                    )}
    )
    ActionStatus delete(@PathParam("code") String code);

}
