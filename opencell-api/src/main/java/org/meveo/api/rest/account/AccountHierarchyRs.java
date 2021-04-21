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

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.account.AccountHierarchyDto;
import org.meveo.api.dto.account.CRMAccountHierarchyDto;
import org.meveo.api.dto.account.CustomerHierarchyDto;
import org.meveo.api.dto.account.FindAccountHierachyRequestDto;
import org.meveo.api.dto.response.CustomerListResponse;
import org.meveo.api.dto.response.account.GetAccountHierarchyResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * Web service for managing account hierarchy. Account hierarchy is {@link org.meveo.model.crm.Customer}-&gt;{!link org.meveo.model.payments.CustomerAccount}-&gt;
 * {@link org.meveo.model.billing.BillingAccount}-&gt; {@link org.meveo.model.billing.UserAccount}.
 */
@Path("/account/accountHierarchy")
@Tag(name = "AccountHierarchy", description = "@%AccountHierarchy")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface AccountHierarchyRs extends IBaseRs {

    /**
     * Search for a list of customer accounts given a set of filter.
     * @param customerDto customer dto
     * @param calculateBalances  true if needs  to calculate balances
     * @return customer list.
     */
    @POST
    @Path("/find")
	@Operation(
			summary=" Search for a list of customer accounts given a set of filter. ",
			description=" Search for a list of customer accounts given a set of filter. ",
			operationId="    POST_AccountHierarchy_find",
			responses= {
				@ApiResponse(description=" customer list. ",
						content=@Content(
									schema=@Schema(
											implementation= CustomerListResponse.class
											)
								)
				)}
	)
    CustomerListResponse find(AccountHierarchyDto customerDto,  @QueryParam("calculateBalances") Boolean calculateBalances);

    /**
     * Create account hierarchy.
     * 
     * @param accountHierarchyDto account hierarchy dto
     * @return action status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create account hierarchy.  ",
			description=" Create account hierarchy.  ",
			operationId="    POST_AccountHierarchy_create",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(AccountHierarchyDto accountHierarchyDto);

    /**
     * Update account hierarchy.
     * 
     * @param accountHierarchyDto account hierachy dto
     * @return action status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update account hierarchy.  ",
			description=" Update account hierarchy.  ",
			operationId="    PUT_AccountHierarchy_update",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(AccountHierarchyDto accountHierarchyDto);

    /**
     * This service allows to create / update (if exist already) and close / terminate (if termination date is set) a list of customer, customer accounts, billing accounts, user
     * accounts, subscriptions, services, and access in one transaction. It can activate and terminate subscription and service instance. Close customer account. Terminate billing
     * and user account.
     * 
     * @param postData posted data
     * @return action status.
     */
    @POST
    @Path("/customerHierarchyUpdate")
	@Operation(
			summary=" This service allows to create / update (if exist already) and close / terminate (if termination date is set) a list of customer, customer accounts, billing accounts, user accounts, subscriptions, services, and access in one transaction",
			description=" This service allows to create / update (if exist already) and close / terminate (if termination date is set) a list of customer, customer accounts, billing accounts, user accounts, subscriptions, services, and access in one transaction. It can activate and terminate subscription and service instance. Close customer account. Terminate billing and user account.  ",
			operationId="    POST_AccountHierarchy_customerHierarchyUpdate",
			responses= {
				@ApiResponse(description=" action status. ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus customerHierarchyUpdate(CustomerHierarchyDto postData);

    /**
     * Is an update of findAccountHierarchy wherein the user can search on 1 or multiple levels of the hierarchy in 1 search. These are the modes that can be combined by using
     * bitwise - or |. Example: If we search on level=BA for lastName=legaspi and found a match, the search will return the hierarchy from BA to CUST. If we search on level=UA for
     * address1=my_address and found a match, the search will return the hierarchy from UA to CUST.", notes = "CUST = 1, CA = 2, BA = 4, UA = 8.
     * @param postData posted data to API
     * @return account hieracy response.
     * 
     */
    @POST
    @Path("/findAccountHierarchy")
	@Operation(
			summary=" Is an update of findAccountHierarchy wherein the user can search on 1 or multiple levels of the hierarchy in 1 search",
			description=" Is an update of findAccountHierarchy wherein the user can search on 1 or multiple levels of the hierarchy in 1 search. These are the modes that can be combined by using bitwise - or |. Example: If we search on level=BA for lastName=legaspi and found a match, the search will return the hierarchy from BA to CUST. If we search on level=UA for address1=my_address and found a match, the search will return the hierarchy from UA to CUST., notes = CUST = 1, CA = 2, BA = 4, UA = 8. ",
			operationId="    POST_AccountHierarchy_findAccountHierarchy",
			responses= {
				@ApiResponse(description=" account hieracy response. ",
						content=@Content(
									schema=@Schema(
											implementation= GetAccountHierarchyResponseDto.class
											)
								)
				)}
	)
    GetAccountHierarchyResponseDto findAccountHierarchy2(FindAccountHierachyRequestDto postData);

    /**
     * Create a CRMAccountHerarchy.
     * @param postData posted data
     * @return acion status
     */
    @POST
    @Path("/createCRMAccountHierarchy")
	@Operation(
			summary=" Create a CRMAccountHerarchy. ",
			description=" Create a CRMAccountHerarchy. ",
			operationId="    POST_AccountHierarchy_createCRMAccountHierarchy",
			responses= {
				@ApiResponse(description=" acion status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createCRMAccountHierarchy(CRMAccountHierarchyDto postData);

    /**
     * Update a CRM Account HerarHierarchychy.
     * @param postData posted data
     * @return acion status
     */
    @POST
    @Path("/updateCRMAccountHierarchy")
	@Operation(
			summary=" Update a CRM Account HerarHierarchychy. ",
			description=" Update a CRM Account HerarHierarchychy. ",
			operationId="    POST_AccountHierarchy_updateCRMAccountHierarchy",
			responses= {
				@ApiResponse(description=" acion status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus updateCRMAccountHierarchy(CRMAccountHierarchyDto postData);

    /**
     * Create or update a CRM Account Hierarchy.
     * @param postData posted data
     * @return acion status
     */
    @POST
    @Path("/createOrUpdateCRMAccountHierarchy")
	@Operation(
			summary=" Create or update a CRM Account Hierarchy. ",
			description=" Create or update a CRM Account Hierarchy. ",
			operationId="    POST_AccountHierarchy_createOrUpdateCRMAccountHierarchy",
			responses= {
				@ApiResponse(description=" acion status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdateCRMAccountHierarchy(CRMAccountHierarchyDto postData);

    /**
     * Create or update Account Hierarchy based on code.
     * 
     * @param accountHierarchyDto account hierarchy dto.
     * @return action status.
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create or update Account Hierarchy based on code.  ",
			description=" Create or update Account Hierarchy based on code.  ",
			operationId="    POST_AccountHierarchy_createOrUpdate",
			responses= {
				@ApiResponse(description=" action status. ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(AccountHierarchyDto accountHierarchyDto);

}
