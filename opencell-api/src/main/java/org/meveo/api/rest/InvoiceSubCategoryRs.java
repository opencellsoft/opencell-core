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

package org.meveo.api.rest;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.InvoiceSubCategoryDto;
import org.meveo.api.dto.response.GetInvoiceSubCategoryResponse;
import org.meveo.api.dto.response.InvoiceSubCategoryResponseDto;
import org.meveo.api.dto.response.PagingAndFiltering;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Web service for managing {@link org.meveo.model.billing.InvoiceSubCategory}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/invoiceSubCategory")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface InvoiceSubCategoryRs extends IBaseRs {

    /**
     * Search for list of invoiceSubCategories.
     *
     * @return list of invoiceSubCategories
     */
    @GET
    @Path("/list")
    InvoiceSubCategoryResponseDto list();

    /**
     * Create invoice sub category.
     * 
     * @param postData invoice sub category to be created
     * @return action status.
     */
    @POST
    @Path("/")
    ActionStatus create(InvoiceSubCategoryDto postData);

    /**
     * Update invoice sub category.
     * 
     * @param postData invoice sub category to be created
     * @return action status
     */
    @PUT
    @Path("/")
    ActionStatus update(InvoiceSubCategoryDto postData);

    /**
     * Create or update invoice sub category.
     * 
     * @param postData invoice sub category
     * @return action status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(InvoiceSubCategoryDto postData);

    /**
     * Search for invoice sub category with a given code.
     * 
     * @param invoiceSubCategoryCode invoice sub category code
     * @return invoice sub category
     */
    @GET
    @Path("/")
    GetInvoiceSubCategoryResponse find(@QueryParam("invoiceSubCategoryCode") String invoiceSubCategoryCode);

    /**
     * Remove invoice sub category with a given code.
     * 
     * @param invoiceSubCategoryCode invoice sub category
     * @return action status
     */
    @DELETE
    @Path("/{invoiceSubCategoryCode}")
    ActionStatus remove(@PathParam("invoiceSubCategoryCode") String invoiceSubCategoryCode);
    
    /**
     * List InvoiceSubCategory matching a given criteria
     *
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of InvoiceSubCategory
     */
    @POST
    @Path("/list")
    public InvoiceSubCategoryResponseDto listPost(PagingAndFiltering pagingAndFiltering);

}
