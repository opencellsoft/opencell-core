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

package org.meveo.api.rest.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.account.TransferAccountOperationDto;
import org.meveo.api.dto.account.TransferOperationsDto;
import org.meveo.api.dto.payment.AccountOperationDto;
import org.meveo.api.dto.payment.LitigationRequestDto;
import org.meveo.api.dto.payment.MatchOperationRequestDto;
import org.meveo.api.dto.payment.UnMatchingOperationRequestDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.payment.AccountOperationResponseDto;
import org.meveo.api.dto.response.payment.AccountOperationsResponseDto;
import org.meveo.api.dto.response.payment.MatchedOperationsResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 8.0.0
 */
@Path("/accountOperation")
@Tag(name = "AccountOperation", description = "@%AccountOperation")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface AccountOperationRs extends IBaseRs {

    /**
     * Create a new account operation
     * 
     * @param postData The account operation's data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a new account operation  ",
			description=" Create a new account operation  ",
			operationId="    POST_AccountOperation_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(AccountOperationDto postData);

    /**
     * List account operations matching a given criteria
     * 
     * @param customerAccountCode The customer account's code. Deprecated in v. 4.7.2 Use query=userAccount.code:code instead
     * @param query Search criteria
     * @param fields Data retrieval options/fieldnames separated by a comma
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return A list of account operations
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" List account operations matching a given criteria  ",
			description=" List account operations matching a given criteria  ",
			operationId="    GET_AccountOperation_list",
			responses= {
				@ApiResponse(description=" A list of account operations ",
						content=@Content(
									schema=@Schema(
											implementation= AccountOperationsResponseDto.class
											)
								)
				)}
	)
    AccountOperationsResponseDto listGet(@Deprecated @QueryParam("customerAccountCode") String customerAccountCode, @QueryParam("query") String query,
            @QueryParam("fields") String fields, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit,
            @DefaultValue("id") @QueryParam("sortBy") String sortBy, @DefaultValue("DESCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * List account operations matching a given criteria
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of account operations
     */
    @POST
    @Path("/list")
	@Operation(
			summary=" List account operations matching a given criteria  ",
			description=" List account operations matching a given criteria  ",
			operationId="    POST_AccountOperation_list",
			responses= {
				@ApiResponse(description=" List of account operations ",
						content=@Content(
									schema=@Schema(
											implementation= AccountOperationsResponseDto.class
											)
								)
				)}
	)
    AccountOperationsResponseDto listPost(PagingAndFiltering pagingAndFiltering);

    /**
     * Match operations
     * 
     * @param postData The matching operation's data
     * @return Request processing status
     */
    @POST
    @Path("/matchOperations")
	@Operation(
			summary=" Match operations  ",
			description=" Match operations  ",
			operationId="    POST_AccountOperation_matchOperations",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus matchOperations(MatchOperationRequestDto postData);

    /**
     * Unmatching operations
     * 
     * @param postData The unmatching operations data
     * @return Request processing status
     */
    @POST
    @Path("/unMatchingOperations")
	@Operation(
			summary=" Unmatching operations  ",
			description=" Unmatching operations  ",
			operationId="    POST_AccountOperation_unMatchingOperations",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus unMatchingOperations(UnMatchingOperationRequestDto postData);

    /**
     * Add a new litigation
     * 
     * @param postData The litigation's data
     * @return Request processing status
     */
    @POST
    @Path("/addLitigation")
	@Operation(
			summary=" Add a new litigation  ",
			description=" Add a new litigation  ",
			operationId="    POST_AccountOperation_addLitigation",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus addLitigation(LitigationRequestDto postData);

    /**
     * Cancel a litigation
     * 
     * @param postData The litigation's data
     * @return Request processing status
     */
    @POST
    @Path("/cancelLitigation")
	@Operation(
			summary=" Cancel a litigation  ",
			description=" Cancel a litigation  ",
			operationId="    POST_AccountOperation_cancelLitigation",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus cancelLitigation(LitigationRequestDto postData);

    /**
     * Finds an accountOperation given an id.
     * 
     * @param id id of the account operation
     * @return Account operation response
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Finds an accountOperation given an id.  ",
			description=" Finds an accountOperation given an id.  ",
			operationId="    GET_AccountOperation_search",
			responses= {
				@ApiResponse(description=" Account operation response ",
						content=@Content(
									schema=@Schema(
											implementation= AccountOperationResponseDto.class
											)
								)
				)}
	)
    AccountOperationResponseDto find(@QueryParam("id") Long id);

    /**
     * List matched operations for a given account operation
     * 
     * @param accountOperationId Account operation identifier
     * @return A list of matched operations
     */
    @GET
    @Path("/{accountOperationId}/listMatchedOperations")
	@Operation(
			summary=" List matched operations for a given account operation  ",
			description=" List matched operations for a given account operation  ",
			operationId="    GET_AccountOperation_{accountOperationId}_listMatchedOperations",
			responses= {
				@ApiResponse(description=" A list of matched operations ",
						content=@Content(
									schema=@Schema(
											implementation= MatchedOperationsResponseDto.class
											)
								)
				)}
	)
    MatchedOperationsResponseDto listMatchedOperations(@PathParam("accountOperationId") Long accountOperationId);

    /**
     * Transfer an account operation from one customer to another.
     *
     * @param transferAccountOperationDto the transfer account operation Dto
     * @return Request processing status
     */
    @POST
    @Path("/transferAccountOperation")
	@Operation(
			summary=" Transfer an account operation from one customer to another. ",
			description=" Transfer an account operation from one customer to another. ",
			operationId="    POST_AccountOperation_transferAccountOperation",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus transferAccountOperation(TransferAccountOperationDto transferAccountOperationDto);
    
    /**
     * List accountOperations matching customer account
     * 
     * @param customerAccountCode The customer account's code.
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @return List of accountOperations
     */
    @GET
    @Path("/findByCustomerAccount")
	@Operation(
			summary=" List accountOperations matching customer account  ",
			description=" List accountOperations matching customer account  ",
			operationId="    GET_AccountOperation_findByCustomerAccount",
			responses= {
				@ApiResponse(description=" List of accountOperations ",
						content=@Content(
									schema=@Schema(
											implementation= AccountOperationsResponseDto.class
											)
								)
				)}
	)
    AccountOperationsResponseDto findByCustomerAccount(@QueryParam("customerAccountCode") String customerAccountCode, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit);

	/**
	 * update account operation's accountingDate
	 * @param id account operation identifier
	 * @param newAccountingDate account operation accountingDate
	 *
	 * @return Request processing status
	 */
	@PUT
	@Path("/{id}/updateAccountingDate/{newAccountingDate}")
	@Operation(
			summary=" Update accounting date of an account operation  ",
			description=" Update accounting date of an account operation  ",
			operationId="    PUT_AccountOperation_update",
			responses= {
					@ApiResponse(description=" Request processing status ",
							content = @Content(
									schema = @Schema(
											implementation= ActionStatus.class
									)
							)
					)}
	)
	ActionStatus updateAccountingDate(@PathParam("id") Long id, @PathParam("newAccountingDate") String newAccountingDate);
	
	/**
	 * update account operation's status
	 * @param id account operation identifier
	 * @param newStatus account operation new status
	 *
	 * @return Request processing status
	 */
	@PUT
	@Path("/{id}/status/{newStatus}")
	@Operation(
			summary=" Update status of an account operation  ",
			description=" Update status of an account operation  ",
			operationId="    PUT_AccountOperation_updateStatus",
			responses= {
					@ApiResponse(description=" Request processing status ",
							content = @Content(
									schema = @Schema(
											implementation= ActionStatus.class
									)
							)
					)}
	)
	ActionStatus updateStatus(@PathParam("id") Long id, @PathParam("newStatus") String newStatus);
	
	@POST
	@Path("/transferOperations")
	@Operation(
			summary=" transfer an AO from one account to another",
			description=" transfer an AO from one account to another",
			operationId="    POST_AccountOperation_updateStatus",
			responses= {
					@ApiResponse(description=" Request processing status ",
							content = @Content(
									schema = @Schema(
											implementation= ActionStatus.class
									)
							)
					)}
	)
	ActionStatus transferOperations(TransferOperationsDto transferOperationsDto);
}
