package org.meveo.api.rest.account;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.account.AccountHierarchyDto;
import org.meveo.api.dto.account.CRMAccountHierarchyDto;
import org.meveo.api.dto.account.CustomerHierarchyDto;
import org.meveo.api.dto.account.FindAccountHierachyRequestDto;
import org.meveo.api.dto.response.CustomerListResponse;
import org.meveo.api.dto.response.account.GetAccountHierarchyResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * Web service for managing account hierarchy. Account hierarchy is {@link org.meveo.model.crm.Customer}-&gt;{!link org.meveo.model.payments.CustomerAccount}-&gt;
 * {@link org.meveo.model.billing.BillingAccount}-&gt; {@link org.meveo.model.billing.UserAccount}.
 */
@Path("/account/accountHierarchy")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface AccountHierarchyRs extends IBaseRs {

    /**
     * Search for a list of customer accounts given a set of filter.
     * 
     * @param customerDto customer dto
     * @return customer list.
     */
    @POST
    @Path("/find")
    CustomerListResponse find(AccountHierarchyDto customerDto);

    /**
     * Create account hierarchy.
     * 
     * @param accountHierarchyDto account hierarchy dto
     * @return action status
     */
    @POST
    @Path("/")
    ActionStatus create(AccountHierarchyDto accountHierarchyDto);

    /**
     * Update account hierarchy.
     * 
     * @param accountHierarchyDto account hierachy dto
     * @return action status
     */
    @PUT
    @Path("/")
    ActionStatus update(AccountHierarchyDto accountHierarchyDto);

    /**
     * This service allows to create / update (if exist already) and close / terminate (if termination date is set) a list of customer, customer accounts, billing accounts, user
     * accounts, subscriptions, services, and access in one transaction. It can activate and terminate subscription and service instance. Close customer account. Terminate billing
     * and user account.
     * 
     * @param postData posted data
     * @return action status.
     */
    @POST
    @Path("/customerHierarchyUpdate")
    ActionStatus customerHierarchyUpdate(CustomerHierarchyDto postData);

    /**
     * Is an update of findAccountHierarchy wherein the user can search on 1 or multiple levels of the hierarchy in 1 search. These are the modes that can be combined by using
     * bitwise - or |. Example: If we search on level=BA for lastName=legaspi and found a match, the search will return the hierarchy from BA to CUST. If we search on level=UA for
     * address1=my_address and found a match, the search will return the hierarchy from UA to CUST.", notes = "CUST = 1, CA = 2, BA = 4, UA = 8.
     * @param postData posted data to API
     * @return account hieracy response.
     * 
     */
    @POST
    @Path("/findAccountHierarchy")
    GetAccountHierarchyResponseDto findAccountHierarchy2(FindAccountHierachyRequestDto postData);

    /**
     * Create a CRMAccountHerarchy.
     * @param postData posted data
     * @return acion status
     */
    @POST
    @Path("/createCRMAccountHierarchy")
    ActionStatus createCRMAccountHierarchy(CRMAccountHierarchyDto postData);

    /**
     * Update a CRM Account HerarHierarchychy.
     * @param postData posted data
     * @return acion status
     */
    @POST
    @Path("/updateCRMAccountHierarchy")
    ActionStatus updateCRMAccountHierarchy(CRMAccountHierarchyDto postData);

    /**
     * Create or update a CRM Account Hierarchy.
     * @param postData posted data
     * @return acion status
     */
    @POST
    @Path("/createOrUpdateCRMAccountHierarchy")
    ActionStatus createOrUpdateCRMAccountHierarchy(CRMAccountHierarchyDto postData);

    /**
     * Create or update Account Hierarchy based on code.
     * 
     * @param accountHierarchyDto account hierarchy dto.
     * @return action status.
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(AccountHierarchyDto accountHierarchyDto);

}
