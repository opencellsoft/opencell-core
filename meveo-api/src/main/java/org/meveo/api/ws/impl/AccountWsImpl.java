package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.account.AccessApi;
import org.meveo.api.account.BillingAccountApi;
import org.meveo.api.account.CustomerAccountApi;
import org.meveo.api.account.CustomerApi;
import org.meveo.api.account.UserAccountApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.account.BillingAccountDto;
import org.meveo.api.dto.account.CustomerAccountDto;
import org.meveo.api.dto.account.CustomerDto;
import org.meveo.api.dto.account.UserAccountDto;
import org.meveo.api.dto.response.account.GetAccessResponse;
import org.meveo.api.dto.response.account.GetBillingAccountResponse;
import org.meveo.api.dto.response.account.GetCustomerAccountResponse;
import org.meveo.api.dto.response.account.GetCustomerResponse;
import org.meveo.api.dto.response.account.GetUserAccountResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.ws.AccountWs;

/**
 * @author Edward P. Legaspi
 **/
@WebService(serviceName = "AccountWs", endpointInterface = "org.meveo.api.ws.AccountWs")
@Interceptors({ LoggingInterceptor.class })
public class AccountWsImpl extends BaseWs implements AccountWs {

	@Inject
	private CustomerApi customerApi;

	@Inject
	private CustomerAccountApi customerAccountApi;

	@Inject
	private BillingAccountApi billingAccountApi;

	@Inject
	private UserAccountApi userAccountApi;

	@Inject
	private AccessApi accessApi;

	@Override
	public ActionStatus createCustomer(CustomerDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			customerApi.create(postData, getCurrentUser());
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
	public ActionStatus updateCustomer(CustomerDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			customerApi.update(postData, getCurrentUser());
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
	public GetCustomerResponse findCustomer(String customerCode) {
		GetCustomerResponse result = new GetCustomerResponse();

		try {
			result.setCustomer(customerApi.find(customerCode, getCurrentUser().getProvider()));
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
	public ActionStatus removeCustomer(String customerCode) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			customerApi.remove(customerCode, getCurrentUser().getProvider());
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
	public ActionStatus createCustomerAccount(CustomerAccountDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			customerAccountApi.create(postData, getCurrentUser());
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
	public ActionStatus updateCustomerAccount(CustomerAccountDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			customerAccountApi.update(postData, getCurrentUser());
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
	public GetCustomerAccountResponse findCustomerAccount(String customerAccountCode) {
		GetCustomerAccountResponse result = new GetCustomerAccountResponse();

		try {
			result.setCustomerAccount(customerAccountApi.find(customerAccountCode, getCurrentUser()));
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
	public ActionStatus removeCustomerAccount(String customerAccountCode) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			customerAccountApi.remove(customerAccountCode, getCurrentUser().getProvider());
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
	public ActionStatus createBillingAccount(BillingAccountDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			billingAccountApi.create(postData, getCurrentUser());
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
	public ActionStatus updateBillingAccount(BillingAccountDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			billingAccountApi.update(postData, getCurrentUser());
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
	public GetBillingAccountResponse findBillingAccount(String billingAccountCode) {
		GetBillingAccountResponse result = new GetBillingAccountResponse();

		try {
			result.setBillingAccount(billingAccountApi.find(billingAccountCode, getCurrentUser().getProvider()));
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
	public ActionStatus removeBillingAccount(String billingAccountCode) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			billingAccountApi.remove(billingAccountCode, getCurrentUser().getProvider());
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
	public ActionStatus createUserAccount(UserAccountDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			userAccountApi.create(postData, getCurrentUser());
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
	public ActionStatus updateUserAccount(UserAccountDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			userAccountApi.update(postData, getCurrentUser());
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
	public GetUserAccountResponse findUserAccount(String userAccountCode) {
		GetUserAccountResponse result = new GetUserAccountResponse();

		try {
			result.setUserAccount(userAccountApi.find(userAccountCode, getCurrentUser().getProvider()));
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
	public ActionStatus removeUserAccount(String userAccountCode) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			userAccountApi.remove(userAccountCode, getCurrentUser().getProvider());
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
	public ActionStatus createAccess(AccessDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			accessApi.create(postData, getCurrentUser());
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
	public ActionStatus updateAccess(AccessDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			accessApi.update(postData, getCurrentUser());
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
	public GetAccessResponse findAccess(Long accessId) {
		GetAccessResponse result = new GetAccessResponse();

		try {
			result.setAccess(accessApi.find(accessId, getCurrentUser().getProvider()));
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
	public ActionStatus removeAccess(Long accessId) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			accessApi.remove(accessId, getCurrentUser().getProvider());
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
