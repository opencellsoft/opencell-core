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

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.TaxDto;
import org.meveo.api.dto.response.GetTaxResponse;
import org.meveo.api.dto.response.GetTaxesResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Web service for managing {@link org.meveo.model.billing.Tax}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/tax")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface TaxRs extends IBaseRs {

    /**
     * Create tax. Description per language can be defined
     * 
     * @param postData tax to be created
     * @return action status
     */
    @POST
    @Path("/")
    ActionStatus create(TaxDto postData);

    /**
     * Update tax. Description per language can be defined
     * 
     * @param postData tax to be updated
     * @return action status
     */
    @PUT
    @Path("/")
    ActionStatus update(TaxDto postData);

    /**
     * Search tax with a given code.
     * 
     * @param taxCode tax's
     * @return tax if exists
     */
    @GET
    @Path("/")
    GetTaxResponse find(@QueryParam("taxCode") String taxCode);

    /**
     * Remove tax with a given code.
     * 
     * @param taxCode tax's code
     * @return action status
     */
    @DELETE
    @Path("/{taxCode}")
    ActionStatus remove(@PathParam("taxCode") String taxCode);

    /**
     * Create or uptadate a tax. 
     *
     * @param postData tax to be created or updated
     * @return action status
     */
    @POST 
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(TaxDto postData);

    /**
     * Search for the list of taxes.
     *
     * @return list of all taxes.
     */
    @GET 
    @Path("/list")
    GetTaxesResponse list();

    /**
     * List taxes matching a given criteria
     *
     * @return List of taxes
     */
    @GET
    @Path("/listGetAll")
    GetTaxesResponse listGetAll();
}
