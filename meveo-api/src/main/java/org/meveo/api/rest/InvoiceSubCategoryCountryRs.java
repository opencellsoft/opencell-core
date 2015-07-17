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
import org.meveo.api.rest.security.RSSecured;

/**
 * Web service for managing {@link org.meveo.model.billing.InvoiceSubcategoryCountry}.
 * @author Edward P. Legaspi
 **/
@Path("/invoiceSubCategoryCountry")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface InvoiceSubCategoryCountryRs extends IBaseRs {

	/**
	 * Create invoice sub category country.
	 * 
	 * @param postData
	 * @return
	 */
	@Path("/")
	@POST
	public ActionStatus create(InvoiceSubCategoryCountryDto postData);

	/**
	 * Update invoice sub category country.
	 * 
	 * @param postData
	 * @return
	 */
	@Path("/")
	@PUT
	public ActionStatus update(InvoiceSubCategoryCountryDto postData);

	/**
	 * Search invoice sub category country with a given code and country.
	 * 
	 * @param invoiceSubCategoryCode
	 * @param country
	 * @return
	 */
	@Path("/")
	@GET
	public GetInvoiceSubCategoryCountryResponse find(
			@QueryParam("invoiceSubCategoryCode") String invoiceSubCategoryCode,
			@QueryParam("country") String country);

	/**
	 * Remove invoice sub category country with a given code and country.
	 * 
	 * @param invoiceSubCategoryCode
	 * @param country
	 * @return
	 */
	@Path("/{invoiceSubCategoryCode}/{country}")
	@DELETE
	public ActionStatus remove(
			@PathParam("invoiceSubCategoryCode") String invoiceSubCategoryCode,
			@PathParam("country") String country);

}
