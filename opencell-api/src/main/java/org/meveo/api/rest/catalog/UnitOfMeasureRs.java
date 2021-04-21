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
import org.meveo.api.dto.catalog.UnitOfMeasureDto;
import org.meveo.api.dto.response.catalog.GetListUnitOfMeasureResponseDto;
import org.meveo.api.dto.response.catalog.GetUnitOfMeasureResponseDto;
import org.meveo.api.rest.IBaseRs;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
/**
 * @author Mounir Bahije
 **/

@Path("/catalog/unitOfMeasure")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface UnitOfMeasureRs extends IBaseRs {

    /**
     * Create a new unitOfMeasure
     * 
     * @param postData The unitOfMeasure's data
     * @return Request processing status
     */
    @Path("/")
    @POST
    ActionStatus create(UnitOfMeasureDto postData);

    /**
     * Update an existing unitOfMeasure
     * 
     * @param postData The unitOfMeasure's data
     * @return Request processing status
     */
    @Path("/")
    @PUT
    ActionStatus update(UnitOfMeasureDto postData);

    /**
     * Search for a unitOfMeasure with a given code
     * 
     * @param unitOfMeasureCode The unitOfMeasure's code
     * @return A unitOfMeasure
     */
    @GET
    @Path("/")
    GetUnitOfMeasureResponseDto find(@QueryParam("unitOfMeasureCode") String unitOfMeasureCode);

    /**
     * Remove an existing unitOfMeasure with a given code
     * 
     * @param unitOfMeasureCode The unitOfMeasure's code
     * @return Request processing status
     */
    @Path("/{code}")
    @DELETE
    ActionStatus delete(@PathParam("code") String unitOfMeasureCode);

    /**
     * Create new or update an existing unitOfMeasure
     * 
     * @param postData The unitOfMeasure's data
     * @return Request processing status
     */
    @Path("/createOrUpdate")
    @POST
    ActionStatus createOrUpdate(UnitOfMeasureDto postData);

    /**
     * List all currencies.
     * @return list of all unitOfMeasure/
     */
    @GET
    @Path("/list")
    GetListUnitOfMeasureResponseDto list();

    /**
     * List UnitOfMeasures matching a given criteria
     *
     * @return List of UnitOfMeasures
     */
    @GET
    @Path("/listGetAll")
    GetListUnitOfMeasureResponseDto listGetAll();
}
