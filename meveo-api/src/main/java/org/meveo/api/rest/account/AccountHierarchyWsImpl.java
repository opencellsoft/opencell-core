package org.meveo.api.rest.account;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.account.AccountHierarchyApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.account.AccountHierarchyDto;
import org.meveo.api.dto.response.CustomerListResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.BaseWs;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ LoggingInterceptor.class })
public class AccountHierarchyWsImpl extends BaseWs implements
		AccountHierarchyWs {

	@Inject
	private AccountHierarchyApi accountHierarchyApi;

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
	@Override
	public CustomerListResponse find(AccountHierarchyDto customerDto) {
		CustomerListResponse result = new CustomerListResponse();

		try {
			result.setCustomerDtoList(accountHierarchyApi.find(customerDto,
					getCurrentUser()));
		} catch (MeveoApiException e) {
			result.getActionStatus().setErrorCode(e.getErrorCode());
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
	@Override
	public ActionStatus create(AccountHierarchyDto customerHeirarchyDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			accountHierarchyApi.createAccountHierarchy(customerHeirarchyDto,
					getCurrentUser());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public ActionStatus update(AccountHierarchyDto customerHeirarchyDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			accountHierarchyApi.updateCustomerHeirarchy(customerHeirarchyDto,
					getCurrentUser());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

}