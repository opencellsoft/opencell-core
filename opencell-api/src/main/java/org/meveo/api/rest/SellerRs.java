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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.SellerDto;
import org.meveo.api.dto.response.GetSellerResponse;
import org.meveo.api.dto.response.SellerCodesResponseDto;
import org.meveo.api.dto.response.SellerResponseDto;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

/**
 * Web service for managing {@link org.meveo.model.admin.Seller}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/seller")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface SellerRs extends IBaseRs {

    /**
     * Create seller.
     * 
     * @param postData posted data to API containing information of seller
     * @return action status
     */
    @POST
    @Path("/")
    ActionStatus create(SellerDto postData);

    /**
     * Update seller.
     * 
     * @param postData posted data
     * @return action status.
     */
    @PUT
    @Path("/")
    ActionStatus update(SellerDto postData);

    /**
     * Search for seller with a given code.
     * 
     * @param sellerCode seller code
     * @param inheritCF Should inherited custom fields be retrieved. Defaults to INHERIT_NO_MERGE.
     * @return found seller.
     */
    @GET
    @Path("/")
    GetSellerResponse find(@QueryParam("sellerCode") String sellerCode, @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF);

    /**
     * Remove seller with a given code.
     * 
     * @param sellerCode code of seller
     * @return action status.
     */
    @DELETE
    @Path("/{sellerCode}")
    ActionStatus remove(@PathParam("sellerCode") String sellerCode);

    /**
     * Search for seller with a given code.
     * 
     * @return list of seller
     */
    @GET
    @Path("/list")
    SellerResponseDto list();

    /**
     * Search for all seller's code.
     *
     * @return list of seller's code.
     */
    @GET
    @Path("/listSellerCodes")
    SellerCodesResponseDto listSellerCodes();

    /**
     * Create or update a seller.
     *
     * @param postData posted data
     * @return created or updated seller.
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(SellerDto postData);

}
