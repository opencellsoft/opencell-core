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

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.notification.EmailNotification;
import org.meveo.service.communication.impl.EmailTemplateService;

/**
 * A service class to manage CRUD operations on EmailNotification entity
 * 
 * @author Andrius Karpavicius
 * @author Youssef IZEM
 * @lastModifiedVersion 7.0
 */
@Stateless
public class EmailNotificationService extends NotificationInstanceService<EmailNotification> {

    @Inject
    private EmailTemplateService emailTemplateService;

    @Override
    public void create(EmailNotification notification) throws BusinessException {
        EmailTemplate emailTemplate = notification.getEmailTemplate();
        if (emailTemplate != null) {
            emailTemplate.setCode(notification.getCode());
            this.emailTemplateService.create(emailTemplate);
        }
        super.create(notification);
    }

    @Override
    public EmailNotification update(EmailNotification notification) throws BusinessException {
        EmailTemplate emailTemplate = notification.getEmailTemplate();
        this.emailTemplateService.update(emailTemplate);
        return super.update(notification);
    }
    
}