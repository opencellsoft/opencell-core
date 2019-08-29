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
import org.meveo.api.dto.InvoiceSubCategoryDto;
import org.meveo.api.dto.response.GetInvoiceSubCategoryResponse;

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

}
