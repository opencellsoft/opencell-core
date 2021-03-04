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

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.account.BillingAccountApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.account.BillingAccountDto;
import org.meveo.api.dto.billing.CounterInstanceDto;
import org.meveo.api.dto.response.account.BillingAccountsResponseDto;
import org.meveo.api.dto.response.account.GetBillingAccountResponseDto;
import org.meveo.api.dto.response.billing.GetCountersInstancesResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.account.BillingAccountRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

/**
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class BillingAccountRsImpl extends BaseRs implements BillingAccountRs {

    @Inject
    private BillingAccountApi billingAccountApi;

    @Override
    public ActionStatus create(BillingAccountDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            BillingAccount billingAccount = billingAccountApi.create(postData);
            result.setEntityCode(billingAccount.getCode());
            result.setEntityId(billingAccount.getId());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(BillingAccountDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            BillingAccount billingAccount = billingAccountApi.update(postData);
            result.setEntityCode(billingAccount.getCode());
            result.setEntityId(billingAccount.getId());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetBillingAccountResponseDto find(String billingAccountCode, CustomFieldInheritanceEnum inheritCF, boolean includeUserAccounts) {
        GetBillingAccountResponseDto result = new GetBillingAccountResponseDto();

        try {
            result.setBillingAccount(billingAccountApi.find(billingAccountCode, inheritCF, includeUserAccounts));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String billingAccountCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            billingAccountApi.remove(billingAccountCode);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public BillingAccountsResponseDto listByCustomerAccount(String customerAccountCode) {
        BillingAccountsResponseDto result = new BillingAccountsResponseDto();

        try {
            result.setBillingAccounts(billingAccountApi.listByCustomerAccount(customerAccountCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(BillingAccountDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            BillingAccount billingAccount = billingAccountApi.createOrUpdate(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(billingAccount.getCode());
            }
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetCountersInstancesResponseDto filterBillingAccountCountersByPeriod(String billingAccountCode, Date date) {
        GetCountersInstancesResponseDto result = new GetCountersInstancesResponseDto();

        try {
            List<CounterInstance> counters = billingAccountApi.filterCountersByPeriod(billingAccountCode, date);
            for (CounterInstance ci : counters) {
                result.getCountersInstances().getCounterInstance().add(new CounterInstanceDto(ci));
            }

            result.getCountersInstances().getCounterInstance().sort(Comparator.comparing(CounterInstanceDto::getCode));
            
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

}
