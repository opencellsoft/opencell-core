package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.account.AccountHierarchyDto;
import org.meveo.api.dto.account.BillingAccountDto;
import org.meveo.api.dto.account.CRMAccountHierarchyDto;
import org.meveo.api.dto.account.CreditCategoryDto;
import org.meveo.api.dto.account.CustomerAccountDto;
import org.meveo.api.dto.account.CustomerBrandDto;
import org.meveo.api.dto.account.CustomerCategoryDto;
import org.meveo.api.dto.account.CustomerDto;
import org.meveo.api.dto.account.CustomerHierarchyDto;
import org.meveo.api.dto.account.FindAccountHierachyRequestDto;
import org.meveo.api.dto.account.UserAccountDto;
import org.meveo.api.dto.payment.AccountOperationDto;
import org.meveo.api.dto.payment.DunningInclusionExclusionDto;
import org.meveo.api.dto.response.CustomerListResponse;
import org.meveo.api.dto.response.account.AccessesResponseDto;
import org.meveo.api.dto.response.account.BillingAccountsResponseDto;
import org.meveo.api.dto.response.account.CustomerAccountsResponseDto;
import org.meveo.api.dto.response.account.CustomersResponseDto;
import org.meveo.api.dto.response.account.FindAccountHierarchyResponseDto;
import org.meveo.api.dto.response.account.GetAccessResponseDto;
import org.meveo.api.dto.response.account.GetBillingAccountResponseDto;
import org.meveo.api.dto.response.account.GetCustomerAccountResponseDto;
import org.meveo.api.dto.response.account.GetCustomerResponseDto;
import org.meveo.api.dto.response.account.GetUserAccountResponseDto;
import org.meveo.api.dto.response.account.UserAccountsResponseDto;
import org.meveo.api.dto.response.payment.AccountOperationsResponseDto;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface AccountWs extends IBaseWs {

	// customer

	@WebMethod
	ActionStatus createCustomer(@WebParam(name = "customer") CustomerDto postData);

	@WebMethod
	ActionStatus updateCustomer(@WebParam(name = "customer") CustomerDto postData);

	@WebMethod
	GetCustomerResponseDto findCustomer(@WebParam(name = "customerCode") String customerCode);

	@WebMethod
	ActionStatus removeCustomer(@WebParam(name = "customerCode") String customerCode);

	@WebMethod
	CustomersResponseDto listCustomerWithFilter(@WebParam(name = "customer") CustomerDto postData);

	@WebMethod
	ActionStatus createCustomerBrand(@WebParam(name = "customerBrand") CustomerBrandDto postData);

	@WebMethod
	ActionStatus createCustomerCategory(@WebParam(name = "customerCategory") CustomerCategoryDto postData);

	@WebMethod
	ActionStatus removeCustomerBrand(@WebParam(name = "brandCode") String brandCode);

	@WebMethod
	ActionStatus removeCustomerCategory(@WebParam(name = "categoryCode") String categoryCode);
	
	@WebMethod
	ActionStatus createOrUpdateCustomer(@WebParam(name = "customer") CustomerDto postData);
	
	// customer account

	@WebMethod
	ActionStatus createCustomerAccount(@WebParam(name = "customerAccount") CustomerAccountDto postData);

	@WebMethod
	ActionStatus updateCustomerAccount(@WebParam(name = "customerAccount") CustomerAccountDto postData);

	@WebMethod
	GetCustomerAccountResponseDto findCustomerAccount(@WebParam(name = "customerAccountCode") String customerAccountCode);

	@WebMethod
	ActionStatus removeCustomerAccount(@WebParam(name = "customerAccountCode") String customerAccountCode);

	@WebMethod
	CustomerAccountsResponseDto listByCustomer(@WebParam(name = "customerCode") String customerCode);

	@WebMethod
	ActionStatus createCreditCategory(@WebParam(name = "creditCategory") CreditCategoryDto postData);

	@WebMethod
	ActionStatus removeCreditCategory(@WebParam(name = "creditCategoryCode") String creditCategoryCode);
	
	@WebMethod
	ActionStatus createOrUpdateCustomerAccount(@WebParam(name = "customerAccount") CustomerAccountDto postData);
	
	// billing account

	@WebMethod
	ActionStatus createBillingAccount(@WebParam(name = "billingAccount") BillingAccountDto postData);

	@WebMethod
	ActionStatus updateBillingAccount(@WebParam(name = "billingAccount") BillingAccountDto postData);

	@WebMethod
	GetBillingAccountResponseDto findBillingAccount(@WebParam(name = "billingAccountCode") String billingAccountCode);

	@WebMethod
	ActionStatus removeBillingAccount(@WebParam(name = "billingAccountCode") String billingAccountCode);

	@WebMethod
	BillingAccountsResponseDto listByCustomerAccount(@WebParam(name = "customerAccountCode") String customerAccountCode);
	
	@WebMethod
	ActionStatus createOrUpdateBillingAccount(@WebParam(name = "billingAccount") BillingAccountDto postData);
	
	// user account

	@WebMethod
	ActionStatus createUserAccount(@WebParam(name = "userAccount") UserAccountDto postData);

	@WebMethod
	ActionStatus updateUserAccount(@WebParam(name = "userAccount") UserAccountDto postData);

	@WebMethod
	GetUserAccountResponseDto findUserAccount(@WebParam(name = "userAccountCode") String userAccountCode);

	@WebMethod
	ActionStatus removeUserAccount(@WebParam(name = "userAccountCode") String userAccountCode);

	@WebMethod
	UserAccountsResponseDto listByBillingAccount(@WebParam(name = "billingAccountCode") String billingAccountCode);
	
	@WebMethod
	ActionStatus createOrUpdateUserAccount(@WebParam(name = "userAccount") UserAccountDto postData);
	
	// access

	@WebMethod
	ActionStatus createAccess(@WebParam(name = "access") AccessDto postData);

	@WebMethod
	ActionStatus updateAccess(@WebParam(name = "access") AccessDto postData);

	@WebMethod
	GetAccessResponseDto findAccess(@WebParam(name = "accessCode") String accessCode, @WebParam(name = "subscriptionCode") String subscriptionCode);

	@WebMethod
	ActionStatus removeAccess(@WebParam(name = "accessCode") String accessCode, @WebParam(name = "subscriptionCode") String subscriptionCode);

	@WebMethod
	AccessesResponseDto listAccess(@WebParam(name = "subscriptionCode") String subscriptionCode);
	
	@WebMethod
	ActionStatus createOrUpdateAccess(@WebParam(name = "access") AccessDto postData);

	// account hierarchy

	@WebMethod
	ActionStatus createAccountHierarchy(@WebParam(name = "accountHierarchy") AccountHierarchyDto accountHierarchyDto);

	@WebMethod
	CustomerListResponse findAccountHierarchy(@WebParam(name = "accountHierarchy") AccountHierarchyDto accountHierarchyDto);

	@WebMethod
	ActionStatus updateAccountHierarchy(@WebParam(name = "accountHierarchy") AccountHierarchyDto accountHierarchyDto);
	
	@WebMethod
	ActionStatus createCRMAccountHierarchy(@WebParam(name = "crmAccountHierarchy") CRMAccountHierarchyDto postData);
	
	@WebMethod
	ActionStatus updateCRMAccountHierarchy(@WebParam(name = "crmAccountHierarchy") CRMAccountHierarchyDto postData);
	
	@WebMethod
	ActionStatus createOrUpdateAccountHierarchy(@WebParam(name = "accountHierarchy") AccountHierarchyDto accountHierarchyDto);
	
	// customer hierarchy

	@WebMethod
	ActionStatus customerHierarchyUpdate(@WebParam(name = "customerHierarchy") CustomerHierarchyDto postData);	

	// account operation

	@WebMethod
	ActionStatus createAccountOperation(@WebParam(name = "accountOperation") AccountOperationDto postData);

	@WebMethod
	AccountOperationsResponseDto listAccountOperations(@WebParam(name = "customerAccountCode") String customerAccountCode);

	// dunning

	@WebMethod
	ActionStatus dunningInclusionExclusion(@WebParam(name = "dunningInclusionExclusion") DunningInclusionExclusionDto dunningDto);

	@WebMethod
	FindAccountHierarchyResponseDto findAccountHierarchy2(@WebParam(name = "findAccountHierachyRequest") FindAccountHierachyRequestDto postData);

}
