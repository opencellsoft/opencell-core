package org.meveo.api.rest.account.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.account.AccountHierarchyApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.account.AccountHierarchyDto;
import org.meveo.api.dto.account.CRMAccountHierarchyDto;
import org.meveo.api.dto.account.CustomerHierarchyDto;
import org.meveo.api.dto.account.FindAccountHierachyRequestDto;
import org.meveo.api.dto.response.CustomerListResponse;
import org.meveo.api.dto.response.account.GetAccountHierarchyResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.account.AccountHierarchyRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class AccountHierarchyRsImpl extends BaseRs implements AccountHierarchyRs {

    @Inject
    private AccountHierarchyApi accountHierarchyApi;

    /**
     * 
     * @param accountHierarchyDto account hierarchy dto.
     * @param calculateBalances  true if needs  to calculate balances
     * @return list of customer dto satisfying the filter
     */
    @Override
    public CustomerListResponse find(AccountHierarchyDto accountHierarchyDto, Boolean calculateBalances) {
        CustomerListResponse result = new CustomerListResponse();

        try {
            result.setCustomers(accountHierarchyApi.find(accountHierarchyDto, calculateBalances));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    /*
     * Creates the customer hierarchy including : - Trading Country - Trading Currency - Trading Language - Customer Brand - Customer Category - Seller - Customer - Customer
     * Account - Billing Account - User Account
     * 
     * Required Parameters :customerId, customerCategoryCode, sellerCode ,currencyCode,countryCode,lastName if title is provided,languageCode,billingCycleCode
     */
    @Override
    public ActionStatus create(AccountHierarchyDto accountHierarchyDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            accountHierarchyApi.create(accountHierarchyDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(AccountHierarchyDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            accountHierarchyApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus customerHierarchyUpdate(CustomerHierarchyDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            accountHierarchyApi.customerHierarchyUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetAccountHierarchyResponseDto findAccountHierarchy2(FindAccountHierachyRequestDto postData) {
        GetAccountHierarchyResponseDto result = new GetAccountHierarchyResponseDto();
        try {
            result = accountHierarchyApi.findAccountHierarchy2(postData);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createCRMAccountHierarchy(CRMAccountHierarchyDto postData) {
        ActionStatus result = new ActionStatus();
        try {
            accountHierarchyApi.createCRMAccountHierarchy(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateCRMAccountHierarchy(CRMAccountHierarchyDto postData) {
        ActionStatus result = new ActionStatus();
        try {
            accountHierarchyApi.updateCRMAccountHierarchy(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(AccountHierarchyDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            accountHierarchyApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateCRMAccountHierarchy(CRMAccountHierarchyDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            accountHierarchyApi.createOrUpdateCRMAccountHierarchy(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}
