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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

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

import io.swagger.v3.oas.annotations.Operation;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.account.BillingAccountDto;
import org.meveo.api.dto.response.account.BillingAccountsResponseDto;
import org.meveo.api.dto.response.account.GetBillingAccountResponseDto;
import org.meveo.api.dto.response.billing.GetCountersInstancesResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.serialize.RestDateParam;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

/**
 * @author Edward P. Legaspi
 **/
@Path("/account/billingAccount")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface BillingAccountRs extends IBaseRs {

    /**
     * Create a new billing account.
     * 
     * @param postData Billing account data
     * @return Request processing status
     */
    @POST
    @Path("/")
    @Operation(summary = "Create a new billing account", tags = { "Billing account management" })
    ActionStatus create(BillingAccountDto postData);

    /**
     * Update existing billing account.
     * 
     * @param postData Billing account data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    @Operation(summary = "Update existing billing account", tags = { "Billing account management" })
    ActionStatus update(BillingAccountDto postData);

    /**
     * Search for a billing account with a given code.
     * 
     * @param billingAccountCode Billing account code
     * @param inheritCF Should inherited custom fields be retrieved. Defaults to INHERIT_NO_MERGE.
     * @param includeUserAccounts True to include user accounts
     * @return Billing account
     */
    @GET
    @Path("/")
    @Operation(summary = "Search for a billing account with a given code", tags = { "Deprecated" }, deprecated = true)
    GetBillingAccountResponseDto find(@QueryParam("billingAccountCode") String billingAccountCode, @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF,
            @QueryParam("includeUserAccounts") boolean includeUserAccounts);

    /**
     * Search for a billing account with a given code.
     *
     * @param billingAccountCode Billing account code
     * @param inheritCF Should inherited custom fields be retrieved. Defaults to INHERIT_NO_MERGE.
     * @param includeUserAccounts True to include user accounts
     * @return Billing account
     */
    @GET
    @Path("/{billingAccountCode}")
    @Operation(summary = "Search for a billing account with a given code", tags = { "Billing account management" })
    GetBillingAccountResponseDto findV2(@PathParam("billingAccountCode") String billingAccountCode, @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF,
                                      @QueryParam("includeUserAccounts") boolean includeUserAccounts);

    /**
     * Remove a billing account with a Billing Account Code.
     *
     * @param billingAccountCode Billing account code
     * @return Request processing status
     */
    @DELETE
    @Path("/{billingAccountCode}")
    @Operation(summary = "Remove a billing account with a Billing Account Code", tags = { "Billing account management" })
    ActionStatus remove(@PathParam("billingAccountCode") String billingAccountCode);

    /**
     * List BillingAccount filter by customerAccountCode.
     * 
     * @param customerAccountCode Customer account code
     * @return list of billing account
     */
    @GET
    @Path("/list")
    @Operation(summary = "List BillingAccount filter by customerAccountCode", tags = { "Deprecated" }, deprecated = true)
    BillingAccountsResponseDto listByCustomerAccount(@QueryParam("customerAccountCode") String customerAccountCode);

    /**
     * List BillingAccount filter by customerAccountCode.
     *
     * @param customerAccountCode Customer account code
     * @return list of billing account
     */
    @GET
    @Path("/customerAccounts/{customerAccountCode}")
    @Operation(summary = "List BillingAccount filter by customerAccountCode", tags = { "Billing account management" })
    BillingAccountsResponseDto listByCustomerAccountV2(@PathParam("customerAccountCode") String customerAccountCode);

    /**
     * List BillingAccounts matching a given criteria
     *
     * @return List of BillingAccounts
     */
    @GET
    @Path("/listGetAll")
    @Operation(summary = "List all billing accounts.",
            tags = { "Billing account management" })
    BillingAccountsResponseDto listGetAll();

    /**
     * Create or update Billing Account based on code.
     * 
     * @param postData Billing account data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    @Operation(summary = "Create or update Billing Account based on cod", tags = { "Deprecated" }, deprecated = true)
    ActionStatus createOrUpdate(BillingAccountDto postData);

    /**
     * filter counters by period date.
     *
     * @param billingAccountCode Billing account code
     * @param date Date
     * @return list of counter instances.
     */
    @GET
    @Path("/filterCountersByPeriod")
    @Operation(summary = "filter counters by period date", tags = { "Deprecated" }, deprecated = true)
    GetCountersInstancesResponseDto filterBillingAccountCountersByPeriod(@QueryParam("billingAccountCode") String billingAccountCode, @QueryParam("date") @RestDateParam Date date);


    /**
     * filter counters by period date.
     *
     * @param billingAccountCode Billing account code
     * @param date Date
     * @return list of counter instances.
     */
    @GET
    @Path("/{billingAccountCode}/filterCountersByPeriod")
    @Operation(summary = "filter counters by period date", tags = { "Billing account management" })
    GetCountersInstancesResponseDto filterBillingAccountCountersByPeriodV2(@PathParam("billingAccountCode") String billingAccountCode, @QueryParam("date") @RestDateParam Date date);
}
