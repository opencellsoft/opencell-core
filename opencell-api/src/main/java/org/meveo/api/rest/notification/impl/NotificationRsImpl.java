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

package org.meveo.api.rest.notification.impl;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.notification.ScriptNotificationDto;
import org.meveo.api.dto.response.notification.GetScriptNotificationResponseDto;
import org.meveo.api.dto.response.notification.InboundRequestsResponseDto;
import org.meveo.api.dto.response.notification.NotificationHistoriesResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.notification.ScriptNotificationApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.notification.NotificationRs;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class NotificationRsImpl extends BaseRs implements NotificationRs {

    @Inject
    private ScriptNotificationApi scriptNotificationApi;

    @Override
    public ActionStatus create(ScriptNotificationDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            scriptNotificationApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(ScriptNotificationDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            scriptNotificationApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetScriptNotificationResponseDto find(String notificationCode) {
        GetScriptNotificationResponseDto result = new GetScriptNotificationResponseDto();

        try {
            result.setNotificationDto(scriptNotificationApi.find(notificationCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String notificationCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            scriptNotificationApi.remove(notificationCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public NotificationHistoriesResponseDto listNotificationHistory() {
        NotificationHistoriesResponseDto result = new NotificationHistoriesResponseDto();

        try {
            result.setNotificationHistories(scriptNotificationApi.listNotificationHistory());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public InboundRequestsResponseDto listInboundRequest() {
        InboundRequestsResponseDto result = new InboundRequestsResponseDto();

        try {
            result.setInboundRequests(scriptNotificationApi.listInboundRequest());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(ScriptNotificationDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            scriptNotificationApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus enable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            scriptNotificationApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            scriptNotificationApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}