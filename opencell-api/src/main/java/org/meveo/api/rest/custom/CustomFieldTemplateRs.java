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

package org.meveo.api.rest.custom;

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
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.dto.response.GetCustomFieldTemplateReponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Edward P. Legaspi
 **/
@Path("/customFieldTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface CustomFieldTemplateRs extends IBaseRs {

    /**
     * Define a new custom field
     * 
     * @param postData posted data to API
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(CustomFieldTemplateDto postData);

    /**
     * Update existing custom field definition
     * 
     * @param postData posted data to API
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(CustomFieldTemplateDto postData);

    /**
     * Remove custom field definition given its code and entity it applies to
     * 
     * @param customFieldTemplateCode Custom field template code
     * @param appliesTo Entity it applies to
     * @return Request processing status
     */
    @DELETE
    @Path("/{customFieldTemplateCode}/{appliesTo}")
    ActionStatus remove(@PathParam("customFieldTemplateCode") String customFieldTemplateCode, @PathParam("appliesTo") String appliesTo);

    /**
     * Get custom field definition
     * 
     * @param customFieldTemplateCode Custom field template code
     * @param appliesTo Entity it applies to
     * @return instance of GetCustomFieldTemplateReponseDto
     */
    @GET
    @Path("/")
    GetCustomFieldTemplateReponseDto find(@QueryParam("customFieldTemplateCode") String customFieldTemplateCode, @QueryParam("appliesTo") String appliesTo);

    /**
     * Define new or update existing custom field definition
     * 
     * @param postData posted data to API
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(CustomFieldTemplateDto postData);

    /**
     * Enable a Custom field template with a given code
     * 
     * @param customFieldTemplateCode Custom field template code
     * @param appliesTo Entity it applies to
     * @return Request processing status
     */
    @POST
    @Path("/{customFieldTemplateCode}/{appliesTo}/enable")
    ActionStatus enable(@PathParam("customFieldTemplateCode") String customFieldTemplateCode, @PathParam("appliesTo") String appliesTo);

    /**
     * Disable a Custom field template with a given code
     * 
     * @param customFieldTemplateCode Custom field template code
     * @param appliesTo Entity it applies to
     * @return Request processing status
     */
    @POST
    @Path("/{customFieldTemplateCode}/{appliesTo}/disable")
    ActionStatus disable(@PathParam("customFieldTemplateCode") String customFieldTemplateCode, @PathParam("appliesTo") String appliesTo);

}