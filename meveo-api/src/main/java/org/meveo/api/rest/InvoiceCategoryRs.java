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
import org.meveo.api.rest.security.RSSecured;

/**
 * Web service for managing {@link org.meveo.model.billing.InvoiceCategory}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/invoiceCategory")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface InvoiceCategoryRs extends IBaseRs {

	/**
	 * Create invoice category.
	 * 
	 * @param postData
	 * @return
	 */
	@Path("/")
	@POST
	public ActionStatus create(InvoiceCategoryDto postData);

	/**
	 * Update invoice category.
	 * 
	 * @param postData
	 * @return
	 */
	@Path("/")
	@PUT
	public ActionStatus update(InvoiceCategoryDto postData);

	/**
	 * Search invoice with a given code.
	 * 
	 * @param invoiceCategoryCode
	 * @return
	 */
	@Path("/")
	@GET
	public GetInvoiceCategoryResponse find(
			@QueryParam("invoiceCategoryCode") String invoiceCategoryCode);

	/**
	 * Remove invoice with a given code.
	 * 
	 * @param invoiceCategoryCode
	 * @return
	 */
	@Path("/{invoiceCategoryCode}")
	@DELETE
	public ActionStatus remove(
			@PathParam("invoiceCategoryCode") String invoiceCategoryCode);
	
	
	/**
	 * Create or update invoice with a given code.
	 * @param postData
	 * @return
	 */
	@Path("/createOrUpdate")
	@POST
	public ActionStatus createOrUpdate(InvoiceCategoryDto postData);
	
}
