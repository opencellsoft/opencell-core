package org.meveo.api.rest.payment;

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
import org.meveo.api.dto.account.CreditCategoryDto;
import org.meveo.api.dto.response.payment.CreditCategoriesResponseDto;
import org.meveo.api.dto.response.payment.CreditCategoryResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Edward P. Legaspi
 * @created 22 Aug 2017
 */
@Path("/payment/creditCategory")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface CreditCategoryRs extends IBaseRs {

	@POST
	@Path("/")
	ActionStatus create(CreditCategoryDto postData);

	@PUT
	@Path("/")
	ActionStatus update(CreditCategoryDto postData);

	@POST
	@Path("/createOrUpdate")
	ActionStatus createOrUpdate(CreditCategoryDto postData);

	@GET
	@Path("/")
	CreditCategoryResponseDto find(@QueryParam("creditCategoryCode") String creditCategoryCode);

	@GET
	@Path("/list")
	CreditCategoriesResponseDto list();

	@DELETE
	@Path("/{creditCategoryCode}")
	ActionStatus remove(@PathParam("creditCategoryCode") String creditCategoryCode);

}
