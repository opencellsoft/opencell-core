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

package org.meveo.api.rest.billing;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.billing.*;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.billing.FindWalletOperationsResponseDto;
import org.meveo.api.dto.response.billing.GetWalletTemplateResponseDto;
import org.meveo.api.dto.response.billing.WalletBalanceResponseDto;
import org.meveo.api.rest.IBaseRs;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * Wallet operation and balance related REST API
 * 
 * @author Edward P. Legaspi
 * @author Said Ramli
 * @lastModifiedVersion 5.1
 **/
@Path("/billing/wallet")
@Tag(name = "Wallet", description = "@%Wallet")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface WalletRs extends IBaseRs {

    /**
     * Create a new wallet template
     * 
     * @param postData The wallet template's data
     * @return Request processing status
     */
    @POST
    @Path("/template")
	@Operation(
			summary=" Create a new wallet template  ",
			description=" Create a new wallet template  ",
			operationId="    POST_Wallet_template",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createWalletTemplate(WalletTemplateDto postData);

    /**
     * Update an existing wallet template
     * 
     * @param postData The wallet template's data
     * @return Request processing status
     */
    @PUT
    @Path("/template")
	@Operation(
			summary=" Update an existing wallet template  ",
			description=" Update an existing wallet template  ",
			operationId="    PUT_Wallet_template",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus updateWalletTemplate(WalletTemplateDto postData);

    /**
     * Remove an existing wallet template with a given code
     * 
     * @param walletTemplateCode The wallet template's code
     * @return Request processing status
     */
    @DELETE
    @Path("/template/{walletTemplateCode}")
	@Operation(
			summary=" Remove an existing wallet template with a given code  ",
			description=" Remove an existing wallet template with a given code  ",
			operationId="    DELETE_Wallet_template_{walletTemplateCode}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus removeWalletTemplate(@PathParam("walletTemplateCode") String walletTemplateCode);

    /**
     * Search for a wallet template with a given code
     * 
     * @param walletTemplateCode The wallet template's code
     * @return A wallet template
     */
    @GET
    @Path("/template")
	@Operation(
			summary=" Search for a wallet template with a given code  ",
			description=" Search for a wallet template with a given code  ",
			operationId="    GET_Wallet_template",
			responses= {
				@ApiResponse(description=" A wallet template ",
						content=@Content(
									schema=@Schema(
											implementation= GetWalletTemplateResponseDto.class
											)
								)
				)}
	)
    GetWalletTemplateResponseDto findWalletTemplate(@QueryParam("walletTemplateCode") String walletTemplateCode);

    /**
     * Gets the current (open or reserved) wallet balance amount at a given level and date period. In wallet operation, status='OPEN OR RESERVED'.
     * 
     * @param calculateParameters Wallet balance calculation parameters
     * @return Request processing status and balance amounts
     */
    @POST
    @Path("/balance/current")
	@Operation(
			summary=" Gets the current (open or reserved) wallet balance amount at a given level and date period. In wallet operation, status='OPEN OR RESERVED'.  ",
			description=" Gets the current (open or reserved) wallet balance amount at a given level and date period. In wallet operation, status='OPEN OR RESERVED'.  ",
			operationId="    POST_Wallet_balance_current",
			responses= {
				@ApiResponse(description=" Request processing status and balance amounts ",
						content=@Content(
									schema=@Schema(
											implementation= WalletBalanceResponseDto.class
											)
								)
				)}
	)
    WalletBalanceResponseDto currentBalance(WalletBalanceDto calculateParameters);

    /**
     * Gets the reserved wallet balance amount at a given level and date period. In wallet operation, status='RESERVED'.
     * 
     * @param calculateParameters Wallet balance calculation parameters
     * @return Request processing status and balance amounts
     */
    @POST
    @Path("/balance/reserved")
	@Operation(
			summary=" Gets the reserved wallet balance amount at a given level and date period. In wallet operation, status='RESERVED'.  ",
			description=" Gets the reserved wallet balance amount at a given level and date period. In wallet operation, status='RESERVED'.  ",
			operationId="    POST_Wallet_balance_reserved",
			responses= {
				@ApiResponse(description=" Request processing status and balance amounts ",
						content=@Content(
									schema=@Schema(
											implementation= WalletBalanceResponseDto.class
											)
								)
				)}
	)
    WalletBalanceResponseDto reservedBalance(WalletBalanceDto calculateParameters);

    /**
     * Gets the open wallet balance amount at a given level and date period. In wallet operation, status='OPEN'.
     * 
     * @param calculateParameters Wallet balance calculation parameters
     * @return Request processing status and balance amounts status
     */
    @POST
    @Path("/balance/open")
	@Operation(
			summary=" Gets the open wallet balance amount at a given level and date period. In wallet operation, status='OPEN'.  ",
			description=" Gets the open wallet balance amount at a given level and date period. In wallet operation, status='OPEN'.  ",
			operationId="    POST_Wallet_balance_open",
			responses= {
				@ApiResponse(description=" Request processing status and balance amounts status ",
						content=@Content(
									schema=@Schema(
											implementation= WalletBalanceResponseDto.class
											)
								)
				)}
	)
    WalletBalanceResponseDto openBalance(WalletBalanceDto calculateParameters);

    /**
     * Create reservation for a given offer, user account, seller, provider and date.
     * 
     * @param postData The reservation's data
     * @return Request processing status
     */
    @POST
    @Path("/reservation")
	@Operation(
			summary=" Create reservation for a given offer, user account, seller, provider and date.  ",
			description=" Create reservation for a given offer, user account, seller, provider and date.  ",
			operationId="    POST_Wallet_reservation",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createReservation(WalletReservationDto postData);

    /**
     * Updates a reservation. Same as create we just need to pass the id of the reservation.
     * 
     * @param postData The reservation's data
     * @return Request processing status
     */
    @PUT
    @Path("/reservation")
	@Operation(
			summary=" Updates a reservation. Same as create we just need to pass the id of the reservation.  ",
			description=" Updates a reservation. Same as create we just need to pass the id of the reservation.  ",
			operationId="    PUT_Wallet_reservation",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus updateReservation(WalletReservationDto postData);

    /**
     * Cancel a reservation given an id.
     * 
     * @param reservationId The reservation's id
     * @return Request processing status
     */
    @DELETE
    @Path("/reservation/{reservationId:[0-9]+}")
	@Operation(
			summary=" Cancel a reservation given an id.  ",
			description=" Cancel a reservation given an id.  ",
			operationId="    DELETE_Wallet_reservation_{reservationId:[0-9]+}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus cancelReservation(@PathParam("reservationId") Long reservationId);

    /**
     * Confirm a reservation given an id.
     * 
     * @param postData The reservation's id
     * @return Request processing status
     */
    @POST
    @Path("/reservation/confirm")
	@Operation(
			summary=" Confirm a reservation given an id.  ",
			description=" Confirm a reservation given an id.  ",
			operationId="    POST_Wallet_reservation_confirm",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus confirmReservation(WalletReservationDto postData);

    /**
     * Create a new operation
     * 
     * @param postData The operation's data
     * @return Request processing status
     */
    @POST
    @Path("/operation")
	@Operation(
			summary=" Create a new operation  ",
			description=" Create a new operation  ",
			operationId="    POST_Wallet_operation",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOperation(WalletOperationDto postData);

    /**
     * Search for an operation with a given (example) code. Deprecated in v.4.7.2
     * 
     * @param postData The operation's data (FindWalletOperationsDto)
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return List of wallet operations
     */
    @Deprecated
    @POST
    @Path("/operation/find")
	@Operation(
			summary=" Search for an operation with a given (example) code. Deprecated in v.4.7.2  ",
			description=" Search for an operation with a given (example) code. Deprecated in v.4.7.2  ",
			deprecated=true,
			operationId="    POST_Wallet_operation_find",
			responses= {
				@ApiResponse(description=" List of wallet operations ",
						content=@Content(
									schema=@Schema(
											implementation= FindWalletOperationsResponseDto.class
											)
								)
				)}
	)
    FindWalletOperationsResponseDto findOperations(FindWalletOperationsDto postData, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit,
            @DefaultValue("id") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * List wallet operations matching a given criteria
     * 
     * @param query Search criteria
     * @param fields Data retrieval options/fieldnames separated by a comma
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @param withRTs if true load the related rated transactions
     * @return List of wallet operations
     */
    @GET
    @Path("/operation/list")
	@Operation(
			summary=" List wallet operations matching a given criteria  ",
			description=" List wallet operations matching a given criteria  ",
			operationId="    GET_Wallet_operation_list",
			responses= {
				@ApiResponse(description=" List of wallet operations ",
						content=@Content(
									schema=@Schema(
											implementation= FindWalletOperationsResponseDto.class
											)
								)
				)}
	)
    FindWalletOperationsResponseDto listOperationsGet(@QueryParam("query") String query,
                                            @QueryParam("fields") String fields, 
                                            @QueryParam("offset") Integer offset,
                                            @QueryParam("limit") Integer limit, 
                                            @DefaultValue("id") @QueryParam("sortBy") String sortBy, 
                                            @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder, 
                                            @DefaultValue("false") @QueryParam("withRTs") Boolean withRTs);

    /**
     * List wallet operations matching a given criteria
     *
     * @return List of wallet operations
     */
    @GET
    @Path("/operation/listGetAll")
	@Operation(
			summary=" List wallet operations matching a given criteria ",
			description=" List wallet operations matching a given criteria ",
			operationId="    GET_Wallet_operation_listGetAll",
			responses= {
				@ApiResponse(description=" List of wallet operations ",
						content=@Content(
									schema=@Schema(
											implementation= FindWalletOperationsResponseDto.class
											)
								)
				)}
	)
    FindWalletOperationsResponseDto list( @DefaultValue("false") @QueryParam("withRTs") Boolean withRTs );

    /**
     * List wallet operations matching a given criteria
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @param withRTs if true load the related rated transactions
     * @return List of wallet operations
     */
    @POST
    @Path("/operation/list")
	@Operation(
			summary=" List wallet operations matching a given criteria  ",
			description=" List wallet operations matching a given criteria  ",
			operationId="    POST_Wallet_operation_list",
			responses= {
				@ApiResponse(description=" List of wallet operations ",
						content=@Content(
									schema=@Schema(
											implementation= FindWalletOperationsResponseDto.class
											)
								)
				)}
	)
    FindWalletOperationsResponseDto listOperationsPost(PagingAndFiltering pagingAndFiltering, @DefaultValue("false") @QueryParam("withRTs") Boolean withRTs);

    /**
     * Create new or update an existing wallet template
     * 
     * @param postData The wallet template's data
     * @return Request processing status
     */
    @POST
    @Path("/template/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing wallet template  ",
			description=" Create new or update an existing wallet template  ",
			operationId="    POST_Wallet_template_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdateWalletTemplate(WalletTemplateDto postData);
}
