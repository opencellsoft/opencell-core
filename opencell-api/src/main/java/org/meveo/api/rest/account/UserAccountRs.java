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

import java.util.Date;

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
import org.meveo.api.dto.account.ApplyProductRequestDto;
import org.meveo.api.dto.account.UserAccountDto;
import org.meveo.api.dto.response.account.GetUserAccountResponseDto;
import org.meveo.api.dto.response.account.UserAccountsResponseDto;
import org.meveo.api.dto.response.billing.GetCountersInstancesResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.serialize.RestDateParam;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

/**
 * @author Edward P. Legaspi
 **/
@Path("/account/userAccount")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface UserAccountRs extends IBaseRs {

    /**
     * Create a new user account.
     * 
     * @param postData The user account's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(UserAccountDto postData);

    /**
     * Update an existing user account
     * 
     * @param postData The user account's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(UserAccountDto postData);

    /**
     * Search for a user account with a given code.
     * 
     * @param userAccountCode user account code
     * @param includeSubscriptions True to include subscriptions
     * @param inheritCF Should inherited custom fields be retrieved. Defaults to INHERIT_NO_MERGE.
     * @return found user account if exist
     */
    @GET
    @Path("/")
    GetUserAccountResponseDto find(@QueryParam("userAccountCode") String userAccountCode, @DefaultValue("false") @QueryParam("includeSubscriptions") boolean includeSubscriptions,
            @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF);

    /**
     * Remove an existing user account with a given code.
     * 
     * @param userAccountCode The user account's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{userAccountCode}")
    ActionStatus remove(@PathParam("userAccountCode") String userAccountCode);

    /**
     * List user accounts filtered by a billing account's code.
     * 
     * @param billingAccountCode The user billing account's code
     * @return list of user accounts.
     */
    @GET
    @Path("/list")
    UserAccountsResponseDto listByBillingAccount(@QueryParam("billingAccountCode") String billingAccountCode);

    /**
     * Create new or update an existing user account.
     * 
     * @param postData The user account's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(UserAccountDto postData);

    /**
     * Filter counters by period date.
     *
     * @param userAccountCode The user account's code
     * @param date The date corresponding to the period
     * @return counter instances.
     */
    @GET
    @Path("/filterCountersByPeriod")
    GetCountersInstancesResponseDto filterUserAccountCountersByPeriod(@QueryParam("userAccountCode") String userAccountCode, @QueryParam("date") @RestDateParam Date date);

    /**
     * Apply a product on a user account.
     *
     * @param postData ApplyProductRequestDto userAccount field must be set
     * @return action status.
     */
    @POST
    @Path("/applyProduct")
    ActionStatus applyProduct(ApplyProductRequestDto postData);
}
