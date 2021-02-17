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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.account.AccessApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.response.account.AccessesResponseDto;
import org.meveo.api.dto.response.account.GetAccessResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.account.AccessRs;
import org.meveo.api.rest.impl.BaseRs;

import java.util.Date;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class AccessRsImpl extends BaseRs implements AccessRs {

    @Inject
    private AccessApi accessApi;

    @Override
    public ActionStatus create(AccessDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            result.setEntityId(accessApi.create(postData).getId());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(AccessDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            result.setEntityId(accessApi.update(postData).getId());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetAccessResponseDto find(String accessCode, String subscriptionCode, Date subscriptionValidityDate, Date startDate, Date endDate, Date usageDate) {
        GetAccessResponseDto result = new GetAccessResponseDto();

        try {
            result.setAccess(accessApi.find(accessCode, subscriptionCode, subscriptionValidityDate, startDate, endDate, usageDate));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String accessCode, String subscriptionCode, Date startDate, Date endDate) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            accessApi.remove(accessCode, subscriptionCode, startDate, endDate);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public AccessesResponseDto listBySubscription(String subscriptionCode) {
        AccessesResponseDto result = new AccessesResponseDto();

        try {
            result.setAccesses(accessApi.listBySubscription(subscriptionCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(AccessDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            accessApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus enable(String accessCode, String subscriptionCode, Date startDate, Date endDate) {

        ActionStatus result = new ActionStatus();

        try {
            accessApi.enableOrDisable(accessCode, subscriptionCode, startDate, endDate, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disable(String accessCode, String subscriptionCode, Date startDate, Date endDate) {

        ActionStatus result = new ActionStatus();

        try {
            accessApi.enableOrDisable(accessCode, subscriptionCode, startDate, endDate, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}
