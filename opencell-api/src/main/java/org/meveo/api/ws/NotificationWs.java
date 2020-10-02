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

package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.notification.EmailNotificationDto;
import org.meveo.api.dto.notification.ScriptNotificationDto;
import org.meveo.api.dto.notification.WebHookDto;
import org.meveo.api.dto.response.notification.GetEmailNotificationResponseDto;
import org.meveo.api.dto.response.notification.GetScriptNotificationResponseDto;
import org.meveo.api.dto.response.notification.GetWebHookNotificationResponseDto;
import org.meveo.api.dto.response.notification.InboundRequestsResponseDto;
import org.meveo.api.dto.response.notification.NotificationHistoriesResponseDto;

/**
 * @author Edward P. Legaspi
 **/
@WebService
@Deprecated
public interface NotificationWs extends IBaseWs {

    // notification

    @WebMethod
    ActionStatus createNotification(@WebParam(name = "notification") ScriptNotificationDto postData);

    @WebMethod
    ActionStatus updateNotification(@WebParam(name = "notification") ScriptNotificationDto postData);

    @WebMethod
    GetScriptNotificationResponseDto findNotification(@WebParam(name = "notificationCode") String notificationCode);

    @WebMethod
    ActionStatus removeNotification(@WebParam(name = "notificationCode") String notificationCode);

    @WebMethod
    ActionStatus createOrUpdateNotification(@WebParam(name = "notification") ScriptNotificationDto postData);

    /**
     * Enable a Script type notification by its code
     * 
     * @param code Script type notification code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableNotification(@WebParam(name = "code") String code);

    /**
     * Disable a Script type notification by its code
     * 
     * @param code Script type notification code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableNotification(@WebParam(name = "code") String code);

    // webHook

    @WebMethod
    ActionStatus createWebHookNotification(@WebParam(name = "notification") WebHookDto postData);

    @WebMethod
    ActionStatus updateWebHookNotification(@WebParam(name = "notification") WebHookDto postData);

    @WebMethod
    GetWebHookNotificationResponseDto findWebHookNotification(@WebParam(name = "notificationCode") String notificationCode);

    @WebMethod
    ActionStatus removeWebHookNotification(@WebParam(name = "notificationCode") String notificationCode);

    @WebMethod
    ActionStatus createOrUpdateWebHookNotification(@WebParam(name = "notification") WebHookDto postData);

    /**
     * Enable a Webhook notification by its code
     * 
     * @param code Webhook notification code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableWebHookNotification(@WebParam(name = "code") String code);

    /**
     * Disable a Webhook notification by its code
     * 
     * @param code Webhook notification code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableWebHookNotification(@WebParam(name = "code") String code);

    // email

    @WebMethod
    ActionStatus createEmailNotification(@WebParam(name = "notification") EmailNotificationDto postData);

    @WebMethod
    ActionStatus updateEmailNotification(@WebParam(name = "notification") EmailNotificationDto postData);

    @WebMethod
    GetEmailNotificationResponseDto findEmailNotification(@WebParam(name = "notificationCode") String notificationCode);

    @WebMethod
    ActionStatus removeEmailNotification(@WebParam(name = "notificationCode") String notificationCode);

    @WebMethod
    ActionStatus createOrUpdateEmailNotification(@WebParam(name = "notification") EmailNotificationDto postData);

    /**
     * Enable a Email notification by its code
     * 
     * @param code Email notification code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableEmailNotification(@WebParam(name = "code") String code);

    /**
     * Disable a Email notification by its code
     * 
     * @param code Email notification code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableEmailNotification(@WebParam(name = "code") String code);

    // history

    @WebMethod
    NotificationHistoriesResponseDto listNotificationHistory();

    @WebMethod
    InboundRequestsResponseDto listInboundRequest();

}
