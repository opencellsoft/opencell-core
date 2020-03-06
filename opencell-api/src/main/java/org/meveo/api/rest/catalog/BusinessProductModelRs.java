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
import org.meveo.api.dto.catalog.BusinessProductModelDto;
import org.meveo.api.dto.response.catalog.GetBusinessProductModelResponseDto;
import org.meveo.api.dto.response.module.MeveoModuleDtosResponse;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Edward P. Legaspi
 **/
@Path("/catalog/businessProductModel")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface BusinessProductModelRs extends IBaseRs {

    /**
     * Create a new business product model
     * 
     * @param postData The business product model's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(BusinessProductModelDto postData);

    /**
     * Update an existing business product model
     * 
     * @param postData The business product model's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(BusinessProductModelDto postData);

    /**
     * Remove an existing business product model with a given code 
     * 
     * @param businessProductModelCode The business product model's code
     * @return A business product model
     */
    @GET
    @Path("/")
    GetBusinessProductModelResponseDto find(@QueryParam("businessProductModelCode") String businessProductModelCode);


    /**
     * Remove an existing business product model with a given code 
     * 
     * @param businessProductModelCode The business product model's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{businessProductModelCode}")
    ActionStatus remove(@PathParam("businessProductModelCode") String businessProductModelCode);

    /**
     * Create new or update an existing business product model
     * 
     * @param postData The business product model's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(BusinessProductModelDto postData);

    /**
     * List business product models
     * 
     * @return A list of business product models
     */
    @GET
    @Path("/list")
    public MeveoModuleDtosResponse list();

    /**
     * Install business product model module
     * @param moduleDto business product model
     * @return Request processing status
     */
    @PUT
    @Path("/install")
    public ActionStatus install(BusinessProductModelDto moduleDto);
}
