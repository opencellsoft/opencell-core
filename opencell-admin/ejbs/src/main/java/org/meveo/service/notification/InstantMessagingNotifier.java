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

package org.meveo.service.notification;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.Session;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.notification.InstantMessagingNotification;
import org.meveo.model.notification.NotificationHistoryStatusEnum;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.base.ValueExpressionWrapper;
import org.slf4j.Logger;

//TODO : transform that into MDB to correctly handle retries
/**
 * @author Edward P. Legaspi
 * @lastMofiedVersion 7.0
 */
@Stateless
public class InstantMessagingNotifier {

    @Inject
    Logger log;

    @Resource(lookup = "java:/MeveoMail")
    private Session mailSession;

    @Inject
    NotificationHistoryService notificationHistoryService;
    

    @Inject
    private CurrentUserProvider currentUserProvider;

    // Jabber jabber = new Jabber();

    /**
     * Send instant message as fired notification result
     * 
     * @param notification Instant message type notification that was fired
     * @param entityOrEvent Entity or event that triggered notification
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     */
    @Asynchronous
    public void sendInstantMessageAsync(InstantMessagingNotification notification, Object entityOrEvent, MeveoUser lastCurrentUser) {
    	sendInstantMessage(notification, entityOrEvent, lastCurrentUser);
    }
    
    /**
     * Send instant message as fired notification result
     * 
     * @param notification Instant message type notification that was fired
     * @param entityOrEvent Entity or event that triggered notification
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     */
    public void sendInstantMessage(InstantMessagingNotification notification, Object entityOrEvent, MeveoUser lastCurrentUser) {
        

        currentUserProvider.reestablishAuthentication(lastCurrentUser);
        
        try {
            HashMap<Object, Object> userMap = new HashMap<Object, Object>();
            userMap.put("event", entityOrEvent);
            Set<String> imIdSet = notification.getIds();
            if (imIdSet == null) {
                imIdSet = new HashSet<String>();
            }
            if (!StringUtils.isBlank(notification.getIdEl())) {
                imIdSet.add((String) ValueExpressionWrapper.evaluateExpression(notification.getIdEl(), userMap, String.class));
            }
            String message = (String) ValueExpressionWrapper.evaluateExpression(notification.getMessage(), userMap, String.class);

            switch (notification.getImProvider()) {
            case FACEBOOK:
                break;
            case GTALK:

                break;
            case TWITTER:
                break;
            case YAHOO_MESSENGER:
                break;
            }
            if (notification.isSaveSuccessfulNotifications()) {
                notificationHistoryService.create(notification, entityOrEvent, "", NotificationHistoryStatusEnum.SENT);
            }
        } catch (Exception e) {
            try {
                notificationHistoryService.create(notification, entityOrEvent, e.getMessage(), NotificationHistoryStatusEnum.FAILED);
            } catch (BusinessException e2) {
                log.error("Failed to create notification history", e2);
            }
        }
    }
}
