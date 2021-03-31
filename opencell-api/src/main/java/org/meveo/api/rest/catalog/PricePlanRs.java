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
import org.meveo.api.dto.catalog.PricePlanMatrixDto;
import org.meveo.api.dto.response.catalog.GetPricePlanResponseDto;
import org.meveo.api.dto.response.catalog.PricePlanMatrixesResponseDto;
import org.meveo.api.rest.IBaseRs;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Web service for managing {@link org.meveo.model.catalog.PricePlanMatrix}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/catalog/pricePlan")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface PricePlanRs extends IBaseRs {

    /**
     * Create a new price plan matrix
     * 
     * @param postData The price plan matrix's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(PricePlanMatrixDto postData);

    /**
     * Update an existing price plan matrix
     * 
     * @param postData The price plan matrix's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(PricePlanMatrixDto postData);

    /**
     * Find a price plan matrix with a given code
     * 
     * @param pricePlanCode The price plan's code
     * @return pricePlanMatrixDto Returns pricePlanMatrixDto containing pricePlan
     */
    @GET
    @Path("/")    
    GetPricePlanResponseDto find(@QueryParam("pricePlanCode") String pricePlanCode);

    /**
     * Remove an existing price plan matrix with a given code
     * 
     * @param pricePlanCode The price plan's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{pricePlanCode}")    
    ActionStatus remove(@PathParam("pricePlanCode") String pricePlanCode);

    /**
     * List price plan matrix.
     * 
     * @param eventCode The charge's code linked to price plan.
     * @return Return pricePlanMatrixes
     */
    @GET
    @Path("/list")
    PricePlanMatrixesResponseDto listPricePlanByEventCode(@QueryParam("eventCode") String eventCode);

    /**
     * List PricePlanMatrixes
     *
     * @return List of PricePlanMatrixes
     */
    @GET
    @Path("/listGetAll")
    PricePlanMatrixesResponseDto listGetAll();

    /**
     * Create new or update an existing price plan matrix
     * 
     * @param postData The price plan matrix's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(PricePlanMatrixDto postData);

    /**
     * Enable a Price plan with a given code
     * 
     * @param code Price plan code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Price plan with a given code
     * 
     * @param code Price plan code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    ActionStatus disable(@PathParam("code") String code);

}