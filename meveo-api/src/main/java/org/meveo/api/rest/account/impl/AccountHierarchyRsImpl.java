package org.meveo.api.rest.account.impl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.DuplicateDefaultAccountException;
import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.account.AccountHierarchyApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.account.AccountHierarchyDto;
import org.meveo.api.dto.account.CRMAccountHierarchyDto;
import org.meveo.api.dto.account.CustomerHierarchyDto;
import org.meveo.api.dto.account.FindAccountHierachyRequestDto;
import org.meveo.api.dto.response.CustomerListResponse;
import org.meveo.api.dto.response.account.GetAccountHierarchyResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.account.AccountHierarchyRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ LoggingInterceptor.class })
@Api(value = "/account/accountHierarchy", tags = "accountHierarchy")
public class AccountHierarchyRsImpl extends BaseRs implements AccountHierarchyRs {

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
	@ApiOperation(value = "")
	public CustomerListResponse find(AccountHierarchyDto accountHierarchyDto) {
		CustomerListResponse result = new CustomerListResponse();

		try {
			result.setCustomers(accountHierarchyApi.find(accountHierarchyDto, getCurrentUser()));
		} catch (MeveoApiException e) {
			result.getActionStatus().setErrorCode(e.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
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
	@ApiOperation(value = "")
	public ActionStatus create(AccountHierarchyDto accountHierarchyDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			accountHierarchyApi.create(accountHierarchyDto, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (DuplicateDefaultAccountException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	@ApiOperation(value = "")
	public ActionStatus update(AccountHierarchyDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			accountHierarchyApi.update(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (DuplicateDefaultAccountException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	@ApiOperation(value = "What this service does: This service allows to create / update (if exist already) and close / terminate (if termination date is set) a list of customer, customer accounts, billing accounts, user accounts, subscriptions, services, and access in one transaction. It can activate and terminate subscription and service instance. Close customer account. Terminate billing and user account.")
	public ActionStatus customerHierarchyUpdate(CustomerHierarchyDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			accountHierarchyApi.customerHierarchyUpdate(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	@ApiOperation(value = "Is an update of findAccountHierarchy wherein the user can search on 1 or multiple levels of the hierarchy in 1 search. These are the modes that can be combined by using bitwise - or |. Example: If we search on level=BA for lastName=legaspi and found a match, the search will return the hierarchy from BA to CUST. If we search on level=UA for address1=my_address and found a match, the search will return the hierarchy from UA to CUST.", notes = "CUST = 1, CA = 2, BA = 4, UA = 8.")
	public GetAccountHierarchyResponseDto findAccountHierarchy2(FindAccountHierachyRequestDto postData) {
		GetAccountHierarchyResponseDto result = new GetAccountHierarchyResponseDto();
		try {
			result = accountHierarchyApi.findAccountHierarchy2(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		} catch (Exception e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	@ApiOperation(value = "")
	public ActionStatus createCRMAccountHierarchy(CRMAccountHierarchyDto postData) {
		ActionStatus result = new ActionStatus();
		try {
			accountHierarchyApi.createCRMAccountHierarchy(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	@ApiOperation(value = "")
	public ActionStatus updateCRMAccountHierarchy(CRMAccountHierarchyDto postData) {
		ActionStatus result = new ActionStatus();
		try {
			accountHierarchyApi.updateCRMAccountHierarchy(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	@ApiOperation(value = "")
	public ActionStatus createOrUpdate(AccountHierarchyDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			accountHierarchyApi.createOrUpdate(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	@ApiOperation(value = "")
	public ActionStatus createOrUpdateCRMAccountHierarchy(CRMAccountHierarchyDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try {
			accountHierarchyApi.createOrUpdateCRMAccountHierarchy(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}
}
