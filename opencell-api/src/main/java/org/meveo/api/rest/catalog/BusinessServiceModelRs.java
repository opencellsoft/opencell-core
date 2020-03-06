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
import org.meveo.api.dto.catalog.BusinessServiceModelDto;
import org.meveo.api.dto.response.catalog.GetBusinessServiceModelResponseDto;
import org.meveo.api.dto.response.module.MeveoModuleDtosResponse;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Edward P. Legaspi
 **/
@Path("/catalog/businessServiceModel")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface BusinessServiceModelRs extends IBaseRs {

    /**
     * Create a new business service model
     * 
     * @param postData The business service model's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(BusinessServiceModelDto postData);

    /**
     * Update an existing business service model
     * 
     * @param postData The business service model's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(BusinessServiceModelDto postData);

    /**
     * Search for a business service model with a given code 
     * 
     * @param businessServiceModelCode The business service model's code
     * @return A business servie model
     */
    @GET
    @Path("/")
    GetBusinessServiceModelResponseDto find(@QueryParam("businessServiceModelCode") String businessServiceModelCode);

    /**
     * Remove an existing business service model with a given code 
     * 
     * @param businessServiceModelCode The business service model's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{businessServiceModelCode}")
    ActionStatus remove(@PathParam("businessServiceModelCode") String businessServiceModelCode);

    /**
     * Create new or update an existing business service model
     * 
     * @param postData The business service model's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(BusinessServiceModelDto postData);

    /**
     * List business service model
     * 
     * @return A list of business service models
     */
    @GET
    @Path("/list")
    public MeveoModuleDtosResponse list();

    /**
     * Install business offer model module
     * 
     * @param moduleDto The business service model's data
     * @return Request processing status
     */
    @PUT
    @Path("/install")
    public ActionStatus install(BusinessServiceModelDto moduleDto);
}