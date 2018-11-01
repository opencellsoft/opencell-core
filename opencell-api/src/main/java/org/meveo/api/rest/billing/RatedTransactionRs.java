package org.meveo.api.rest.billing;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.billing.RatedTransactionListResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * Rated transactions related REST API
 *
 * @author Said Ramli
 * @author Mohamed El Youssoufi
 * @lastModifiedVersion 5.2
 */
@Path("/billing/ratedTransaction")
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
    RatedTransactionListResponseDto listGet(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset,
            @QueryParam("limit") Integer limit, @DefaultValue("code") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder,
            @DefaultValue("false") @QueryParam("returnUserAccountCode") Boolean returnUserAccountCode);

    /**
     * Get a list of rated transactions
     *
     * @param pagingAndFiltering Search and paging criteria. Pass "userAccountCode" as field option to retrieve associated User account's code.
     * @return A list of Rated transactions
     */
    @POST
    @Path("/list")
    RatedTransactionListResponseDto listPost(PagingAndFiltering pagingAndFiltering);

    /**
     * Call service to cancel one or many opened Rated Transactions according to the passed query, cancel an opened Rated Transaction is to set status to CANCELED.
     * 
     * @param pagingAndFiltering Search criteria
     * @return ActionStatus with SUCESS or FAIL status inside
     */
    @POST
    @Path("/cancelRatedTransactions")
    ActionStatus cancelRatedTransactions(PagingAndFiltering pagingAndFiltering);
}