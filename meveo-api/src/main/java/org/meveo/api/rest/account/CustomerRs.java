package org.meveo.api.rest.account;

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
import org.meveo.api.dto.account.CustomerDto;
import org.meveo.api.dto.response.account.GetCustomerResponse;
import org.meveo.api.dto.response.account.ListCustomerResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * @author Edward P. Legaspi
 **/
@Path("/account/customer")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface CustomerRs extends IBaseRs {

	@POST
	@Path("/")
	ActionStatus create(CustomerDto postData);

	@PUT
	@Path("/")
	ActionStatus update(CustomerDto postData);

	@GET
	@Path("/")
	GetCustomerResponse find(@QueryParam("customerCode") String customerCode);

	@DELETE
	@Path("/{customerCode}")
	ActionStatus remove(@PathParam("customerCode") String customerCode);

	/**
	 * List CustomerAccount filter by customerCode.
	 * 
	 * @param customerCode
	 * @return
	 */
	@POST
	@Path("/list")
	ListCustomerResponseDto list(CustomerDto postData);

}
