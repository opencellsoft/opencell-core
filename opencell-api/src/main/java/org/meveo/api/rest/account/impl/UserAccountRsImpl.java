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

import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.account.UserAccountApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.account.ApplyProductRequestDto;
import org.meveo.api.dto.account.UserAccountDto;
import org.meveo.api.dto.billing.CounterInstanceDto;
import org.meveo.api.dto.response.account.GetUserAccountResponseDto;
import org.meveo.api.dto.response.account.UserAccountsResponseDto;
import org.meveo.api.dto.response.billing.GetCountersInstancesResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.account.UserAccountRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

/**
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class UserAccountRsImpl extends BaseRs implements UserAccountRs {

    @Inject
    private UserAccountApi userAccountApi;

    @Override
    public ActionStatus create(UserAccountDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            UserAccount userAccount = userAccountApi.create(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(userAccount.getCode());
            }
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(UserAccountDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            userAccountApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetUserAccountResponseDto find(String userAccountCode, boolean includeSubscriptions, CustomFieldInheritanceEnum inheritCF) {
        GetUserAccountResponseDto result = new GetUserAccountResponseDto();

        try {
        	final UserAccountDto userAccountDto = userAccountApi.find(userAccountCode, inheritCF);
            if(!includeSubscriptions) {
            	userAccountDto.setSubscriptions(null);
            }
            result.setUserAccount(userAccountDto);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String userAccountCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            userAccountApi.remove(userAccountCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public UserAccountsResponseDto listByBillingAccount(String billingAccountCode) {
        UserAccountsResponseDto result = new UserAccountsResponseDto();

        try {
            result.setUserAccounts(userAccountApi.listByBillingAccount(billingAccountCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(UserAccountDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            UserAccount userAccount = userAccountApi.createOrUpdate(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(userAccount.getCode());
            }
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetCountersInstancesResponseDto filterUserAccountCountersByPeriod(String userAccountCode, Date date) {
        GetCountersInstancesResponseDto result = new GetCountersInstancesResponseDto();

        try {
            List<CounterInstance> counters = userAccountApi.filterCountersByPeriod(userAccountCode, date);
            for (CounterInstance ci : counters) {
                result.getCountersInstances().getCounterInstance().add(new CounterInstanceDto(ci));
            }
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus applyProduct(ApplyProductRequestDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            userAccountApi.applyProduct(postData);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }
}
