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

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.billing.AccountingCodeDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.billing.AccountingCodeGetResponseDto;
import org.meveo.api.dto.response.billing.AccountingCodeListResponseDto;
import org.meveo.api.rest.IBaseRs;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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
    @Path("/")
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
    AccountingCodeListResponseDto listGet(@QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit, @DefaultValue("code") @QueryParam("sortBy") String sortBy,
            @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * List AccountingCodes matching a given criteria
     *
     * @return List of AccountingCodes
     */
    @GET
    @Path("/listGetAll")
    AccountingCodeListResponseDto list();

    /**
     * List AccountingCode matching the given criteria.
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return list of AccountingCode
     */
    @POST
    @Path("/list")
    AccountingCodeListResponseDto listPost(PagingAndFiltering pagingAndFiltering);

    /**
     * Removes an AccountingCode entity.
     * 
     * @param accountingCode the string to search
     * @return request processing status
     */
    @DELETE
    @Path("/{accountingCode}")
    ActionStatus remove(@PathParam("accountingCode") String accountingCode);

    /**
     * Enable a Accounting code with a given code
     * 
     * @param code Accounting code code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Accounting code with a given code
     * 
     * @param code Accounting code code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    ActionStatus disable(@PathParam("code") String code);
}