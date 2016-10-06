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
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.notification.EmailNotification;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.notification.EmailNotificationService;
import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class EmailNotificationApi extends BaseCrudApi<EmailNotification, EmailNotificationDto> {

    @Inject
    private EmailNotificationService emailNotificationService;

    @SuppressWarnings("rawtypes")
    @Inject
    private CounterTemplateService counterTemplateService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    public EmailNotification create(EmailNotificationDto postData, User currentUser) throws MeveoApiException, BusinessException {

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

        if (emailNotificationService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
            throw new EntityAlreadyExistsException(EmailNotification.class, postData.getCode());
        }
        ScriptInstance scriptInstance = null;
        if (!StringUtils.isBlank(postData.getScriptInstanceCode())) {
            scriptInstance = scriptInstanceService.findByCode(postData.getScriptInstanceCode(), currentUser.getProvider());
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
            counterTemplate = counterTemplateService.findByCode(postData.getCounterTemplate(), currentUser.getProvider());
            if (counterTemplate == null) {
                throw new EntityDoesNotExistsException(CounterTemplate.class, postData.getCounterTemplate());
            }
        }

        EmailNotification notif = new EmailNotification();
        notif.setProvider(currentUser.getProvider());
        notif.setCode(postData.getCode());
        notif.setClassNameFilter(postData.getClassNameFilter());
        notif.setEventTypeFilter(postData.getEventTypeFilter());
        notif.setScriptInstance(scriptInstance);
        notif.setParams(postData.getScriptParams());
        notif.setElFilter(postData.getElFilter());
        notif.setCounterTemplate(counterTemplate);

        notif.setEmailFrom(postData.getEmailFrom());
        notif.setEmailToEl(postData.getEmailToEl());
        notif.setSubject(postData.getSubject());
        notif.setBody(postData.getBody());
        notif.setHtmlBody(postData.getHtmlBody());

        Set<String> emails = new HashSet<String>();
        for (String email : postData.getSendToMail()) {
            emails.add(email);
        }
        notif.setEmails(emails);

        emailNotificationService.create(notif, currentUser);

        return notif;
    }

    @Override
    public EmailNotificationDto find(String notificationCode, User currentUser) throws MeveoApiException {
        EmailNotificationDto result = new EmailNotificationDto();

        if (!StringUtils.isBlank(notificationCode)) {
            EmailNotification notif = emailNotificationService.findByCode(notificationCode, currentUser.getProvider());

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

    public EmailNotification update(EmailNotificationDto postData, User currentUser) throws MeveoApiException, BusinessException {

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

        EmailNotification notif = emailNotificationService.findByCode(postData.getCode(), currentUser.getProvider());
        if (notif == null) {
            throw new EntityDoesNotExistsException(EmailNotification.class, postData.getCode());
        }
        ScriptInstance scriptInstance = null;
        if (!StringUtils.isBlank(postData.getScriptInstanceCode())) {
            scriptInstance = scriptInstanceService.findByCode(postData.getScriptInstanceCode(), currentUser.getProvider());
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
            counterTemplate = counterTemplateService.findByCode(postData.getCounterTemplate(), currentUser.getProvider());
            if (counterTemplate == null) {
                throw new EntityDoesNotExistsException(CounterTemplate.class, postData.getCounterTemplate());
            }
        }

        notif.setClassNameFilter(postData.getClassNameFilter());
        notif.setEventTypeFilter(postData.getEventTypeFilter());
        notif.setScriptInstance(scriptInstance);
        notif.setParams(postData.getScriptParams());
        notif.setElFilter(postData.getElFilter());
        notif.setCounterTemplate(counterTemplate);

        notif.setEmailFrom(postData.getEmailFrom());
        notif.setEmailToEl(postData.getEmailToEl());
        notif.setSubject(postData.getSubject());
        notif.setBody(postData.getBody());
        notif.setHtmlBody(postData.getHtmlBody());
        Set<String> emails = new HashSet<String>();
        for (String email : postData.getSendToMail()) {
            emails.add(email);
        }
        notif.setEmails(emails);

        notif = emailNotificationService.update(notif, currentUser);

        return notif;
    }

    public void remove(String notificationCode, User currentUser) throws MeveoApiException, BusinessException {
        if (!StringUtils.isBlank(notificationCode)) {
            EmailNotification notif = emailNotificationService.findByCode(notificationCode, currentUser.getProvider());

            if (notif == null) {
                throw new EntityDoesNotExistsException(EmailNotification.class, notificationCode);
            }

            emailNotificationService.remove(notif, currentUser);
        } else {
            missingParameters.add("code");

            handleMissingParameters();
        }
    }

    @Override
    public EmailNotification createOrUpdate(EmailNotificationDto postData, User currentUser) throws MeveoApiException, BusinessException {
        if (emailNotificationService.findByCode(postData.getCode(), currentUser.getProvider()) == null) {
            return create(postData, currentUser);
        } else {
            return update(postData, currentUser);
        }
    }
}
