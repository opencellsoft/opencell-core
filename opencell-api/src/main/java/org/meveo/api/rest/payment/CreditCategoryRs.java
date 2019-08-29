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
 * @since 22 Aug 2017
 */
@Path("/payment/creditCategory")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface CreditCategoryRs extends IBaseRs {

    /**
     * Create a new credit category 
     *
     * @param postData The credit payment category data 
     * @return Request processing status
     */
	@POST
	@Path("/")
	ActionStatus create(CreditCategoryDto postData);

    /**
     * Update a credit category payment 
     *
     * @param postData The credit payment category data 
     * @return Request processing status
     */
	@PUT
	@Path("/")
	ActionStatus update(CreditCategoryDto postData);

    /**
     * Create or update a credit category payment 
     *
     * @param postData The credit payment category data 
     * @return Request processing status
     */
	@POST
	@Path("/createOrUpdate")
	ActionStatus createOrUpdate(CreditCategoryDto postData);

    /**
     * Get a credit category payment with a credit category code
     *
     * @param creditCategoryCode The creditCategory code 
     * @return Credit Category Response data
     */
	@GET
	@Path("/")
	CreditCategoryResponseDto find(@QueryParam("creditCategoryCode") String creditCategoryCode);

    /**
     * Retrieve the list of credit category paiement 
     *
     * @param postData The contact data 
     * @return List of Credit Categories
     */
	@GET
	@Path("/list")
	CreditCategoriesResponseDto list();

    /**
     * Delete a credit category with his given code 
     *
     * @param creditCategoryCode The creditCategory code 
     * @return Request processing status
     */
	@DELETE
	@Path("/{creditCategoryCode}")
	ActionStatus remove(@PathParam("creditCategoryCode") String creditCategoryCode);

}
