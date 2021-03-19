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
import org.meveo.api.dto.InvoiceCategoryDto;
import org.meveo.api.dto.response.GetInvoiceCategoryResponse;
import org.meveo.api.dto.response.InvoiceCategoryResponseDto;
import org.meveo.api.dto.response.PagingAndFiltering;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Web service for managing {@link org.meveo.model.billing.InvoiceCategory}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/invoiceCategory")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface InvoiceCategoryRs extends IBaseRs {

    /**
     * Search for list of invoice categories.
     *
     * @return list of invoice categories
     */
    @GET
    @Path("/list")
    InvoiceCategoryResponseDto list();

    /**
     * Create invoice category. Description per language can be defined
     * 
     * @param postData invoice category to be created
     * @return action status
     */
    @POST
    @Path("/")
    ActionStatus create(InvoiceCategoryDto postData);

    /**
     * Update invoice category.
     * 
     * @param postData invoice category to be updated
     * @return action status
     */
    @PUT
    @Path("/")
    ActionStatus update(InvoiceCategoryDto postData);

    /**
     * Search invoice with a given code.
     * 
     * @param invoiceCategoryCode invoice category code
     * @return invoice category
     */
    @GET
    @Path("/")
    GetInvoiceCategoryResponse find(@QueryParam("invoiceCategoryCode") String invoiceCategoryCode);

    /**
     * Remove invoice with a given code.
     * 
     * @param invoiceCategoryCode invoice category code
     * @return action status
     */
    @DELETE
    @Path("/{invoiceCategoryCode}")
    ActionStatus remove(@PathParam("invoiceCategoryCode") String invoiceCategoryCode);

    /**
     * Create or update invoice with a given code.
     * 
     * @param postData invoice category
     * @return action status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(InvoiceCategoryDto postData);
    
    /**
     * List InvoiceCategory matching a given criteria
     *
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of InvoiceCategory
     */
    @POST
    @Path("/list")
    public InvoiceCategoryResponseDto listPost(PagingAndFiltering pagingAndFiltering);

}
