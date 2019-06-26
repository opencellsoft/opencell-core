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