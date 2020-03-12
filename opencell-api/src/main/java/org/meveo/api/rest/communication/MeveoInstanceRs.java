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

package org.meveo.api.rest.communication;

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
import org.meveo.api.dto.communication.MeveoInstanceDto;
import org.meveo.api.dto.response.communication.MeveoInstanceResponseDto;
import org.meveo.api.dto.response.communication.MeveoInstancesResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @since Jun 4, 2016 4:05:47 AM
 *
 */
@Path("/communication/meveoInstance")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface MeveoInstanceRs extends IBaseRs {

	/**
	 * Create a meveoInstance by dto.
     *
	 * @param meveoInstanceDto meveo instance
	 * @return action status
	 */
	@POST
    @Path("/")
    ActionStatus create(MeveoInstanceDto meveoInstanceDto);

	/**
	 * Update a meveoInstance by dto
     *
	 * @param meveoInstanceDto
	 * @return Request processing status
	 */
    @PUT
    @Path("/")
    ActionStatus update(MeveoInstanceDto meveoInstanceDto);

    /**
     * Find a meveoInstance by code
     *
     * @param code the code of the meveo instance
     * @return Request processing status
     */
    @GET
    @Path("/")
    MeveoInstanceResponseDto find(@QueryParam("code") String code);

    /**
     * Remove a meveoInstance by code
     *
     * @param code the code of the meveo instance
     * @return Request processing status
     */
    @DELETE
    @Path("/{code}")
    ActionStatus remove(@PathParam("code") String code);

    /**
     * List meveoInstances
     *
     * @return List of Meveo Instances 
     */
    @GET
    @Path("/list")
    MeveoInstancesResponseDto list();

    /**
     * CreateOrUpdate a meveoInstance by dto
     *
     * @param meveoInstanceDto meveo Instance data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(MeveoInstanceDto meveoInstanceDto);
}

