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

package org.meveo.api.rest.account;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.response.TitleDto;
import org.meveo.api.dto.response.account.TitleResponseDto;
import org.meveo.api.dto.response.account.TitlesResponseDto;
import org.meveo.api.rest.IBaseRs;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/account/title")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface TitleRs extends IBaseRs {

    /**
     * Create a new title
     * 
     * @param postData The title's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(TitleDto postData);

    /**
     * Search for a title with a given code 
     * 
     * @param titleCode The title's code
     * @return A title's data
     */
    @GET
    @Path("/")
    TitleResponseDto find(@QueryParam("titleCode") String titleCode);

    /**
     * List titles 
     * 
     * @return A list of titles
     */
    @GET
    @Path("/list")
    TitlesResponseDto list();

    /**
     * List titles matching a given criteria
     *
     * @return List of titles
     */
    @GET
    @Path("/listGetAll")
    TitlesResponseDto listGetAll();

    /**
     * Update an existing title
     * 
     * @param postData The title's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(TitleDto postData);

    /**
     * Remove an existing title with a given code 
     * 
     * @param titleCode The title's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{titleCode}")
    public ActionStatus remove(@PathParam("titleCode") String titleCode);

    /**
     * Create new or update an existing title
     * 
     * @param postData The title's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(TitleDto postData);
}
