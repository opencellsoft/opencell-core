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
import org.meveo.api.dto.payment.LitigationRequestDto;
import org.meveo.api.dto.payment.MatchOperationRequestDto;
import org.meveo.api.dto.payment.UnMatchingOperationRequestDto;
import org.meveo.api.dto.response.CustomerListResponse;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
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
import org.meveo.api.dto.response.payment.AccountOperationResponseDto;
import org.meveo.api.dto.response.payment.AccountOperationsResponseDto;
import org.meveo.api.dto.response.payment.MatchedOperationsResponseDto;
import org.meveo.model.payments.PaymentMethodEnum;

/**
 * @author Edward P. Legaspi
 * @author anasseh
 * 
 * @lastModifiedVersion willBeSetHere
 **/
@WebService
public interface AccountWs extends IBaseWs {

    // customer

    /**
     * Create a new customer.
     * 
     * @param postData The customer's data
     * @return Request processing status
     */
    @WebMethod
    ActionStatus createCustomer(@WebParam(name = "customer") CustomerDto postData);

    /**
     * Update an existing customer.
     * 
     * @param postData The customer's data
     * @return Request processing status
     */
    @WebMethod
    ActionStatus updateCustomer(@WebParam(name = "customer") CustomerDto postData);

    /**
     * Search for a customer with a given code
     * 
     * @param customerCode The customer's code
     * @return The customer's data
     */
    @WebMethod
    GetCustomerResponseDto findCustomer(@WebParam(name = "customerCode") String customerCode);

    /**
     * Remove customer with a given code
     * 
     * @param customerCode The customer's code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus removeCustomer(@WebParam(name = "customerCode") String customerCode);

    @WebMethod
    ActionStatus createOrUpdateCustomer(@WebParam(name = "customer") CustomerDto postData);

    /**
     * Retrieves a list of Customers filtered by code, customerCategory, seller, or customerBrand.
     * 
     * @param postData Contains filter parameters code, customerCategory, seller or customerBrand. Deprecated in v.4.7.2 Use "pagingAndFiltering" instead
     * @param firstRow firstRow
     * @param numberOfRows number of rows
     * @param pagingAndFiltering Pagination criteria
     * @return CustomersResponseDto CustomersResponseDto
     */
    @WebMethod
    CustomersResponseDto listCustomerWithFilter(@Deprecated @WebParam(name = "customer") CustomerDto postData, @Deprecated @WebParam(name = "firstRow") Integer firstRow,
            @Deprecated @WebParam(name = "numberOfRows") Integer numberOfRows, @WebParam(name = "pagingAndFiltering") PagingAndFiltering pagingAndFiltering);

    // customer brand

    /**
     * Create a new customer brand
     * 
     * @param postData The customer brand's data
     * @return Request processing status
     */
    @WebMethod
    ActionStatus createCustomerBrand(@WebParam(name = "customerBrand") CustomerBrandDto postData);

    /**
     * Update an existing customer brand
     * 
     * @param postData The customer brand's data
     * @return Request processing status
     */
    @WebMethod
    ActionStatus updateCustomerBrand(@WebParam(name = "customerBrand") CustomerBrandDto postData);

    /**
     * Remove existing customer brand with a given brand code
     * 
     * @param brandCode The brand's code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus removeCustomerBrand(@WebParam(name = "brandCode") String brandCode);

    /**
     * Create new or update an existing customer brand
     * 
     * @param postData The customer brand's data
     * @return Request processing status
     */
    @WebMethod
    ActionStatus createOrUpdateCustomerBrand(@WebParam(name = "customerBrand") CustomerBrandDto postData);

    // customer category

    /**
     * Create a new customer category.
     * 
     * @param postData The customer category's data
     * @return Request processing status
     */
    @WebMethod
    ActionStatus createCustomerCategory(@WebParam(name = "customerCategory") CustomerCategoryDto postData);

    /**
     * Update an existing customer category
     * 
     * @param postData The customer category's data
     * @return Request processing status
     */
    @WebMethod
    ActionStatus updateCustomerCategory(@WebParam(name = "customerCategory") CustomerCategoryDto postData);

    /**
     * Create new or update an existing customer category
     * 
     * @param postData The customer category's data
     * @return Request processing status
     */
    @WebMethod
    ActionStatus createOrUpdateCustomerCategory(@WebParam(name = "customerCategory") CustomerCategoryDto postData);

    /**
     * Remove an existing customer category with a given category code
     * 
     * @param categoryCode The category's code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus removeCustomerCategory(@WebParam(name = "categoryCode") String categoryCode);

    // customer account

    /**
     * Create a new customer account
     * 
     * @param postData The customer account's data
     * @return Request processing status
     */
    @WebMethod
    ActionStatus createCustomerAccount(@WebParam(name = "customerAccount") CustomerAccountDto postData);

    /**
     * Update an existing customer account
     * 
     * @param postData The customer account's data
     * @return Request processing status
     */
    @WebMethod
    ActionStatus updateCustomerAccount(@WebParam(name = "customerAccount") CustomerAccountDto postData);

