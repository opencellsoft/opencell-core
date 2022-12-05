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

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(summary = "Create a new user account", tags = { "User account management" })
    ActionStatus create(UserAccountDto postData);

    /**
     * Update an existing user account
     * 
     * @param postData The user account's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    @Operation(summary = "Update an existing user account", tags = { "User account management" })
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
    @Operation(summary = "Search for a user account with a given code", tags = { "Deprecated" }, deprecated = true)
    GetUserAccountResponseDto find(@QueryParam("userAccountCode") String userAccountCode, @DefaultValue("false") @QueryParam("includeSubscriptions") boolean includeSubscriptions,
            @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF);

    /**
     * Search for a user account with a given code.
     *
     * @param userAccountCode user account code
     * @param includeSubscriptions True to include subscriptions
     * @param inheritCF Should inherited custom fields be retrieved. Defaults to INHERIT_NO_MERGE.
     * @return found user account if exist
     */
    @GET
    @Path("/{userAccountCode}")
    @Operation(summary = "Search for a user account with a given code", tags = { "User account management" })
    GetUserAccountResponseDto findV2(@PathParam("userAccountCode") String userAccountCode, @DefaultValue("false") @QueryParam("includeSubscriptions") boolean includeSubscriptions,
                                   @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF);

    /**
     * Remove an existing user account with a given code.
     * 
     * @param userAccountCode The user account's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{userAccountCode}")
    @Operation(summary = "Remove an existing user account with a given code", tags = { "User account management" },
    responses = {
            @ApiResponse(responseCode="200", description = "the user account is successfully deleted",
                    content = @Content(schema = @Schema(implementation = ActionStatus.class))),
            @ApiResponse(responseCode = "404", description = "unknown user account code"),
            @ApiResponse(responseCode = "400", description = "the user account is referenced")
    })
    Response remove(@PathParam("userAccountCode") String userAccountCode);

    /**
     * List user accounts filtered by a billing account's code.
     * 
     * @param billingAccountCode The user billing account's code
     * @return list of user accounts.
     */
    @GET
    @Path("/list")
    @Operation(summary = "List user accounts filtered by a billing account's code", tags = { "Deprecated" }, deprecated = true)
    UserAccountsResponseDto listByBillingAccount(@QueryParam("billingAccountCode") String billingAccountCode);

    /**
     * List user accounts filtered by a billing account's code.
     *
     * @param billingAccountCode The user billing account's code
     * @return list of user accounts.
     */
    @GET
    @Path("/billingAccounts/{billingAccountCode}")
    @Operation(summary = "List user accounts filtered by a billing account's code", tags = { "User account management" })
    UserAccountsResponseDto listByBillingAccountV2(@PathParam("billingAccountCode") String billingAccountCode);

    /**
     * List user accounts matching a given criteria
     *
     * @return List of user accounts
     */
    @GET
    @Path("/listGetAll")
    @Operation(summary = "List all user accounts.",
            tags = { "User account management" })
    UserAccountsResponseDto listGetAll();

    /**
     * Create new or update an existing user account.
     * 
     * @param postData The user account's data
     * @return Request processing status
     */
    @POST
    @Operation(summary = "Create new or update an existing user account", tags = { "Deprecated" }, deprecated = true)
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
    @Operation(summary = "Filter counters by period date", tags = { "User account management" })
    GetCountersInstancesResponseDto filterUserAccountCountersByPeriod(@QueryParam("userAccountCode") String userAccountCode, @QueryParam("date") @RestDateParam Date date);

    /**
     * Filter counters by period date.
     *
     * @param userAccountCode The user account's code
     * @param date The date corresponding to the period
     * @return counter instances.
     */
    @GET
    @Path("/{userAccountCode}/filterCountersByPeriod")
    @Operation(summary = "Filter counters by period date", tags = { "User account management" })
    GetCountersInstancesResponseDto filterUserAccountCountersByPeriodV2(@PathParam("userAccountCode") String userAccountCode, @QueryParam("date") @RestDateParam Date date);

    /**
     * Apply a product on a user account.
     *
     * @param postData ApplyProductRequestDto userAccount field must be set
     * @return action status.
     */
    @POST
    @Path("/applyProduct")
    @Operation(summary = "Filter counters by period date", tags = { "User account management" })
    ActionStatus applyProduct(ApplyProductRequestDto postData);
}
