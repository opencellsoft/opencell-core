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

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.MessagingException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.notification.EmailNotification;
import org.meveo.model.notification.NotificationHistoryStatusEnum;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.communication.impl.EmailSender;
import org.meveo.service.communication.impl.InternationalSettingsService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.slf4j.Logger;

/**
 * @author phung
 * @author Edward P. Legaspi
 * @author Youssef IZEM
 * @lastModifiedVersion 7.0
 */
@Stateless
public class EmailNotifier {

    @Inject
    NotificationHistoryService notificationHistoryService;
    
    @Inject
    protected CustomFieldInstanceService customFieldInstanceService;

    @Inject
    private Logger log;

    @Inject
    private EmailSender emailSender;

    @Inject
    private CurrentUserProvider currentUserProvider;

    @Inject
    private InternationalSettingsService internationalSettingsService;

    /**
     * Send email message as fired notification result
     * 
     * @param notification Email type notification that was fired
     * @param entityOrEvent Entity or event that triggered notification
     * @param context Execution context
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     */
    @Asynchronous
    public void sendEmailAsync(EmailNotification notification, Object entityOrEvent, Map<String, Object> context, MeveoUser lastCurrentUser) {
    	sendEmail(notification, entityOrEvent, context, lastCurrentUser);
    }
    
    /**
     * Send email message as fired notification result
     * 
     * @param notification Email type notification that was fired
     * @param entityOrEvent Entity or event that triggered notification
     * @param context Execution context
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     */
    public boolean sendEmail(EmailNotification notification, Object entityOrEvent, Map<String, Object> context, MeveoUser lastCurrentUser) {
        boolean isSent = false;
        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        try {

            HashMap<Object, Object> userMap = (HashMap<Object, Object>) customFieldInstanceService.getCFValue(notification, EmailNotification.EMAIL_TEMPLATE_PARAMS);
            if (userMap == null) {
                userMap = new HashMap<>();
            }
            userMap.put("event", entityOrEvent);
                    
            for (Map.Entry<String, Object> entry : context.entrySet()) {
                log.trace("key:"+entry.getKey()+"  value:"+entry.getValue());
                userMap.put(entry.getKey(), entry.getValue());
            }
            
            log.debug("event[{}], context[{}]", entityOrEvent, context);

            String body = null;
            String subject = null;
            String htmlBody = null;

            if (notification != null && notification.getEmailTemplate() != null) {
                EmailTemplate emailTemplate = notification.getEmailTemplate();

                String languageCode = lastCurrentUser.getLocale();
                String emailSubject = internationalSettingsService.resolveSubject(emailTemplate,languageCode);
                String emailContent = internationalSettingsService.resolveEmailContent(emailTemplate,languageCode);
                String htmlContent = internationalSettingsService.resolveHtmlContent(emailTemplate,languageCode);

                subject = (String) ValueExpressionWrapper.evaluateExpression(emailSubject, userMap, String.class);
                if (!StringUtils.isBlank(notification.getEmailTemplate().getHtmlContent())) {
                    htmlBody = (String) ValueExpressionWrapper.evaluateExpression(htmlContent, userMap, String.class);
                } else {
                    body = (String) ValueExpressionWrapper.evaluateExpression(emailContent, userMap, String.class);
                }
            }

            List<String> to = new ArrayList<>();
            to.add((String) ValueExpressionWrapper.evaluateExpression(notification.getEmailToEl(), userMap, String.class));
           
            String result = context.containsKey("EMAIL_TO_LIST") ? (String)context.get("EMAIL_TO_LIST") : "" ;
            for (String mail : result.split(",")) {
            	if(!StringUtils.isBlank(mail)) {
            		to.add(mail);
            	}
            }
            
            if (notification.getEmails() != null) {
                to.addAll(notification.getEmails());
            }

            String emailFrom = context.containsKey("EMAIL_FROM") ? (String)context.get("EMAIL_FROM") :
                    ValueExpressionWrapper.evaluateExpression(notification.getEmailFrom(), userMap, String.class);           
            emailSender.send(emailFrom, asList(emailFrom), to, subject, body, htmlBody);
            isSent = true;
            if (notification.isSaveSuccessfulNotifications()) {
                notificationHistoryService.create(notification, entityOrEvent, "", NotificationHistoryStatusEnum.SENT);
            }
           
        } catch (Exception e) {
            try {
                log.error("Error occured when sending email", e);
                notificationHistoryService.create(notification, entityOrEvent, e.getMessage(),
                    e instanceof MessagingException ? NotificationHistoryStatusEnum.TO_RETRY : NotificationHistoryStatusEnum.FAILED);
            } catch (BusinessException e2) {
                log.error("Failed to create notification history", e2);
            }
        }
        return isSent;
    }
}
