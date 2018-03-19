package org.meveo.api.rest.billing;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.billing.AccountingCodeDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.billing.AccountingCodeGetResponseDto;
import org.meveo.api.dto.response.billing.AccountingCodeListResponse;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Edward P. Legaspi
 * @version 23 Feb 2018
 * @lastModifiedVersion 5.0
 **/
@Path("/billing/accountingCode")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface AccountingCodeRs extends IBaseRs {

    /**
     * Creates a new AccountingCode.
     * 
     * @param postData
     * @return
     */
    @POST
    @Path("/")
    ActionStatus create(AccountingCodeDto postData);

    /**
     * Updates AccountingCode.
     * 
     * @param postData
     * @return
     */
    @PUT
    @Path("/")
    ActionStatus update(AccountingCodeDto postData);

    /**
     * Create or update an AccountingCode. Checks if the code already exists.
     * 
     * @param postData
     * @return
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(AccountingCodeDto postData);

    /**
     * Finds an AccountingCode.
     * 
     * @param accountingCode
     * @return
     */
    @GET
    @Path("/{accountingCode}")
    AccountingCodeGetResponseDto find(@QueryParam("accountingCode") String accountingCode);

    @GET
    @Path("/list")
    AccountingCodeListResponse listGet(@QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit, @DefaultValue("code") @QueryParam("sortBy") String sortBy,
            @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    @POST
    @Path("/list")
    AccountingCodeListResponse listPost(PagingAndFiltering pagingAndFiltering);

    /**
     * Removes an AccountingCode entity.
     * 
     * @param accountingCode
     * @return
     */
    @DELETE
    @Path("/{accountingCode}")
    ActionStatus remove(@PathParam("accountingCode") String accountingCode);

}
