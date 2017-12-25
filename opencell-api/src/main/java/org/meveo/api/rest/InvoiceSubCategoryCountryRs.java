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
import org.meveo.api.dto.InvoiceSubCategoryCountryDto;
import org.meveo.api.dto.response.GetInvoiceSubCategoryCountryResponse;

/**
 * Web service for managing {@link org.meveo.model.billing.InvoiceSubcategoryCountry}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/invoiceSubCategoryCountry")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface InvoiceSubCategoryCountryRs extends IBaseRs {

    /**
     * Create invoice sub category country.
     * 
     * @param postData invoice sub category to be created
     * @return action status
     */
    @Path("/")
    @POST
    ActionStatus create(InvoiceSubCategoryCountryDto postData);

    /**
     * Update invoice sub category country.
     * 
     * @param postData invoice sub category to be updated
     * @return action status
     */
    @Path("/")
    @PUT
    ActionStatus update(InvoiceSubCategoryCountryDto postData);

    /**
     * Search invoice sub category country with a given code and country with the highest priority (1-lowest).
     * 
     * @param invoiceSubCategoryCode invoice sub category code
     * @param country country
     * @return invoice sub category if exists for given codes.
     */
    @Path("/")
    @GET
    GetInvoiceSubCategoryCountryResponse find(@QueryParam("invoiceSubCategoryCode") String invoiceSubCategoryCode, @QueryParam("country") String country);

    /**
     * Remove all the InvoiceSubCategoryCountries with a given code and country.
     * 
     * @param invoiceSubCategoryCode invoice sub category code
     * @param country country
     * @return action status
     */
    @Path("/{invoiceSubCategoryCode}/{country}")
    @DELETE
    ActionStatus remove(@PathParam("invoiceSubCategoryCode") String invoiceSubCategoryCode, @PathParam("country") String country);

    /**
     * Create or update Invoice SubCategory Country based on invoice sub-category and country.
     * 
     * @param postData invoice sub category to be created or updated
     * @return action status
     */
    @Path("/createOrUpdate")
    @POST
    ActionStatus createOrUpdate(InvoiceSubCategoryCountryDto postData);
}
