package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.account.AccountHierarchyDto;
import org.meveo.api.dto.account.BillingAccountDto;
import org.meveo.api.dto.account.CustomerAccountDto;
import org.meveo.api.dto.account.CustomerDto;
import org.meveo.api.dto.account.UserAccountDto;
import org.meveo.api.dto.response.CustomerListResponse;
import org.meveo.api.dto.response.account.GetAccessResponse;
import org.meveo.api.dto.response.account.GetBillingAccountResponse;
import org.meveo.api.dto.response.account.GetCustomerAccountResponse;
import org.meveo.api.dto.response.account.GetCustomerResponse;
import org.meveo.api.dto.response.account.GetListAccessResponse;
import org.meveo.api.dto.response.account.GetUserAccountResponse;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface AccountWs extends IBaseWs {

	@WebMethod
	ActionStatus createCustomer(@WebParam(name = "customer") CustomerDto postData);

	@WebMethod
	ActionStatus updateCustomer(@WebParam(name = "customer") CustomerDto postData);

	@WebMethod
	GetCustomerResponse findCustomer(@WebParam(name = "customerCode") String customerCode);

	@WebMethod
	ActionStatus removeCustomer(@WebParam(name = "customerCode") String customerCode);

	@WebMethod
	ActionStatus createCustomerAccount(@WebParam(name = "customerAccount") CustomerAccountDto postData);

	@WebMethod
	ActionStatus updateCustomerAccount(@WebParam(name = "customerAccount") CustomerAccountDto postData);

	@WebMethod
	GetCustomerAccountResponse findCustomerAccount(@WebParam(name = "customerAccountCode") String customerAccountCode);

	@WebMethod
	ActionStatus removeCustomerAccount(@WebParam(name = "customerAccountCode") String customerAccountCode);

	@WebMethod
	ActionStatus createBillingAccount(@WebParam(name = "billingAccount") BillingAccountDto postData);

	@WebMethod
	ActionStatus updateBillingAccount(@WebParam(name = "billingAccount") BillingAccountDto postData);

	@WebMethod
	GetBillingAccountResponse findBillingAccount(@WebParam(name = "billingAccountCode") String billingAccountCode);

	@WebMethod
	ActionStatus removeBillingAccount(@WebParam(name = "billingAccountCode") String billingAccountCode);

	@WebMethod
	ActionStatus createUserAccount(@WebParam(name = "userAccount") UserAccountDto postData);

	@WebMethod
	ActionStatus updateUserAccount(@WebParam(name = "userAccount") UserAccountDto postData);

	@WebMethod
	GetUserAccountResponse findUserAccount(@WebParam(name = "userAccountCode") String userAccountCode);

	@WebMethod
	ActionStatus removeUserAccount(@WebParam(name = "userAccountCode") String userAccountCode);

	@WebMethod
	ActionStatus createAccess(@WebParam(name = "access") AccessDto postData);

	@WebMethod
	ActionStatus updateAccess(@WebParam(name = "access") AccessDto postData);

	@WebMethod
	GetAccessResponse findAccess(@WebParam(name = "accessCode") String accessCode,
			@WebParam(name = "subscriptionCode") String subscriptionCode);

	@WebMethod
	ActionStatus removeAccess(@WebParam(name = "accessCode") String accessCode,
			@WebParam(name = "subscriptionCode") String subscriptionCode);

	@WebMethod
	public CustomerListResponse findAccountHierarchy(
			@WebParam(name = "accountHierarchy") AccountHierarchyDto accountHierarchyDto);

	@WebMethod
	public ActionStatus createAccountHierarchy(
			@WebParam(name = "accountHierarchy") AccountHierarchyDto accountHierarchyDto);

	@WebMethod
	public ActionStatus updateAccountHierarchy(
			@WebParam(name = "accountHierarchy") AccountHierarchyDto accountHierarchyDto);

	@WebMethod
	GetListAccessResponse listAccess(@WebParam(name = "subscriptionCode") String subscriptionCode);

}
