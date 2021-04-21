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
import org.meveo.api.dto.account.ApplyProductRequestDto;
import org.meveo.api.dto.account.UserAccountDto;
import org.meveo.api.dto.response.account.GetUserAccountResponseDto;
import org.meveo.api.dto.response.account.UserAccountsResponseDto;
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
@Path("/account/userAccount")
@Tag(name = "UserAccount", description = "@%UserAccount")
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
	@Operation(
			summary=" Create a new user account.  ",
			description=" Create a new user account.  ",
			operationId="    POST_UserAccount_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(UserAccountDto postData);

    /**
     * Update an existing user account
     * 
     * @param postData The user account's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing user account  ",
			description=" Update an existing user account  ",
			operationId="    PUT_UserAccount_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(UserAccountDto postData);

    /**
     * Search for a user account with a given code.
     * 
     * @param userAccountCode user account code
     * @param inheritCF Should inherited custom fields be retrieved. Defaults to INHERIT_NO_MERGE.
     * @return found user account if exist
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search for a user account with a given code.  ",
			description=" Search for a user account with a given code.  ",
			operationId="    GET_UserAccount_search",
			responses= {
				@ApiResponse(description=" found user account if exist ",
						content=@Content(
									schema=@Schema(
											implementation= GetUserAccountResponseDto.class
											)
								)
				)}
	)
    GetUserAccountResponseDto find(@QueryParam("userAccountCode") String userAccountCode,
    		@DefaultValue("false") @QueryParam("includeSubscriptions") boolean includeSubscriptions,
            @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF);

    /**
     * Remove an existing user account with a given code.
     * 
     * @param userAccountCode The user account's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{userAccountCode}")
	@Operation(
			summary=" Remove an existing user account with a given code.  ",
			description=" Remove an existing user account with a given code.  ",
			operationId="    DELETE_UserAccount_{userAccountCode}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("userAccountCode") String userAccountCode);

    /**
     * List user accounts filtered by a billing account's code.
     * 
     * @param billingAccountCode The user billing account's code
     * @return list of user accounts.
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" List user accounts filtered by a billing account's code.  ",
			description=" List user accounts filtered by a billing account's code.  ",
			operationId="    GET_UserAccount_list",
			responses= {
				@ApiResponse(description=" list of user accounts. ",
						content=@Content(
									schema=@Schema(
											implementation= UserAccountsResponseDto.class
											)
								)
				)}
	)
    UserAccountsResponseDto listByBillingAccount(@QueryParam("billingAccountCode") String billingAccountCode);

    /**
     * List user accounts matching a given criteria
     *
     * @return List of user accounts
     */
    @GET
    @Path("/listGetAll")
	@Operation(
			summary=" List user accounts matching a given criteria ",
			description=" List user accounts matching a given criteria ",
			operationId="    GET_UserAccount_listGetAll",
			responses= {
				@ApiResponse(description=" List of user accounts ",
						content=@Content(
									schema=@Schema(
											implementation= UserAccountsResponseDto.class
											)
								)
				)}
	)
    UserAccountsResponseDto listGetAll();

    /**
     * Create new or update an existing user account.
     * 
     * @param postData The user account's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing user account.  ",
			description=" Create new or update an existing user account.  ",
			operationId="    POST_UserAccount_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" Filter counters by period date. ",
			description=" Filter counters by period date. ",
			operationId="    GET_UserAccount_filterCountersByPeriod",
			responses= {
				@ApiResponse(description=" counter instances. ",
						content=@Content(
									schema=@Schema(
											implementation= GetCountersInstancesResponseDto.class
											)
								)
				)}
	)
    GetCountersInstancesResponseDto filterUserAccountCountersByPeriod(@QueryParam("userAccountCode") String userAccountCode, @QueryParam("date") @RestDateParam Date date);

    /**
     * Apply a product on a user account.
     *
     * @param postData ApplyProductRequestDto userAccount field must be set
     * @return action status.
     */
    @POST
    @Path("/applyProduct")
	@Operation(
			summary=" Apply a product on a user account. ",
			description=" Apply a product on a user account. ",
			operationId="    POST_UserAccount_applyProduct",
			responses= {
				@ApiResponse(description=" action status. ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus applyProduct(ApplyProductRequestDto postData);
}
