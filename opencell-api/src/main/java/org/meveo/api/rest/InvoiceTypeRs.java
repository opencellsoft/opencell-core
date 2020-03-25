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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.billing.InvoiceTypeDto;
import org.meveo.api.dto.response.GetInvoiceTypeResponse;
import org.meveo.api.dto.response.GetInvoiceTypesResponse;

/**
 * Web service for managing {@link org.meveo.model.billing.InvoiceType}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/invoiceType")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface InvoiceTypeRs extends IBaseRs {

    /**
     * Create invoiceType. Description per language can be defined
     * 
     * @param invoiceTypeDto invoice type to be created
     * @return action status
     */
    @POST
    @Path("/")
    ActionStatus create(InvoiceTypeDto invoiceTypeDto);

    /**
     * Update invoiceType. Description per language can be defined
     * 
     * @param invoiceTypeDto invoice type to be updated
     * @return action status
     */
    @PUT
    @Path("/")
    ActionStatus update(InvoiceTypeDto invoiceTypeDto);

    /**
     * Search invoiceType with a given code.
     * 
     * @param invoiceTypeCode invoice type's code
     * @return invoice type
     */
    @GET
    @Path("/")
    GetInvoiceTypeResponse find(@QueryParam("invoiceTypeCode") String invoiceTypeCode);

    /**
     * Remove invoiceType with a given code.
     * 
     * @param invoiceTypeCode invoice type's code
     * @return action status
     */
    @DELETE
    @Path("/{invoiceTypeCode}")
    ActionStatus remove(@PathParam("invoiceTypeCode") String invoiceTypeCode);

    /**
     * Create new or update an existing invoiceType with a given code.
     * 
     * @param invoiceTypeDto The invoiceType's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(InvoiceTypeDto invoiceTypeDto);

    /**
     * List of invoiceType.
     * 
     * @return A list of invoiceType
     */
    @GET
    @Path("/list")
    GetInvoiceTypesResponse list();
}
