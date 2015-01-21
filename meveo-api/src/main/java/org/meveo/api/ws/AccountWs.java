package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
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

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface AccountWs extends IBaseWs {

	@WebMethod
	ActionStatus createCustomer(CustomerDto postData);

	@WebMethod
	ActionStatus updateCustomer(CustomerDto postData);

	@WebMethod
	GetCustomerResponse findCustomer(String customerCode);

	@WebMethod
	ActionStatus removeCustomer(String customerCode);

	@WebMethod
	ActionStatus createCustomerAccount(CustomerAccountDto postData);

	@WebMethod
	ActionStatus updateCustomerAccount(CustomerAccountDto postData);

	@WebMethod
	GetCustomerAccountResponse findCustomerAccount(String customerAccountCode);

	@WebMethod
	ActionStatus removeCustomerAccount(String customerAccountCode);

	@WebMethod
	ActionStatus createBillingAccount(BillingAccountDto postData);

	@WebMethod
	ActionStatus updateBillingAccount(BillingAccountDto postData);

	@WebMethod
	GetBillingAccountResponse findBillingAccount(String billingAccountCode);

	@WebMethod
	ActionStatus removeBillingAccount(String billingAccountCode);

	@WebMethod
	ActionStatus createUserAccount(UserAccountDto postData);

	@WebMethod
	ActionStatus updateUserAccount(UserAccountDto postData);

	@WebMethod
	GetUserAccountResponse findUserAccount(String userAccountCode);

	@WebMethod
	ActionStatus removeUserAccount(String userAccountCode);

	@WebMethod
	ActionStatus createAccess(AccessDto postData);

	@WebMethod
	ActionStatus updateAccess(AccessDto postData);

	@WebMethod
	GetAccessResponse findAccess(Long accessId);

	@WebMethod
	ActionStatus removeAccess(Long accessId);

}
