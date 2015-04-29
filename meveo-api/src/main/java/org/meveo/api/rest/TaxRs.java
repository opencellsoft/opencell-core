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
import org.meveo.api.dto.TaxDto;
import org.meveo.api.dto.response.GetTaxResponse;
import org.meveo.api.rest.security.RSSecured;

/**
 * Web service for managing {@link org.meveo.model.billing.Tax}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/tax")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface TaxRs extends IBaseRs {

	/**
	 * Create tax.
	 * 
	 * @param postData
	 * @return
	 */
	@Path("/")
	@POST
	public ActionStatus create(TaxDto postData);

	/**
	 * Update tax.
	 * @param postData
	 * @return
	 */
	@Path("/")
	@PUT
	public ActionStatus update(TaxDto postData);

	/**
	 * Search tax with a given code.
	 * @param taxCode
	 * @return
	 */
	@Path("/")
	@GET
	public GetTaxResponse find(@QueryParam("taxCode") String taxCode);

	/**
	 * Remove tax with a given code.
	 * 
	 * @param taxCode
	 * @return
	 */
	@Path("/{taxCode}")
	@DELETE
	public ActionStatus remove(@PathParam("taxCode") String taxCode);

}
