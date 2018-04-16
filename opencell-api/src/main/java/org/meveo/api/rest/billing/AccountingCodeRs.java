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
 * API for managing AccountingCode entity.
 * 
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
     * @param postData object representation of AccountingCode
     * @return request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(AccountingCodeDto postData);

    /**
     * Updates AccountingCode. An existing AccountingCode is search using the code field. 
     * 
     * @param postData object representation of AccountingCode
     * @return request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(AccountingCodeDto postData);

    /**
     * Create or update an AccountingCode. Checks if the code already exists.
     * 
     * @param postData object representation of AccountingCode
     * @return request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(AccountingCodeDto postData);

    /**
     * Finds an AccountingCode.
     * 
     * @param accountingCode the string to search
     * @return request processing status
     */
    @GET
    @Path("/{accountingCode}")
    AccountingCodeGetResponseDto find(@QueryParam("accountingCode") String accountingCode);

    /**
     * List AccountingCode matching the given criteria.
     * 
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return list of AccountingCode
     */
    @GET
    @Path("/list")
    AccountingCodeListResponse listGet(@QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit, @DefaultValue("code") @QueryParam("sortBy") String sortBy,
            @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * List AccountingCode matching the given criteria.
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return list of AccountingCode
     */
    @POST
    @Path("/list")
    AccountingCodeListResponse listPost(PagingAndFiltering pagingAndFiltering);

    /**
     * Removes an AccountingCode entity.
     * 
     * @param accountingCode the string to search
     * @return request processing status
     */
    @DELETE
    @Path("/{accountingCode}")
    ActionStatus remove(@PathParam("accountingCode") String accountingCode);

}
