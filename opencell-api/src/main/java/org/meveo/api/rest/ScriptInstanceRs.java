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
import javax.ws.rs.core.Response;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ScriptInstanceDto;
import org.meveo.api.dto.response.GetScriptInstanceResponseDto;
import org.meveo.api.dto.response.ScriptInstanceReponseDto;

import java.util.List;
import java.util.Map;

/**
 * @author Edward P. Legaspi
 * @author Mounir Bahije
 * @lastModifiedVersion 5.2
 *
 * **/
@Path("/scriptInstance")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface ScriptInstanceRs extends IBaseRs {

    /**
     * Create a new script instance.
     * 
     * @param postData The script instance's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ScriptInstanceReponseDto create(ScriptInstanceDto postData);

    /**
     * Update an existing script instance.
     * 
     * @param postData The script instance's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ScriptInstanceReponseDto update(ScriptInstanceDto postData);

    /**
     * Remove an existing script instance with a given code .
     * 
     * @param scriptInstanceCode The script instance's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{scriptInstanceCode}")
    ActionStatus remove(@PathParam("scriptInstanceCode") String scriptInstanceCode);

    /**
     * Find a script instance with a given code.
     *
     * @param scriptInstanceCode The script instance's code
     * @return script instance
     */
    @GET
    @Path("/")
    GetScriptInstanceResponseDto find(@QueryParam("scriptInstanceCode") String scriptInstanceCode);

    /**
     * Execute a script instance with a given code and list of parameters for the context of the script
     *
     * @param scriptInstanceCode The script instance's code
     * @return response of the script
     */
    @GET
    @Path("/execute")
    Response execute(@QueryParam("scriptInstanceCode") String scriptInstanceCode);

    /**
     * Create new or update an existing script instance with a given code.
     * 
     * @param postData The script instance's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ScriptInstanceReponseDto createOrUpdate(ScriptInstanceDto postData);

    /**
     * Enable a Script instance with a given code
     * 
     * @param code Script instance code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Script instance with a given code
     * 
     * @param code Script instance code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    ActionStatus disable(@PathParam("code") String code);

}