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

package org.meveo.api.rest.account;

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
import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.response.account.AccessesResponseDto;
import org.meveo.api.dto.response.account.GetAccessResponseDto;
import org.meveo.api.rest.IBaseRs;

import java.util.Date;

/**
 * @author Edward P. Legaspi
 **/
@Path("/account/access")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface AccessRs extends IBaseRs {

    /**
     * Create a new access
     * 
     * @param postData Access data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(AccessDto postData);

    /**
     * Update an existing access
     *
     * @param postData Access data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(AccessDto postData);

    /**
     * Search for an access with a given access code and subscription code.
     * 
     * @param accessCode Access code
     * @param subscriptionCode Subscription code
     * @param startDate Access startDate
     * @param endDate Access endDate
     * @param usageDate a usage date
     * @return Access
     */
    @GET
    @Path("/")
    GetAccessResponseDto find(@QueryParam("accessCode") String accessCode, @QueryParam("subscriptionCode") String subscriptionCode, @QueryParam("subscriptionValidityDate") Date subscriptionValidityDate, @QueryParam("startDate") Date startDate, @QueryParam("endDate") Date endDate, @QueryParam("usageDate") Date usageDate);

    /**
     * Remove an access with a given access code and subscription code.
     * 
     * @param accessCode Access code
     * @param subscriptionCode Subscription code
     * @param startDate Access startDate
     * @param endDate Access endDate
     * @return Request processing status
     */
    @DELETE
    @Path("/{accessCode}/{subscriptionCode}/{startDate}/{endDate}")
    ActionStatus remove(@PathParam("accessCode") String accessCode, @PathParam("subscriptionCode") String subscriptionCode, @PathParam("startDate") Date startDate, @PathParam("endDate") Date endDate);

    /**
     * List Access filtered by subscriptionCode.
     * 
     * @param subscriptionCode Subscription code
     * @return A list of accesses
     */
    @GET
    @Path("/list")
    AccessesResponseDto listBySubscription(@QueryParam("subscriptionCode") String subscriptionCode);

    /**
     * Create new or update an existing access
     * 
     * @param postData data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(AccessDto postData);

    /**
     * Enable an Access point with a given access code and subscription code.
     * 
     * @param accessCode Access code
     * @param subscriptionCode Subscription code
     * @param startDate Access startDate
     * @param endDate Access endDate
     * @return Request processing status
     */
    @POST
    @Path("/{accessCode}/{subscriptionCode}/{startDate}/{endDate}/enable")
    ActionStatus enable(@PathParam("accessCode") String accessCode, @PathParam("subscriptionCode") String subscriptionCode, @PathParam("startDate") Date startDate, @PathParam("endDate") Date endDate);

    /**
     * Disable an Access point with a given access code and subscription code.
     * 
     * @param accessCode Access code
     * @param subscriptionCode Subscription code
     * @param startDate Access startDate
     * @param endDate Access endDate
     * @return Request processing status
     */
    @POST
    @Path("/{accessCode}/{subscriptionCode}/{startDate}/{endDate}/disable")
    ActionStatus disable(@PathParam("accessCode") String accessCode, @PathParam("subscriptionCode") String subscriptionCode, @PathParam("startDate") Date startDate, @PathParam("endDate") Date endDate);
}
