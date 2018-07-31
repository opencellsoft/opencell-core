package org.meveo.api.rest.account;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
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
import org.meveo.api.dto.account.CustomerAccountDto;
import org.meveo.api.dto.response.account.CustomerAccountsResponseDto;
import org.meveo.api.dto.response.account.GetCustomerAccountResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

/**
 * Web service for managing customer account.
 * 
 * @author R.AITYAAZZA
 * @author anasseh
 * 
 * @lastModifiedVersion willBeSetHere
 */
@Path("/account/customerAccount")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface CustomerAccountRs extends IBaseRs {

    /**
     * Create a new customer account
     * 
     * @param postData The customer account's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(CustomerAccountDto postData);

    /**
     * Update an existing customer account
     * 
     * @param postData The customer account's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(CustomerAccountDto postData);

    /**
     * Search for a customer account with a given code.
     * 
     * @param customerAccountCode The customer account's code
     * @param calculateBalances  true if needs  to calculate balances
     * @param inheritCF Custom field inheritance type
     * @return customer account
     */
    @GET
    @Path("/")
    GetCustomerAccountResponseDto find(@QueryParam("customerAccountCode") String customerAccountCode, @QueryParam("calculateBalances") Boolean calculateBalances,
            @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF);

    /**
     * Remove customerAccount with a given code.
     * 
     * @param customerAccountCode The customer account's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{customerAccountCode}")
    ActionStatus remove(@PathParam("customerAccountCode") String customerAccountCode);

    /**
     * List CustomerAccount filtered by customerCode.
     * 
     * @param customerCode The customer account's code
     * @return list of customer account by customer.
     */
    @GET
    @Path("/list")
    CustomerAccountsResponseDto listByCustomer(@QueryParam("customerCode") String customerCode);

    /**
     * Create a new credit category.
     * 
     * @param postData The credit category's data
     * @return Request processing status
     */
    @POST
    @Path("/creditCategory")
    ActionStatus createCreditCategory(CreditCategoryDto postData);

    /**
     * Remove credit category with a given code.
     * 
     * @param creditCategoryCode The credit category's code
     * @return Request processing status
     */
    @DELETE
    @Path("/creditCategory/{creditCategoryCode}")
    ActionStatus removeCreditCategory(@PathParam("creditCategoryCode") String creditCategoryCode);

    /**
     * Create new or update existing customer account
     * 
     * @param postData The customer account's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(CustomerAccountDto postData);
}
