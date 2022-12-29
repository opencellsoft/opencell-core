package org.meveo.api.rest.tunnel;/*
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


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.tunnel.HypertextSectionDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.model.tunnel.HypertextSection;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author Ilham CHAFIK
 */
@Path("/tunnel/section")
@Tag(name = "HypertextSection", description = "@%HypertextSection")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface HypertextSectionRs extends IBaseRs {

    /**
     * Create a new Hypertext Section
     * @param sectionDto section data to be created
     * @return action status
     */
    @POST
    @Path("/")
    @Operation(
            summary=" Create hypertext section.  ",
            tags = { "HypertextSection" },
            description=" Create hypertext section.  ",
            operationId="    POST_HypertextSection_create",
            responses= {
                    @ApiResponse(
                            responseCode = "200",
                            description=" Succeed creating hypertext section.  ",
                            content=@Content(
                                    schema=@Schema(
                                            implementation= ActionStatus.class
                                    )
                            )
                    )
            }
    )
    ActionStatus create(@Parameter(description = "Hypertext section dto for a new insertion", required = true)
                                HypertextSectionDto sectionDto);

    /**
     * Create a new hypertext link
     * @param sectionDto link data to be created
     * @return action status
     */
    @PUT
    @Path("/")
    @Operation(
            summary=" Create hypertext section.  ",
            tags = { "HypertextSection" },
            description=" Create hypertext section.  ",
            operationId="    PUT_HypertextSection_update",
            responses= {
                    @ApiResponse(
                            responseCode = "200",
                            description=" Succeed updating hypertext section.  ",
                            content=@Content(
                                    schema=@Schema(
                                            implementation= ActionStatus.class
                                    )
                            )
                    )
            }
    )
    ActionStatus update(HypertextSectionDto sectionDto);


    /**
     * Create new or update hypertext sections
     * @param sectionsDto sections data to be created or updated
     * @return action status
     */
    @POST
    @Path("/massCreateOrUpdate")
    @Operation(
            summary=" Create or update hypertext sections.  ",
            tags = { "HypertextSection" },
            description=" Create or update hypertext sections.  ",
            operationId="    POST_HypertextSection_createOrUpdate",
            responses= {
                    @ApiResponse(
                            responseCode = "200",
                            description=" Succeed creating or updating hypertext sections.  ",
                            content=@Content(
                                    schema=@Schema(
                                            implementation= ActionStatus.class
                                    )
                            )
                    )
            }
    )
    List<HypertextSection> createOrUpdate(List<HypertextSectionDto> sectionsDto);

    @DELETE
    @Path("/{code}")
    @Operation(
            summary=" Deletes the section with the specified code. ",
            description=" Deletes the section with the specified code. ",
            operationId="    DELETE_HypertextSection_{code}",
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
