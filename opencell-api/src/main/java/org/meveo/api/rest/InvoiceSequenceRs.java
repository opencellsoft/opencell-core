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
    @Path("/")
    @POST
    ActionStatus create(InvoiceSequenceDto invoiceSequenceDto);

    /**
     * Update invoiceSequence.
     * 
     * @param invoiceSequenceDto invoice Sequence to be updated
     * @return action status
     */
    @Path("/")
    @PUT
    ActionStatus update(InvoiceSequenceDto invoiceSequenceDto);

    /**
     * Search invoiceSequence with a given code.
     * 
     * @param invoiceSequenceCode invoice type's code
     * @return invoice sequence
     */
    @Path("/")
    @GET
    GetInvoiceSequenceResponse find(@QueryParam("invoiceSequenceCode") String invoiceSequenceCode);

    /**
     * Create new or update an existing invoiceSequence with a given code.
     * 
     * @param invoiceSequenceDto The invoiceSequence's data
     * @return Request processing status
     */
    @Path("/createOrUpdate")
    @POST
    ActionStatus createOrUpdate(InvoiceSequenceDto invoiceSequenceDto);

    /**
     * List of invoiceSequence.
     * 
     * @return A list of invoiceSequence
     */
    @Path("/list")
    @GET
    GetInvoiceSequencesResponse list();
}
