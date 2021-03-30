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

package org.meveo.api.notification;

import java.util.HashSet;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.notification.EmailNotificationDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.notification.EmailNotification;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.notification.EmailNotificationService;
import org.meveo.service.script.ScriptInstanceService;

/**
 * The CRUD Api for EmailNotification Entity.
 *
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class EmailNotificationApi extends BaseCrudApi<EmailNotification, EmailNotificationDto> {

    @Inject
    private EmailNotificationService emailNotificationService;

    @Inject
    private CounterTemplateService counterTemplateService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Override
    public EmailNotification create(EmailNotificationDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getClassNameFilter())) {
            missingParameters.add("classNameFilter");
        }
        if (postData.getEventTypeFilter() == null) {
            missingParameters.add("eventTypeFilter");
        }
        if (StringUtils.isBlank(postData.getEmailFrom())) {
            missingParameters.add("emailFrom");
        }
        if (StringUtils.isBlank(postData.getSubject())) {
            missingParameters.add("subject");
        }
        if (StringUtils.isBlank(postData.getCode())) {
            addGenericCodeIfAssociated(EmailNotification.class.getName(), postData);
        }

        handleMissingParameters();

        if (emailNotificationService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(EmailNotification.class, postData.getCode());
        }
        ScriptInstance scriptInstance = null;
        if (!StringUtils.isBlank(postData.getScriptInstanceCode())) {
            scriptInstance = scriptInstanceService.findByCode(postData.getScriptInstanceCode());
            if (scriptInstance == null) {
                throw new EntityDoesNotExistsException(ScriptInstance.class, postData.getScriptInstanceCode());
            }
        }
        // check class
        try {
            Class.forName(postData.getClassNameFilter());
        } catch (Exception e) {
            throw new InvalidParameterException("classNameFilter", postData.getClassNameFilter());
        }

        CounterTemplate counterTemplate = null;
        if (!StringUtils.isBlank(postData.getCounterTemplate())) {
            counterTemplate = counterTemplateService.findByCode(postData.getCounterTemplate());
            if (counterTemplate == null) {
                throw new EntityDoesNotExistsException(CounterTemplate.class, postData.getCounterTemplate());
            }
        }

        EmailNotification notif = new EmailNotification();
        notif.setCode(postData.getCode());
        notif.setClassNameFilter(postData.getClassNameFilter());
        notif.setEventTypeFilter(postData.getEventTypeFilter());
        notif.setScriptInstance(scriptInstance);
        notif.setParams(postData.getScriptParams());
        notif.setElFilter(postData.getElFilter());
        notif.setCounterTemplate(counterTemplate);
        notif.setEmailFrom(postData.getEmailFrom());
        notif.setEmailToEl(postData.getEmailToEl());
        notif.setRunAsync(postData.isRunAsync());
        notif.setDescription(postData.getDescription());
        
        EmailTemplate emailTemplate = new EmailTemplate();
        emailTemplate.setSubject(postData.getSubject());
        emailTemplate.setTextContent(postData.getBody());
        emailTemplate.setHtmlContent(postData.getHtmlBody());
        notif.setEmailTemplate(emailTemplate);

        Set<String> emails = new HashSet<>();
        for (String email : postData.getSendToMail()) {
            emails.add(email);
        }
        notif.setEmails(emails);

        emailNotificationService.create(notif);

        return notif;
    }

    @Override
    public EmailNotificationDto find(String notificationCode) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {
        EmailNotificationDto result = new EmailNotificationDto();

        if (!StringUtils.isBlank(notificationCode)) {
            EmailNotification notif = emailNotificationService.findByCode(notificationCode);

            if (notif == null) {
                throw new EntityDoesNotExistsException(EmailNotification.class, notificationCode);
            }

            result = new EmailNotificationDto(notif);
        } else {
            missingParameters.add("code");

            handleMissingParameters();
        }

        return result;
    }

    @Override
    public EmailNotification update(EmailNotificationDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getClassNameFilter())) {
            missingParameters.add("classNameFilter");
        }
        if (postData.getEventTypeFilter() == null) {
            missingParameters.add("eventTypeFilter");
        }
        if (StringUtils.isBlank(postData.getEmailFrom())) {
            missingParameters.add("emailFrom");
        }
        if (StringUtils.isBlank(postData.getSubject())) {
            missingParameters.add("subject");
        }

        handleMissingParameters();

        EmailNotification notif = emailNotificationService.findByCode(postData.getCode());
        if (notif == null) {
            throw new EntityDoesNotExistsException(EmailNotification.class, postData.getCode());
        }
        ScriptInstance scriptInstance = null;
        if (!StringUtils.isBlank(postData.getScriptInstanceCode())) {
            scriptInstance = scriptInstanceService.findByCode(postData.getScriptInstanceCode());
            if (scriptInstance == null) {
                throw new EntityDoesNotExistsException(ScriptInstance.class, postData.getScriptInstanceCode());
            }
        }
        // check class
        try {
            Class.forName(postData.getClassNameFilter());
        } catch (Exception e) {
            throw new InvalidParameterException("classNameFilter", postData.getClassNameFilter());
        }

        CounterTemplate counterTemplate = null;
        if (!StringUtils.isBlank(postData.getCounterTemplate())) {
            counterTemplate = counterTemplateService.findByCode(postData.getCounterTemplate());
            if (counterTemplate == null) {
                throw new EntityDoesNotExistsException(CounterTemplate.class, postData.getCounterTemplate());
            }
        }
        notif.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        notif.setClassNameFilter(postData.getClassNameFilter());
        notif.setEventTypeFilter(postData.getEventTypeFilter());
        notif.setScriptInstance(scriptInstance);
        notif.setParams(postData.getScriptParams());
        notif.setElFilter(postData.getElFilter());
        notif.setCounterTemplate(counterTemplate);
        notif.setEmailFrom(postData.getEmailFrom());
        notif.setEmailToEl(postData.getEmailToEl());
        notif.setDescription(postData.getDescription());
       
        EmailTemplate emailTemplate = notif.getEmailTemplate();
        if (emailTemplate == null) {
            emailTemplate = new EmailTemplate();
            emailTemplate.setCode(notif.getCode());
        }
        emailTemplate.setSubject(postData.getSubject());
        emailTemplate.setTextContent(postData.getBody());
        emailTemplate.setHtmlContent(postData.getHtmlBody());
        notif.setEmailTemplate(emailTemplate);

        if (postData.isDisabled() != null) {
            notif.setDisabled(postData.isDisabled());
        }

        Set<String> emails = new HashSet<>();
        for (String email : postData.getSendToMail()) {
            emails.add(email);
        }
        notif.setEmails(emails);
		if (postData.isRunAsync() != null) {
			notif.setRunAsync(postData.isRunAsync());
		}

        notif = emailNotificationService.update(notif);

        return notif;
    }
}