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
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.billing.RatedTransactionListResponseDto;
import org.meveo.api.rest.IBaseRs;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * Rated transactions related REST API
 *
 * @author Said Ramli
 * @author Mohamed El Youssoufi
 * @lastModifiedVersion 5.2
 */
@Path("/billing/ratedTransaction")
@Tag(name = "RatedTransaction", description = "@%RatedTransaction")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface RatedTransactionRs extends IBaseRs {

    /**
     * Get a list of rated transactions
     *
     * @param query Search criteria. Example : query=fromRange end_Date:2017-05-01|toRange end_date:2019-01-01 or query=code:abcd
     * @param fields Data retrieval options/fieldnames separated by a comma
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @param returnUserAccountCode the return user account code
     * @return A list of Rated transactions
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" Get a list of rated transactions ",
			description=" Get a list of rated transactions ",
			operationId="    GET_RatedTransaction_list",
			responses= {
				@ApiResponse(description=" A list of Rated transactions ",
						content=@Content(
									schema=@Schema(
											implementation= RatedTransactionListResponseDto.class
											)
								)
				)}
	)
    RatedTransactionListResponseDto listGet(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset,
            @QueryParam("limit") Integer limit, @DefaultValue("code") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder,
            @DefaultValue("false") @QueryParam("returnUserAccountCode") Boolean returnUserAccountCode);

    /**
     * List ratedTransactions matching a given criteria
     *
     * @return List of ratedTransactions
     */
    @GET
    @Path("/listGetAll")
	@Operation(
			summary=" List ratedTransactions matching a given criteria ",
			description=" List ratedTransactions matching a given criteria ",
			operationId="    GET_RatedTransaction_listGetAll",
			responses= {
				@ApiResponse(description=" List of ratedTransactions ",
						content=@Content(
									schema=@Schema(
											implementation= RatedTransactionListResponseDto.class
											)
								)
				)}
	)
    RatedTransactionListResponseDto list();

    /**
     * Get a list of rated transactions
     *
     * @param pagingAndFiltering Search and paging criteria. Pass "userAccountCode" as field option to retrieve associated User account's code.
     * @return A list of Rated transactions
     */
    @POST
    @Path("/list")
	@Operation(
			summary=" Get a list of rated transactions ",
			description=" Get a list of rated transactions ",
			operationId="    POST_RatedTransaction_list",
			responses= {
				@ApiResponse(description=" A list of Rated transactions ",
						content=@Content(
									schema=@Schema(
											implementation= RatedTransactionListResponseDto.class
											)
								)
				)}
	)
    RatedTransactionListResponseDto listPost(PagingAndFiltering pagingAndFiltering);

    /**
     * Call service to cancel one or many opened Rated Transactions according to the passed query, cancel an opened Rated Transaction is to set status to CANCELED.
     * 
     * @param pagingAndFiltering Search criteria
     * @return ActionStatus with SUCESS or FAIL status inside
     */
    @POST
    @Path("/cancelRatedTransactions")
	@Operation(
			summary=" Call service to cancel one or many opened Rated Transactions according to the passed query, cancel an opened Rated Transaction is to set status to CANCELED",
			description=" Call service to cancel one or many opened Rated Transactions according to the passed query, cancel an opened Rated Transaction is to set status to CANCELED.  ",
			operationId="    POST_RatedTransaction_cancelRatedTransactions",
			responses= {
				@ApiResponse(description=" ActionStatus with SUCESS or FAIL status inside ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				),@ApiResponse(responseCode = "403", description = "Only rated transactions in statuses OPEN, REJECTED, CANCELED can be cancelled")}
	)
    ActionStatus cancelRatedTransactions(PagingAndFiltering pagingAndFiltering);
}
