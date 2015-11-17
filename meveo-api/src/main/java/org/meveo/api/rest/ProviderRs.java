package org.meveo.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.meveo.api.dto.ProviderDto;
import org.meveo.api.rest.security.RSSecured;

/**
 * Web service for managing {@link org.meveo.model.Provider}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/provider")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface ProviderRs extends IBaseRs {

	/**
	 * Create provider.
	 * 
	 * @param postData
	 * @return
	 */
	@POST
	@Path("/")
	Response create(ProviderDto postData);

	/**
	 * Search for provider with a given code.
	 * 
	 * @param providerCode
	 * @return
	 */
	@GET
	@Path("/")
	Response find(@QueryParam("providerCode") String providerCode);

	/**
	 * Update provider.
	 * 
	 * @param postData
	 * @return
	 */
	@PUT
	@Path("/")
	Response update(ProviderDto postData);

	@GET
	@Path("/getTradingConfiguration")
	Response findTradingConfiguration();

	@GET
	@Path("/getInvoicingConfiguration")
	Response findInvoicingConfiguration();

	@GET
	@Path("/getCustomerConfiguration")
	Response findCustomerConfiguration();

	@GET
	@Path("/getCustomerAccountConfiguration")
	Response findCustomerAccountConfiguration();
	
	@POST
	@Path("/createOrUpdate")
	Response createOrUpdate(ProviderDto postData);
}
