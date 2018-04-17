package org.meveo.api.rest.account.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.account.CustomerAccountApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.account.CreditCategoryDto;
import org.meveo.api.dto.account.CustomerAccountDto;
import org.meveo.api.dto.payment.DunningInclusionExclusionDto;
import org.meveo.api.dto.response.account.CustomerAccountsResponseDto;
import org.meveo.api.dto.response.account.GetCustomerAccountResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.account.CustomerAccountRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

/**
 * @author Edward P. Legaspi
 * 
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class CustomerAccountRsImpl extends BaseRs implements CustomerAccountRs {

    @Inject
    private CustomerAccountApi customerAccountApi;

    @Override
    public ActionStatus create(CustomerAccountDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customerAccountApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(CustomerAccountDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customerAccountApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetCustomerAccountResponseDto find(String customerAccountCode, Boolean calculateBalances, CustomFieldInheritanceEnum inheritCF) {
        GetCustomerAccountResponseDto result = new GetCustomerAccountResponseDto();

        try {
            result.setCustomerAccount(customerAccountApi.find(customerAccountCode, calculateBalances, inheritCF));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String customerAccountCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customerAccountApi.remove(customerAccountCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public CustomerAccountsResponseDto listByCustomer(String customerCode) {
        CustomerAccountsResponseDto result = new CustomerAccountsResponseDto();

        try {
            result.setCustomerAccounts(customerAccountApi.listByCustomer(customerCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus dunningInclusionExclusion(DunningInclusionExclusionDto dunningDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            customerAccountApi.dunningExclusionInclusion(dunningDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createCreditCategory(CreditCategoryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customerAccountApi.createCreditCategory(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus removeCreditCategory(String creditCategoryCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customerAccountApi.removeCreditCategory(creditCategoryCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(CustomerAccountDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customerAccountApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus close(String code) {

        ActionStatus result = new ActionStatus();

        try {
            customerAccountApi.closeAccount(code);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }
}
