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

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.account.BillingAccountDto;
import org.meveo.api.dto.response.account.BillingAccountsResponseDto;
import org.meveo.api.dto.response.account.GetBillingAccountResponseDto;
import org.meveo.api.dto.response.billing.GetCountersInstancesResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.serialize.RestDateParam;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;

/**
 * @author Edward P. Legaspi
 **/
@Path("/account/billingAccount")
@Tag(name = "BillingAccount", description = "@%BillingAccount")
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
	@Operation(
			summary=" Create a new billing account.  ",
			description=" Create a new billing account.  ",
			operationId="    POST_BillingAccount_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(BillingAccountDto postData);

    /**
     * Update existing billing account.
     * 
     * @param postData Billing account data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update existing billing account.  ",
			description=" Update existing billing account.  ",
			operationId="    PUT_BillingAccount_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(BillingAccountDto postData);

    /**
     * Search for a billing account with a given code.
     * 
     * @param billingAccountCode Billing account code
     * @param inheritCF Should inherited custom fields be retrieved. Defaults to INHERIT_NO_MERGE.
     * @return Billing account
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search for a billing account with a given code.  ",
			description=" Search for a billing account with a given code.  ",
			operationId="    GET_BillingAccount_search",
			responses= {
				@ApiResponse(description=" Billing account ",
						content=@Content(
									schema=@Schema(
											implementation= GetBillingAccountResponseDto.class
											)
								)
				)}
	)
    GetBillingAccountResponseDto find(@QueryParam("billingAccountCode") String billingAccountCode,
            @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF);

    /**
     * Remove a billing account with a Billing Account Code.
     *
     * @param billingAccountCode Billing account code
     * @return Request processing status
     */
    @DELETE
    @Path("/{billingAccountCode}")
	@Operation(
			summary=" Remove a billing account with a Billing Account Code. ",
			description=" Remove a billing account with a Billing Account Code. ",
			operationId="    DELETE_BillingAccount_{billingAccountCode}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("billingAccountCode") String billingAccountCode);

    /**
     * List BillingAccount filter by customerAccountCode.
     * 
     * @param customerAccountCode Customer account code
     * @return  list of billing account
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" List BillingAccount filter by customerAccountCode.  ",
			description=" List BillingAccount filter by customerAccountCode.  ",
			operationId="    GET_BillingAccount_list",
			responses= {
				@ApiResponse(description="  list of billing account ",
						content=@Content(
									schema=@Schema(
											implementation= BillingAccountsResponseDto.class
											)
								)
				)}
	)
    BillingAccountsResponseDto listByCustomerAccount(@QueryParam("customerAccountCode") String customerAccountCode);

    /**
     * List BillingAccounts matching a given criteria
     *
     * @return List of BillingAccounts
     */
    @GET
    @Path("/listGetAll")
	@Operation(
			summary=" List BillingAccounts matching a given criteria ",
			description=" List BillingAccounts matching a given criteria ",
			operationId="    GET_BillingAccount_listGetAll",
			responses= {
				@ApiResponse(description=" List of BillingAccounts ",
						content=@Content(
									schema=@Schema(
											implementation= BillingAccountsResponseDto.class
											)
								)
				)}
	)
    BillingAccountsResponseDto listGetAll();

    /**
     * Create or update Billing Account based on code.
     * 
     * @param postData Billing account data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create or update Billing Account based on code.  ",
			description=" Create or update Billing Account based on code.  ",
			operationId="    POST_BillingAccount_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" filter counters by period date. ",
			description=" filter counters by period date. ",
			operationId="    GET_BillingAccount_filterCountersByPeriod",
			responses= {
				@ApiResponse(description=" list of counter instances. ",
						content=@Content(
									schema=@Schema(
											implementation= GetCountersInstancesResponseDto.class
											)
								)
				)}
	)
    GetCountersInstancesResponseDto filterBillingAccountCountersByPeriod(@QueryParam("billingAccountCode") String billingAccountCode, @QueryParam("date") @RestDateParam Date date);
}
