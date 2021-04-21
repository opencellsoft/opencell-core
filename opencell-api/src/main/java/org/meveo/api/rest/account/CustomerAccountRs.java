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
import org.meveo.api.dto.account.CreditCategoryDto;
import org.meveo.api.dto.account.CustomerAccountDto;
import org.meveo.api.dto.account.TransferCustomerAccountDto;
import org.meveo.api.dto.response.account.CustomerAccountsResponseDto;
import org.meveo.api.dto.response.account.GetCustomerAccountResponseDto;
import org.meveo.api.dto.response.billing.GetCountersInstancesResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.serialize.RestDateParam;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;

/**
 * Web service for managing customer account.
 * 
 * @author R.AITYAAZZA
 * @author anasseh
 * @author Abdellatif BARI
 * @lastModifiedVersion 8.0.0
 */
@Path("/account/customerAccount")
@Tag(name = "CustomerAccount", description = "@%CustomerAccount")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface CustomerAccountRs extends IBaseRs {

    /**
     * Create a new customer account
     * 
     * @param postData The customer account's data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a new customer account  ",
			description=" Create a new customer account  ",
			operationId="    POST_CustomerAccount_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(CustomerAccountDto postData);

    /**
     * Update an existing customer account
     * 
     * @param postData The customer account's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing customer account  ",
			description=" Update an existing customer account  ",
			operationId="    PUT_CustomerAccount_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(CustomerAccountDto postData);

    /**
     * Search for a customer account with a given code.
     *
     * @param customerAccountCode   The customer account's code
     * @param calculateBalances     true if needs  to calculate balances
     * @param inheritCF             Should inherited custom fields be retrieved. Defaults to INHERIT_NO_MERGE.
     * @param withAccountOperations true if needs to get account operations
     * @return customer account
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search for a customer account with a given code. ",
			description=" Search for a customer account with a given code. ",
			operationId="    GET_CustomerAccount_search",
			responses= {
				@ApiResponse(description=" customer account ",
						content=@Content(
									schema=@Schema(
											implementation= GetCustomerAccountResponseDto.class
											)
								)
				)}
	)
    GetCustomerAccountResponseDto find(@QueryParam("customerAccountCode") String customerAccountCode, @QueryParam("calculateBalances") Boolean calculateBalances,
            @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF, @QueryParam("withAccountOperations") Boolean withAccountOperations);

    /**
     * Remove customerAccount with a given code.
     * 
     * @param customerAccountCode The customer account's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{customerAccountCode}")
	@Operation(
			summary=" Remove customerAccount with a given code.  ",
			description=" Remove customerAccount with a given code.  ",
			operationId="    DELETE_CustomerAccount_{customerAccountCode}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("customerAccountCode") String customerAccountCode);

    /**
     * List CustomerAccount filtered by customerCode.
     * 
     * @param customerCode The customer account's code
     * @return list of customer account by customer.
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" List CustomerAccount filtered by customerCode.  ",
			description=" List CustomerAccount filtered by customerCode.  ",
			operationId="    GET_CustomerAccount_list",
			responses= {
				@ApiResponse(description=" list of customer account by customer. ",
						content=@Content(
									schema=@Schema(
											implementation= CustomerAccountsResponseDto.class
											)
								)
				)}
	)
    CustomerAccountsResponseDto listByCustomer(@QueryParam("customerCode") String customerCode);

    /**
     * List CustomerAccounts matching a given criteria
     *
     * @return List of CustomerAccounts
     */
    @GET
    @Path("/listGetAll")
	@Operation(
			summary=" List CustomerAccounts matching a given criteria ",
			description=" List CustomerAccounts matching a given criteria ",
			operationId="    GET_CustomerAccount_listGetAll",
			responses= {
				@ApiResponse(description=" List of CustomerAccounts ",
						content=@Content(
									schema=@Schema(
											implementation= CustomerAccountsResponseDto.class
											)
								)
				)}
	)
    CustomerAccountsResponseDto listGetAll();

    /**
     * Create a new credit category.
     * 
     * @param postData The credit category's data
     * @return Request processing status
     */
    @POST
    @Path("/creditCategory")
	@Operation(
			summary=" Create a new credit category.  ",
			description=" Create a new credit category.  ",
			operationId="    POST_CustomerAccount_creditCategory",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createCreditCategory(CreditCategoryDto postData);

    /**
     * Remove credit category with a given code.
     * 
     * @param creditCategoryCode The credit category's code
     * @return Request processing status
     */
    @DELETE
    @Path("/creditCategory/{creditCategoryCode}")
	@Operation(
			summary=" Remove credit category with a given code.  ",
			description=" Remove credit category with a given code.  ",
			operationId="    DELETE_CustomerAccount_creditCategory_{creditCategoryCode}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus removeCreditCategory(@PathParam("creditCategoryCode") String creditCategoryCode);

    /**
     * Create new or update existing customer account
     * 
     * @param postData The customer account's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update existing customer account  ",
			description=" Create new or update existing customer account  ",
			operationId="    POST_CustomerAccount_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(CustomerAccountDto postData);

    /**
     * Transfer an amount from one customer to another.
     *
     * @param transferCustomerAccountDto the transfer Customer Account Dto
     * @return Request processing status
     */
    @POST
    @Path("/transferAccount")
	@Operation(
			summary=" Transfer an amount from one customer to another. ",
			description=" Transfer an amount from one customer to another. ",
			operationId="    POST_CustomerAccount_transferAccount",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus transferAccount(TransferCustomerAccountDto transferCustomerAccountDto);

    /**
     * Filter counters by period date.
     *
     * @param customerAccountCode The customer account's code
     * @param date                The date corresponding to the period
     * @return counter instances.
     */
    @GET
    @Path("/filterCountersByPeriod")
	@Operation(
			summary=" Filter counters by period date. ",
			description=" Filter counters by period date. ",
			operationId="    GET_CustomerAccount_filterCountersByPeriod",
			responses= {
				@ApiResponse(description=" counter instances. ",
						content=@Content(
									schema=@Schema(
											implementation= GetCountersInstancesResponseDto.class
											)
								)
				)}
	)
    GetCountersInstancesResponseDto filterCustomerAccountCountersByPeriod(@QueryParam("customerAccountCode") String customerAccountCode, @QueryParam("date") @RestDateParam Date date);

}