    /**
     * Search for a customer account with a given code.
     * 
     * @param customerAccountCode The customer account's code
     * @param calculateBalances true if need to calculate balances.
     * @return customer account
     */
    @WebMethod
    GetCustomerAccountResponseDto findCustomerAccount(@WebParam(name = "customerAccountCode") String customerAccountCode,
            @WebParam(name = "calculateBalances") Boolean calculateBalances);

    /**
     * Remove customerAccount with a given code.
     * 
     * @param customerAccountCode The customer account's code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus removeCustomerAccount(@WebParam(name = "customerAccountCode") String customerAccountCode);

    /**
     * List CustomerAccount filtered by customerCode.
     * 
     * @param customerCode The customer account's code
     * @return CustomerAccountsResponseDto
     */
    @WebMethod
    CustomerAccountsResponseDto listByCustomer(@WebParam(name = "customerCode") String customerCode);

    // credit category

    /**
     * Create a new credit category
     * 
     * @param postData The credit category's data
     * @return Request processing status
     */
    @WebMethod
    ActionStatus createCreditCategory(@WebParam(name = "creditCategory") CreditCategoryDto postData);

    /**
     * Update an existing credit category
     * 
     * @param postData The customer account's data
     * @return Request processing status
     */
    @WebMethod
    ActionStatus updateCreditCategory(@WebParam(name = "creditCategory") CreditCategoryDto postData);

    /**
     * Create a new credit category
     * 
     * @param postData The credit category's data
     * @return Request processing status
     */
    @WebMethod
    ActionStatus createOrUpdateCreditCategory(@WebParam(name = "creditCategory") CreditCategoryDto postData);

    /**
     * Remove credit category with a given code
     * 
     * @param creditCategoryCode The credit category's code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus removeCreditCategory(@WebParam(name = "creditCategoryCode") String creditCategoryCode);

    /**
     * Create new or update existing customer account
     * 
     * @param postData The customer account's data
     * @return Request processing status
     */
    @WebMethod
    ActionStatus createOrUpdateCustomerAccount(@WebParam(name = "customerAccount") CustomerAccountDto postData);

    // billing account

    /**
     * Create a new billing account.
     * 
     * @param postData Billing account data
     * @return Request processing status
     */
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

    /**
     * Enable an Access point with a given access code and subscription code.
     * 
     * @param accessCode Access code
     * @param subscriptionCode Subscription code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableAccess(@WebParam(name = "accessCode") String accessCode, @WebParam(name = "subscriptionCode") String subscriptionCode);

    /**
     * Disable an Access point with a given access code and subscription code.
     * 
     * @param accessCode Access code
     * @param subscriptionCode Subscription code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableAccess(@WebParam(name = "accessCode") String accessCode, @WebParam(name = "subscriptionCode") String subscriptionCode);

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

    /**
     * Find account operations matching a given search criteria
     * 
     * @param customerAccountCode Customer account code. Deprecated in v.4.7.2. Use pagingAndFiltering instead
     * @param sortBy Sort by. Deprecated in v.4.7.2. Use pagingAndFiltering instead
     * @param sortOrder Sort order. Deprecated in v.4.7.2. Use pagingAndFiltering instead
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return A list of account operations
     */
    @WebMethod
    AccountOperationsResponseDto listAccountOperations(@Deprecated @WebParam(name = "customerAccountCode") String customerAccountCode,
            @Deprecated @WebParam(name = "sortBy") String sortBy, @Deprecated @WebParam(name = "sortOrder") SortOrder sortOrder,
            @WebParam(name = "pagingAndFiltering") PagingAndFiltering pagingAndFiltering);

    @WebMethod
    ActionStatus matchOperations(@WebParam(name = "matchOperationRequest") MatchOperationRequestDto postData);

    @WebMethod
    ActionStatus unMatchingOperations(@WebParam(name = "unMatchingOperationRequest") UnMatchingOperationRequestDto postData);

    /**
     * List matched operations for a given account operation
     * 
     * @param accountOperationId Account operation identifier
     * @return A list of matched operations
     */
    @WebMethod
    public MatchedOperationsResponseDto listMatchedOperations(@WebParam(name = "accountOperationId") Long accountOperationId);

    @WebMethod
    ActionStatus addLitigation(@WebParam(name = "addLitigationRequest") LitigationRequestDto postData);

    @WebMethod
    ActionStatus cancelLitigation(@WebParam(name = "cancelLitigationRequest") LitigationRequestDto postData);

    @WebMethod
    AccountOperationResponseDto findAccountOperation(@WebParam(name = "id") Long id);

    @WebMethod
    ActionStatus updatePaymentMethod(@WebParam(name = "customerAccountCode") String customerAccountCode, @WebParam(name = "aoId") Long aoId,
            @WebParam(name = "paymentMethod") PaymentMethodEnum paymentMethod);

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

    /**
     * Enable an Business Account model by its code
     * 
     * @param code Business Account model code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableBusinessAccountModel(@WebParam(name = "code") String code);

    /**
     * Disable an Business Account model by its code
     * 
     * @param code Business Account model code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableBusinessAccountModel(@WebParam(name = "code") String code);

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
