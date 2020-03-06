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
import org.meveo.api.dto.catalog.CounterTemplateDto;
import org.meveo.api.dto.response.catalog.GetCounterTemplateResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * Web service for managing {@link org.meveo.model.catalog.CounterTemplate}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/catalog/counterTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface CounterTemplateRs extends IBaseRs {

    /**
     * Create counter template.
     * 
     * @param postData counter template
     * @return action status
     */
    @POST
    @Path("/")
    ActionStatus create(CounterTemplateDto postData);

    /**
     * Update counter template.
     * 
     * @param postData counter template
     * @return action status
     */
    @PUT
    @Path("/")
    ActionStatus update(CounterTemplateDto postData);

    /**
     * Search counter template with a given code.
     * 
     * @param counterTemplateCode counter temlate's code
     * @return counter template
     */
    @GET
    @Path("/")
    GetCounterTemplateResponseDto find(@QueryParam("counterTemplateCode") String counterTemplateCode);

    /**
     * Remove counter template with a given code.
     * 
     * @param counterTemplateCode counter template's code
     * @return action status
     */
    @DELETE
    @Path("/{counterTemplateCode}")
    ActionStatus remove(@PathParam("counterTemplateCode") String counterTemplateCode);

    /**
     * Create or update a counter Template.
     *
     * @param postData counter template
     * @return action status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(CounterTemplateDto postData);

    /**
     * Enable a Counter template with a given code
     * 
     * @param code Counter template code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Counter template with a given code
     * 
     * @param code Counter template code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    ActionStatus disable(@PathParam("code") String code);
}
