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
import org.meveo.api.dto.InvoiceCategoryDto;
import org.meveo.api.dto.response.GetInvoiceCategoryResponse;

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
     * Create invoice category. Description per language can be defined
     * 
     * @param postData invoice category to be created
     * @return action status
     */
    @Path("/")
    @POST
    ActionStatus create(InvoiceCategoryDto postData);

    /**
     * Update invoice category.
     * 
     * @param postData invoice category to be updated
     * @return action status
     */
    @Path("/")
    @PUT
    ActionStatus update(InvoiceCategoryDto postData);

    /**
     * Search invoice with a given code.
     * 
     * @param invoiceCategoryCode invoice category code
     * @return invoice category
     */
    @Path("/")
    @GET
    GetInvoiceCategoryResponse find(@QueryParam("invoiceCategoryCode") String invoiceCategoryCode);

    /**
     * Remove invoice with a given code.
     * 
     * @param invoiceCategoryCode invoice category code
     * @return action status
     */
    @Path("/{invoiceCategoryCode}")
    @DELETE
    ActionStatus remove(@PathParam("invoiceCategoryCode") String invoiceCategoryCode);

    /**
     * Create or update invoice with a given code.
     * 
     * @param postData invoice category
     * @return action status
     */
    @Path("/createOrUpdate")
    @POST
    ActionStatus createOrUpdate(InvoiceCategoryDto postData);

}
