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
import org.meveo.api.dto.TerminationReasonDto;
import org.meveo.api.dto.response.GetTerminationReasonResponse;

@Path("/terminationReason")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface TerminationReasonRs extends IBaseRs {

    /**
     * Create a new termination reason.
     * 
     * @param postData The termination reason's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(TerminationReasonDto postData);

    /**
     * Update an existing termination reason.
     * 
     * @param postData The termination reason's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(TerminationReasonDto postData);

    /**
     * Create new or update an existing termination reason with a given code.
     * 
     * @param postData The termination reason's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(TerminationReasonDto postData);

    /**
     * Remove an existing termination reason with a given code.
     * 
     * @param terminationReasonCode The termination reason's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{terminationReasonCode}")
    ActionStatus remove(@PathParam("terminationReasonCode") String terminationReasonCode);

    /**
     * Find a termination reason with a given code.
     * 
     * @param code The termination reason's code
     * @return found termination reason
     */
    @GET
    @Path("/")
    GetTerminationReasonResponse find(@QueryParam("terminationReasonCode") String code);

    /**
     * List of termination reasons.
     * 
     * @return A list of termination reasons
     */
    @GET
    @Path("/list")
    GetTerminationReasonResponse list();

}
