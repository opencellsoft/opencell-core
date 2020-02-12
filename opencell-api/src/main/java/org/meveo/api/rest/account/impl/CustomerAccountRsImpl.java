package org.meveo.api.rest.account.impl;

import org.meveo.api.account.CustomerAccountApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.account.CreditCategoryDto;
import org.meveo.api.dto.account.CustomerAccountDto;
import org.meveo.api.dto.account.TransferCustomerAccountDto;
import org.meveo.api.dto.billing.CounterInstanceDto;
import org.meveo.api.dto.response.account.CustomerAccountsResponseDto;
import org.meveo.api.dto.response.account.GetCustomerAccountResponseDto;
import org.meveo.api.dto.response.billing.GetCountersInstancesResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.account.CustomerAccountRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.payments.CustomerAccount;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.Date;
import java.util.List;

/**
 * @author Edward P. Legaspi
 * @author anasseh
 * @author Abdellatif BARI
 * @lastModifiedVersion 8.0.0
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
            CustomerAccount customerAccount = customerAccountApi.create(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(customerAccount.getCode());
            }
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
    public GetCustomerAccountResponseDto find(String customerAccountCode, Boolean calculateBalances, CustomFieldInheritanceEnum inheritCF, Boolean withAccountOperations) {
        GetCustomerAccountResponseDto result = new GetCustomerAccountResponseDto();

        try {
            result.setCustomerAccount(customerAccountApi.find(customerAccountCode, calculateBalances, inheritCF, withAccountOperations));
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
            CustomerAccount customerAccount = customerAccountApi.createOrUpdate(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(customerAccount.getCode());
            }
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus transferAccount(TransferCustomerAccountDto transferCustomerAccountDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customerAccountApi.transferAccount(transferCustomerAccountDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetCountersInstancesResponseDto filterCustomerAccountCountersByPeriod(String customerAccountCode, Date date) {
        GetCountersInstancesResponseDto result = new GetCountersInstancesResponseDto();

        try {
            List<CounterInstance> counters = customerAccountApi.filterCountersByPeriod(customerAccountCode, date);
            for (CounterInstance ci : counters) {
                result.getCountersInstances().getCounterInstance().add(new CounterInstanceDto(ci));
            }
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

}
