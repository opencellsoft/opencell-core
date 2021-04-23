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
import org.meveo.api.dto.BillingCycleDto;
import org.meveo.api.dto.response.BillingCyclesResponseDto;
import org.meveo.api.dto.response.GetBillingCycleResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author Edward P. Legaspi
 **/
@Path("/billingCycle")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface BillingCycleRs extends IBaseRs {

    /**
     * Search for list of billingCycles.
     *
     * @return list of billingCycles
     */
    @GET
    @Path("/list")
    BillingCyclesResponseDto list();

    /**
     * Create a new billing cycle.
     * 
     * @param postData billing cycle dto
     * @return action status
     */
    @POST
    @Path("/")
    ActionStatus create(BillingCycleDto postData);

    /**
     * Update an existing billing cycle.
     * 
     * @param postData billing cycle
     * @return actioon result
     */
    @PUT
    @Path("/")
    ActionStatus update(BillingCycleDto postData);

    /**
     * Search for billing cycle with a given code.
     * 
     * @param billingCycleCode The billing cycle's code
     * @return billing cycle if exists
     */
    @GET
    @Path("/")
    GetBillingCycleResponse find(@QueryParam("billingCycleCode") String billingCycleCode);

    /**
     * Remove an existing billing cycle with a given code.
     * 
     * @param billingCycleCode The billing cycle's code
     * @return action result
     */
    @DELETE
    @Path("/{billingCycleCode}")
    ActionStatus remove(@PathParam("billingCycleCode") String billingCycleCode);

    /**
     * Create new or update an existing billing cycle with a given code
     * 
     * @param postData The billing cycle's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(BillingCycleDto postData);

}
