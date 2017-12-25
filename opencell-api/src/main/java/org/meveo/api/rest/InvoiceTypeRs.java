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
    @Path("/")
    @POST
    ActionStatus create(InvoiceTypeDto invoiceTypeDto);

    /**
     * Update invoiceType. Description per language can be defined
     * 
     * @param invoiceTypeDto invoice type to be updated
     * @return action status
     */
    @Path("/")
    @PUT
    ActionStatus update(InvoiceTypeDto invoiceTypeDto);

    /**
     * Search invoiceType with a given code.
     * 
     * @param invoiceTypeCode invoice type's code
     * @return invoice type
     */
    @Path("/")
    @GET
    GetInvoiceTypeResponse find(@QueryParam("invoiceTypeCode") String invoiceTypeCode);

    /**
     * Remove invoiceType with a given code.
     * 
     * @param invoiceTypeCode invoice type's code
     * @return action status
     */
    @Path("/{invoiceTypeCode}")
    @DELETE
    ActionStatus remove(@PathParam("invoiceTypeCode") String invoiceTypeCode);

    /**
     * Create new or update an existing invoiceType with a given code.
     * 
     * @param invoiceTypeDto The invoiceType's data
     * @return Request processing status
     */
    @Path("/createOrUpdate")
    @POST
    ActionStatus createOrUpdate(InvoiceTypeDto invoiceTypeDto);

    /**
     * List of invoiceType.
     * 
     * @return A list of invoiceType
     */
    @Path("/list")
    @GET
    GetInvoiceTypesResponse list();
}
