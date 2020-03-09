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

package org.meveo.api.rest.catalog;

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
import org.meveo.api.dto.catalog.TriggeredEdrTemplateDto;
import org.meveo.api.dto.response.catalog.GetTriggeredEdrResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Edward P. Legaspi
 **/
@Path("/catalog/triggeredEdr")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface TriggeredEdrRs extends IBaseRs {

    /**
     * Create a new triggered edr. template
     * 
     * @param postData The triggered edr. template's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(TriggeredEdrTemplateDto postData);

    /**
     * Update an existing triggered edr. template
     * 
     * @param postData The triggered edr. template's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(TriggeredEdrTemplateDto postData);

    /**
     * Find triggered edr with a given code.
     * 
     * @param triggeredEdrCode The triggered edr's code
     * @return Returns triggeredEdrTemplate
     */
    @GET
    @Path("/")
    GetTriggeredEdrResponseDto find(@QueryParam("triggeredEdrCode") String triggeredEdrCode);

    /**
     * Remove an existing triggered edr template with a given code.
     * 
     * @param triggeredEdrCode The triggered edr's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{triggeredEdrCode}")
    ActionStatus remove(@PathParam("triggeredEdrCode") String triggeredEdrCode);

    /**
     * Create new or update an existing triggered edr template
     * 
     * @param postData The triggered edr template's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(TriggeredEdrTemplateDto postData);
}
