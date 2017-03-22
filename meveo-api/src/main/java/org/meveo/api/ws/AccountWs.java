package org.meveo.api.ws;

import java.util.Date;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.CRMAccountTypeSearchDto;
import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.account.AccountHierarchyDto;
import org.meveo.api.dto.account.ApplyProductRequestDto;
import org.meveo.api.dto.account.BillingAccountDto;
import org.meveo.api.dto.account.BusinessAccountModelDto;
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
import org.meveo.api.dto.payment.LitigationRequestDto;
import org.meveo.api.dto.payment.MatchOperationRequestDto;
import org.meveo.api.dto.payment.UnMatchingOperationRequestDto;
import org.meveo.api.dto.response.CustomerListResponse;
import org.meveo.api.dto.response.TitleDto;
import org.meveo.api.dto.response.account.AccessesResponseDto;
import org.meveo.api.dto.response.account.BillingAccountsResponseDto;
import org.meveo.api.dto.response.account.BusinessAccountModelResponseDto;
import org.meveo.api.dto.response.account.CustomerAccountsResponseDto;
import org.meveo.api.dto.response.account.CustomersResponseDto;
import org.meveo.api.dto.response.account.GetAccessResponseDto;
import org.meveo.api.dto.response.account.GetAccountHierarchyResponseDto;
import org.meveo.api.dto.response.account.GetBillingAccountResponseDto;
import org.meveo.api.dto.response.account.GetCustomerAccountResponseDto;
import org.meveo.api.dto.response.account.GetCustomerResponseDto;
import org.meveo.api.dto.response.account.GetUserAccountResponseDto;
import org.meveo.api.dto.response.account.ParentEntitiesResponseDto;
import org.meveo.api.dto.response.account.TitleResponseDto;
import org.meveo.api.dto.response.account.TitlesResponseDto;
import org.meveo.api.dto.response.account.UserAccountsResponseDto;
import org.meveo.api.dto.response.billing.GetCountersInstancesResponseDto;
import org.meveo.api.dto.response.module.MeveoModuleDtosResponse;
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
    ActionStatus createOrUpdateCustomer(@WebParam(name = "customer") CustomerDto postData);

    /**
     * Retrieves a list of Customers filtered by code, customerCategory, seller, or customerBrand.
     * 
     * @param postData Contains filter parameters code, customerCategory, seller or customerBrand.
     * @return
     */
    @WebMethod
    CustomersResponseDto listCustomerWithFilter(@WebParam(name = "customer") CustomerDto postData);

    // customer brand

    @WebMethod
    ActionStatus createCustomerBrand(@WebParam(name = "customerBrand") CustomerBrandDto postData);

    @WebMethod
    ActionStatus updateCustomerBrand(@WebParam(name = "customerBrand") CustomerBrandDto postData);

    @WebMethod
    ActionStatus removeCustomerBrand(@WebParam(name = "brandCode") String brandCode);

    @WebMethod
    ActionStatus createOrUpdateCustomerBrand(@WebParam(name = "customerBrand") CustomerBrandDto postData);

    // customer category

    @WebMethod
    ActionStatus createCustomerCategory(@WebParam(name = "customerCategory") CustomerCategoryDto postData);

    @WebMethod
    ActionStatus updateCustomerCategory(@WebParam(name = "customerCategory") CustomerCategoryDto postData);

    @WebMethod
    ActionStatus createOrUpdateCustomerCategory(@WebParam(name = "customerCategory") CustomerCategoryDto postData);

    @WebMethod
    ActionStatus removeCustomerCategory(@WebParam(name = "categoryCode") String categoryCode);

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

    // credit category

    @WebMethod
    ActionStatus createCreditCategory(@WebParam(name = "creditCategory") CreditCategoryDto postData);

    @WebMethod
    ActionStatus updateCreditCategory(@WebParam(name = "creditCategory") CreditCategoryDto postData);

    @WebMethod
    ActionStatus createOrUpdateCreditCategory(@WebParam(name = "creditCategory") CreditCategoryDto postData);

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
	ActionStatus applyProduct(@WebParam(name = "applyProduct") ApplyProductRequestDto postData);

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
    GetAccountHierarchyResponseDto findAccountHierarchy2(@WebParam(name = "findAccountHierachyRequest") FindAccountHierachyRequestDto postData);

    @WebMethod
    ActionStatus updateAccountHierarchy(@WebParam(name = "accountHierarchy") AccountHierarchyDto accountHierarchyDto);

    // crm account hierarchy

    @WebMethod
    ActionStatus createCRMAccountHierarchy(@WebParam(name = "crmAccountHierarchy") CRMAccountHierarchyDto postData);

    @WebMethod
    ActionStatus updateCRMAccountHierarchy(@WebParam(name = "crmAccountHierarchy") CRMAccountHierarchyDto postData);

    @WebMethod
    ActionStatus createOrUpdateCRMAccountHierarchy(@WebParam(name = "crmAccountHierarchy") CRMAccountHierarchyDto postData);

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

    @WebMethod
    ActionStatus matchOperations(@WebParam(name = "matchOperationRequest") MatchOperationRequestDto postData);

    @WebMethod
    ActionStatus unMatchingOperations(@WebParam(name = "unMatchingOperationRequest") UnMatchingOperationRequestDto postData);

    @WebMethod
    ActionStatus addLitigation(@WebParam(name = "addLitigationRequest") LitigationRequestDto postData);

    @WebMethod
    ActionStatus cancelLitigation(@WebParam(name = "cancelLitigationRequest") LitigationRequestDto postData);

    // dunning

    @WebMethod
    ActionStatus dunningInclusionExclusion(@WebParam(name = "dunningInclusionExclusion") DunningInclusionExclusionDto dunningDto);

    // title

    @WebMethod
    ActionStatus createTitle(@WebParam(name = "title") TitleDto postData);

    @WebMethod
    TitleResponseDto findTitle(@WebParam(name = "titleCode") String titleCode);

    @WebMethod
    ActionStatus updateTitle(@WebParam(name = "title") TitleDto postData);

    @WebMethod
    ActionStatus removeTitle(@WebParam(name = "titleCode") String titleCode);

    @WebMethod
    ActionStatus createOrUpdateTitle(@WebParam(name = "title") TitleDto postData);

    @WebMethod
    TitlesResponseDto listTitle();

    // Business account model

    @WebMethod
    ActionStatus createBusinessAccountModel(@WebParam(name = "businessAccountModelDto") BusinessAccountModelDto postData);

    @WebMethod
    ActionStatus updateBusinessAccountModel(@WebParam(name = "businessAccountModelDto") BusinessAccountModelDto postData);

    @WebMethod
    BusinessAccountModelResponseDto findBusinessAccountModel(@WebParam(name = "bamCode") String bamCode);

    @WebMethod
    ActionStatus removeBusinessAccountModel(@WebParam(name = "bamCode") String bamCode);

    @WebMethod
    MeveoModuleDtosResponse listBusinessAccountModel();

    @WebMethod
    ActionStatus installBusinessAccountModel(@WebParam(name = "businessAccountModelDto") BusinessAccountModelDto postData);

    // Account Hierarchy

    @WebMethod
    ActionStatus terminateCRMAccountHierarchy(@WebParam(name = "crmAccountHierarchy") CRMAccountHierarchyDto postData);

    @WebMethod
    ActionStatus closeCRMAccountHierarchy(@WebParam(name = "crmAccountHierarchy") CRMAccountHierarchyDto postData);

    @WebMethod
    GetCountersInstancesResponseDto filterBillingAccountCountersByPeriod(@WebParam(name = "billingAccountCode") String billingAccountCode, @WebParam(name = "date") Date date);

    @WebMethod
    GetCountersInstancesResponseDto filterUserAccountCountersByPeriod(@WebParam(name = "userAccountCode") String userAccountCode, @WebParam(name = "date") Date date);

    @WebMethod
    ParentEntitiesResponseDto findParents(@WebParam(name = "parentSearchDto") CRMAccountTypeSearchDto searchDto);
}
