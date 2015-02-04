package org.meveo.api.rest.account;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.account.AccountHierarchyDto;
import org.meveo.api.dto.account.CustomerHierarchyDto;
import org.meveo.api.dto.response.CustomerListResponse;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * Web service for managing account hierarchy. Account hierarchy is
 * {@link org.meveo.model.crm.Customer}->{!link
 * org.meveo.model.payments.CustomerAccount}->
 * {@link org.meveo.model.billing.BillingAccount}->
 * {@link org.meveo.model.billing.UserAccount}.
 */
@Path("/account/accountHierarchy")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface AccountHierarchyRs extends IBaseRs {

	/**
	 * Search for a list of customer accounts given a set of filter.
	 * 
	 * @param customerDto
	 * @return
	 */
	@POST
	@Path("/find")
	CustomerListResponse find(AccountHierarchyDto customerDto);

	/**
	 * Create account hierarchy.
	 * 
	 * @param accountHierarchyDto
	 * @return
	 */
	@POST
	@Path("/")
	ActionStatus create(AccountHierarchyDto accountHierarchyDto);

	/**
	 * Update account hierarchy.
	 * 
	 * @param accountHierarchyDto
	 * @return
	 */
	@PUT
	@Path("/")
	ActionStatus update(AccountHierarchyDto accountHierarchyDto);

	@POST
	@Path("/customerHierarchyUpdate")
	ActionStatus customerHierarchyUpdate(CustomerHierarchyDto postData);

}