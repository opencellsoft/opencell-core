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
import org.meveo.api.rest.security.RSSecured;

/**
 * Web service for managing {@link org.meveo.model.billing.InvoiceSubCategory}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/invoiceSubCategory")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface InvoiceSubCategoryRs extends IBaseRs {

	/**
	 * Create invoice sub category.
	 * 
	 * @param postData
	 * @return
	 */
	@Path("/")
	@POST
	public ActionStatus create(InvoiceSubCategoryDto postData);

	/**
	 * Update invoice sub category.
	 * 
	 * @param postData
	 * @return
	 */
	@Path("/")
	@PUT
	public ActionStatus update(InvoiceSubCategoryDto postData);

	/**
	 * Search for invoice sub category with a given code.
	 * 
	 * @param invoiceSubCategoryCode
	 * @return
	 */
	@Path("/")
	@GET
	public GetInvoiceSubCategoryResponse find(
			@QueryParam("invoiceSubCategoryCode") String invoiceSubCategoryCode);

	/**
	 * Remove invoice sub category with a given code.
	 * 
	 * @param invoiceSubCategoryCode
	 * @return
	 */
	@Path("/{invoiceSubCategoryCode}")
	@DELETE
	public ActionStatus remove(
			@PathParam("invoiceSubCategoryCode") String invoiceSubCategoryCode);

}
