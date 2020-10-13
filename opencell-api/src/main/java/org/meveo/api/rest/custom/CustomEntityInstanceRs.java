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
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.CustomEntityInstanceDto;
import org.meveo.api.dto.custom.CustomTableDataResponseDto;
import org.meveo.api.dto.response.CustomEntityInstanceResponseDto;
import org.meveo.api.dto.response.CustomEntityInstancesResponseDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.rest.IBaseRs;

/**
 * Rest API for custom entity instance management
 * 
 * @author Andrius Karpavicius
 **/
@Path("/customEntityInstance")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface CustomEntityInstanceRs extends IBaseRs {

    /**
     * Create a new custom entity instance using a custom entity template.
     *
     * @param dto The custom entity instance's data
     * @param customEntityTemplateCode The custom entity template's code
     * @return Request processing status
     */
    @POST
    @Path("/{customEntityTemplateCode}")
    ActionStatus create(@PathParam("customEntityTemplateCode") String customEntityTemplateCode, CustomEntityInstanceDto dto);

    /**
     * Update an existing custom entity instance using a custom entity template
     * 
     * @param dto The custom entity instance's data
     * @param customEntityTemplateCode The custom entity template's code
     * @return Request processing status
     */
    @PUT
    @Path("/{customEntityTemplateCode}")
    ActionStatus update(@PathParam("customEntityTemplateCode") String customEntityTemplateCode, CustomEntityInstanceDto dto);

    /**
     * Remove an existing custom entity instance with a given code from a custom entity template given by code
     * 
     * @param customEntityTemplateCode The custom entity template's code
     * @param code The custom entity instance's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{customEntityTemplateCode}/{code}")
    ActionStatus remove(@PathParam("customEntityTemplateCode") String customEntityTemplateCode, @PathParam("code") String code);

    /**
     * Find a #### with a given (exemple) code .
     * 
     * @param customEntityTemplateCode The custom entity template's code
     * @param code The custom entity instance's code
     * @return Return a customEntityInstance
     */
    @GET
    @Path("/{customEntityTemplateCode}/{code}")
    CustomEntityInstanceResponseDto find(@PathParam("customEntityTemplateCode") String customEntityTemplateCode, @PathParam("code") String code);

    /**
     * List custom entity instances.
     * 
     * @param customEntityTemplateCode The custom entity instance's code
     * @return A list of custom entity instances
     */
    @GET
    @Path("/list/{customEntityTemplateCode}")
    CustomEntityInstancesResponseDto list(@PathParam("customEntityTemplateCode") String customEntityTemplateCode);

    /**
     * Search in custom entity instances.
     * 
     * @param customEntityTemplateCode The custom entity instance's code
     * @param pagingAndFiltering Paging and search criteria
     * @return Custom table data
     */
    @POST
    @Path("/list/{customEntityTemplateCode}")
    CustomEntityInstancesResponseDto list(@PathParam("customEntityTemplateCode") String customEntityTemplateCode, PagingAndFiltering pagingAndFiltering);

    /**
     * Create new or update an existing custom entity instance with a given code.
     * 
     * @param dto The custom entity instance's data
     * @param customEntityTemplateCode code of custome entity template.
     * @return Request processing status
     */
    @POST
    @Path("/{customEntityTemplateCode}/createOrUpdate")
    ActionStatus createOrUpdate(@PathParam("customEntityTemplateCode") String customEntityTemplateCode, CustomEntityInstanceDto dto);

    /**
     * Enable a Custom entity instance with a given code
     * 
     * @param customEntityTemplateCode The custom entity template's code
     * @param code Custom entity instance code
     * @return Request processing status
     */
    @POST
    @Path("/{customEntityTemplateCode}/{code}/enable")
    ActionStatus enable(@PathParam("customEntityTemplateCode") String customEntityTemplateCode, @PathParam("code") String code);

    /**
     * Disable a Custom entity instance with a given code
     * 
     * @param customEntityTemplateCode The custom entity template's code
     * @param code Custom entity instance code
     * @return Request processing status
     */
    @POST
    @Path("/{customEntityTemplateCode}/{code}/disable")
    ActionStatus disable(@PathParam("customEntityTemplateCode") String customEntityTemplateCode, @PathParam("code") String code);
}