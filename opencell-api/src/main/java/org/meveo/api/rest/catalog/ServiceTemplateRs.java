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

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.catalog.GetListServiceTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetServiceTemplateResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Web service for managing {@link org.meveo.model.catalog.ServiceTemplate}.
 * 
 * @author Edward P. Legaspi
 * @author Youssef IZEM
 * @lastModifiedVersion 5.4
 **/
@Path("/catalog/serviceTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface ServiceTemplateRs extends IBaseRs {

    /**
     * Create a new service template.
     * 
     * @param postData The service template's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(ServiceTemplateDto postData);

    /**
     * Update an existing service template.
     * 
     * @param postData The service template's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(ServiceTemplateDto postData);

    /**
     * Find a service template with a given code.
     * 
     * @param serviceTemplateCode The service template's code
     * @param inheritCF Should inherited custom fields be retrieved. Defaults to INHERIT_NO_MERGE.
     * @return Return serviceTemplate
     */
    @GET
    @Path("/")
    GetServiceTemplateResponseDto find(@QueryParam("serviceTemplateCode") String serviceTemplateCode,
            @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF);

    /**
     * Remove service template with a given code.
     * 
     * @param serviceTemplateCode The service template's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{serviceTemplateCode}")
    ActionStatus remove(@PathParam("serviceTemplateCode") String serviceTemplateCode);

    /**
     * Create new or update an existing service template
     * 
     * @param postData The service template's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(ServiceTemplateDto postData);

    /**
     * Enable a Service template with a given code
     * 
     * @param code Service template code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Service template with a given code
     * 
     * @param code Service template code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    ActionStatus disable(@PathParam("code") String code);
    
    /**
     * Gets a service template list widh .
     * 
     * @param pagingAndFiltering PagingAndFiltering config.
     * @return Return serviceTemplate list
     */
    @GET
    @Path("/list")
    GetListServiceTemplateResponseDto list(PagingAndFiltering pagingAndFiltering);

    /**
     * Gets a service template list.
     *
     * @return Return serviceTemplate list
     */
    @GET
    @Path("/list")
    GetListServiceTemplateResponseDto listGet();
}