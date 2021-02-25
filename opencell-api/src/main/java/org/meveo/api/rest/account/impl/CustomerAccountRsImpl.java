/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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
import org.meveo.apiv2.generic.GenericPagingAndFilteringUtils;
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
        result.setPaging( GenericPagingAndFilteringUtils.getInstance().getPagingAndFiltering() );

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
