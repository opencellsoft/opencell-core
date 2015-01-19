package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.jws.WebService;

import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.account.AccountHierarchyApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.account.AccountHierarchyDto;
import org.meveo.api.dto.response.CustomerListResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.ws.AccountHierarchyWs;

/**
 * @author Edward P. Legaspi
 **/
@WebService(serviceName = "AccountHierarchyWs", endpointInterface = "org.meveo.api.ws.AccountHierarchyWs")
public class AccountHierarchyWsImpl extends BaseWs implements AccountHierarchyWs {

	@Inject
	private AccountHierarchyApi accountHierarchyApi;

	@Override
	public CustomerListResponse findAccountHierarchy(AccountHierarchyDto postData) {
		CustomerListResponse result = new CustomerListResponse();

		try {
			result.setCustomerDtoList(accountHierarchyApi.find(postData, getCurrentUser()));
		} catch (MeveoApiException e) {
			result.getActionStatus().setErrorCode(e.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		} catch (Exception e) {
			result.getActionStatus().setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public ActionStatus createAccountHierarchy(AccountHierarchyDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			accountHierarchyApi.create(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public ActionStatus updateAccountHierarchy(AccountHierarchyDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			accountHierarchyApi.update(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

}
