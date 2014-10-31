package org.meveo.api.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.ActionStatus;
import org.meveo.api.ActionStatusEnum;
import org.meveo.api.CustomerHierarchyApi;
import org.meveo.api.dto.CustomerHierarchyDto;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.response.CustomerListResponse;
import org.meveo.api.rest.security.WSSecured;

/**
 * 
 * @author Luis Alfonso L. Mance
 * 
 */
@Path("/customer")
@RequestScoped
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Interceptors({ LoggingInterceptor.class })
@WSSecured
public class CustomerHierarchyWs extends BaseWs {

	@Inject
	private CustomerHierarchyApi customerHierarchyApi;

	/**
	 * 
	 * @param customer
	 *            entity containing values serving as filter (for "=" operator)
	 * @param limit
	 *            nb max of entity to return
	 * @param index
	 *            pagination limit
	 * @param sortField
	 *            name of the field used for sorting
	 * @return list of customer dto satisfying the filter
	 */
	@POST
	@Path("/select")
	public CustomerListResponse select(CustomerHierarchyDto customerDto,
			@QueryParam("limit") int limit, @QueryParam("index") int index,
			@QueryParam("sortField") String sortField) {
		CustomerListResponse result = new CustomerListResponse();
		try {
			customerDto.setCurrentUser(currentUser);
			result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
			result.setCustomerDtoList(customerHierarchyApi.select(customerDto,
					limit, index, sortField,currentUser));
		} catch (Exception e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}
		return result;
	}

	/*
	 * Creates the customer heirarchy including : - Trading Country - Trading
	 * Currency - Trading Language - Customer Brand - Customer Category - Seller
	 * - Customer - Customer Account - Billing Account - User Account
	 * 
	 * Required Parameters :customerId, customerBrandCode,customerCategoryCode,
	 * sellerCode
	 * ,currencyCode,countryCode,lastName,languageCode,billingCycleCode
	 */
	@POST
	@Path("/create")
	public ActionStatus create(CustomerHierarchyDto customerHeirarchyDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			customerHeirarchyDto.setCurrentUser(currentUser);
			customerHierarchyApi.createCustomerHeirarchy(customerHeirarchyDto);

		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@POST
	@Path("/update")
	public ActionStatus update(CustomerHierarchyDto customerHeirarchyDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			customerHeirarchyDto.setCurrentUser(currentUser);
			customerHierarchyApi.updateCustomerHeirarchy(customerHeirarchyDto);
		} catch (BusinessException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

}
