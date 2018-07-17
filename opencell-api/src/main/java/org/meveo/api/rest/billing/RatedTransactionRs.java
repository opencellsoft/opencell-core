package org.meveo.api.rest.billing;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.billing.RatedTransactionListRequestDto;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.billing.RatedTransactionListResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * RatedTransactionRs : End points for Rated Transactions REST services
 *
 * @author Said Ramli
 */
@Path("/billing/ratedTransaction")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface RatedTransactionRs  extends IBaseRs {
    
    /**
     * Get service to get a list of rated transactions.
     *
     * @param query the query
     * @param fields the fields
     * @param offset the offset
     * @param limit the limit
     * @param sortBy the sort by
     * @param sortOrder the sort order
     * @param returnUserAccountCode the return user account code
     * @return the rated transaction list response dto
     */
    @GET
    @Path("/list")
    RatedTransactionListResponseDto listGet(@QueryParam("query") String query, 
                                      @QueryParam("fields") String fields, 
                                      @QueryParam("offset") Integer offset,
                                      @QueryParam("limit") Integer limit, 
                                      @DefaultValue("code") @QueryParam("sortBy") String sortBy, 
                                      @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder,
                                      @DefaultValue("false") @QueryParam("returnUserAccountCode") Boolean returnUserAccountCode);

    /**
     * Post service to get a list of rated transactions.
     *
     * @param postData : the posted data containing the search criterion
     * @return the rated transaction list response dto
     */
    @POST
    @Path("/list")
    RatedTransactionListResponseDto listPost(RatedTransactionListRequestDto postData);

}
