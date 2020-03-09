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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.billing.InvoiceSequenceDto;
import org.meveo.api.dto.response.GetInvoiceSequenceResponse;
import org.meveo.api.dto.response.GetInvoiceSequencesResponse;

/**
 * Web service for managing {@link org.meveo.model.billing.InvoiceSequence}.
 * 
 * @author akadid abdelmounaim
 **/
@Path("/invoiceSequence")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface InvoiceSequenceRs extends IBaseRs {

    /**
     * Create invoiceSequence.
     * 
     * @param invoiceSequenceDto invoice Sequence to be created
     * @return action status
     */
    @POST
    @Path("/")
    ActionStatus create(InvoiceSequenceDto invoiceSequenceDto);

    /**
     * Update invoiceSequence.
     * 
     * @param invoiceSequenceDto invoice Sequence to be updated
     * @return action status
     */
    @PUT
    @Path("/")
    ActionStatus update(InvoiceSequenceDto invoiceSequenceDto);

    /**
     * Search invoiceSequence with a given code.
     * 
     * @param invoiceSequenceCode invoice type's code
     * @return invoice sequence
     */
    @GET
    @Path("/")
    GetInvoiceSequenceResponse find(@QueryParam("invoiceSequenceCode") String invoiceSequenceCode);

    /**
     * Create new or update an existing invoiceSequence with a given code.
     * 
     * @param invoiceSequenceDto The invoiceSequence's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(InvoiceSequenceDto invoiceSequenceDto);

    /**
     * List of invoiceSequence.
     * 
     * @return A list of invoiceSequence
     */
    @GET
    @Path("/list")
    GetInvoiceSequencesResponse list();
}
