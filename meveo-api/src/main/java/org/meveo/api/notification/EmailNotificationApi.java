package org.meveo.api.notification;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.notification.EmailNotificationDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidEnumValue;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.notification.EmailNotification;
import org.meveo.model.notification.NotificationEventTypeEnum;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.notification.EmailNotificationService;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class EmailNotificationApi extends BaseApi {

	@Inject
	private Logger log;

	@Inject
	private EmailNotificationService emailNotificationService;

	@SuppressWarnings("rawtypes")
	@Inject
	private CounterTemplateService counterTemplateService;

	public void create(EmailNotificationDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getClassNameFilter()) && !StringUtils.isBlank(postData.getEventTypeFilter())
				&& !StringUtils.isBlank(postData.getEmailFrom()) && !StringUtils.isBlank(postData.getSubject())) {
			if (emailNotificationService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
				throw new EntityAlreadyExistsException(EmailNotification.class, postData.getCode());
			}

			// check class
			try {
				Class.forName(postData.getClassNameFilter());
			} catch (Exception e) {
				throw new MeveoApiException("INVALID_CLASS_NAME", "INVALID_CLASS_NAME");
			}

			NotificationEventTypeEnum notificationEventType = null;
			try {
				notificationEventType = NotificationEventTypeEnum.valueOf(postData.getEventTypeFilter());
			} catch (IllegalArgumentException e) {
				log.error("enum: {}", e);
				throw new InvalidEnumValue(NotificationEventTypeEnum.class.getName(), postData.getEventTypeFilter());
			}

			CounterTemplate counterTemplate = null;
			if (!StringUtils.isBlank(postData.getCounterTemplate())) {
				counterTemplate = counterTemplateService.findByCode(postData.getCounterTemplate(), currentUser.getProvider());
			}

			EmailNotification notif = new EmailNotification();
			notif.setProvider(currentUser.getProvider());
			notif.setCode(postData.getCode());
			notif.setClassNameFilter(postData.getClassNameFilter());
			notif.setEventTypeFilter(notificationEventType);
			notif.setElAction(postData.getElAction());
			notif.setElFilter(postData.getElFilter());
			notif.setCounterTemplate(counterTemplate);

			notif.setEmailFrom(postData.getEmailFrom());
			notif.setEmailToEl(postData.getEmailToEl());
			notif.setSubject(postData.getSubject());
			notif.setBody(postData.getBody());
			notif.setHtmlBody(postData.getHtmlBody());

			emailNotificationService.create(notif, currentUser, currentUser.getProvider());
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getClassNameFilter())) {
				missingParameters.add("classNameFilter");
			}
			if (StringUtils.isBlank(postData.getEventTypeFilter())) {
				missingParameters.add("eventTypeFilter");
			}
			if (StringUtils.isBlank(postData.getEmailFrom())) {
				missingParameters.add("emailFrom");
			}
			if (StringUtils.isBlank(postData.getSubject())) {
				missingParameters.add("subject");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public EmailNotificationDto find(String notificationCode, Provider provider) throws MeveoApiException {
		EmailNotificationDto result = new EmailNotificationDto();

		if (!StringUtils.isBlank(notificationCode)) {
			EmailNotification notif = emailNotificationService.findByCode(notificationCode, provider);

			if (notif == null) {
				throw new EntityDoesNotExistsException(EmailNotification.class, notificationCode);
			}

			result = new EmailNotificationDto(notif);
		} else {
			missingParameters.add("code");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}

		return result;
	}

	public void update(EmailNotificationDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getClassNameFilter()) && !StringUtils.isBlank(postData.getEventTypeFilter())
				&& !StringUtils.isBlank(postData.getEmailFrom()) && !StringUtils.isBlank(postData.getSubject())) {
			EmailNotification notif = emailNotificationService.findByCode(postData.getCode(), currentUser.getProvider());
			if (notif == null) {
				throw new EntityDoesNotExistsException(EmailNotification.class, postData.getCode());
			}

			// check class
			try {
				Class.forName(postData.getClassNameFilter());
			} catch (Exception e) {
				throw new MeveoApiException("INVALID_CLASS_NAME", "INVALID_CLASS_NAME");
			}

			NotificationEventTypeEnum notificationEventType = null;
			try {
				notificationEventType = NotificationEventTypeEnum.valueOf(postData.getEventTypeFilter());
			} catch (IllegalArgumentException e) {
				log.error("enum: {}", e);
				throw new InvalidEnumValue(NotificationEventTypeEnum.class.getName(), postData.getEventTypeFilter());
			}

			CounterTemplate counterTemplate = null;
			if (!StringUtils.isBlank(postData.getCounterTemplate())) {
				counterTemplate = counterTemplateService.findByCode(postData.getCounterTemplate(), currentUser.getProvider());
			}

			notif.setClassNameFilter(postData.getClassNameFilter());
			notif.setEventTypeFilter(notificationEventType);
			notif.setElAction(postData.getElAction());
			notif.setElFilter(postData.getElFilter());
			notif.setCounterTemplate(counterTemplate);

			notif.setEmailFrom(postData.getEmailFrom());
			notif.setEmailToEl(postData.getEmailToEl());
			notif.setSubject(postData.getSubject());
			notif.setBody(postData.getBody());
			notif.setHtmlBody(postData.getHtmlBody());

			emailNotificationService.update(notif, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getClassNameFilter())) {
				missingParameters.add("classNameFilter");
			}
			if (StringUtils.isBlank(postData.getEventTypeFilter())) {
				missingParameters.add("eventTypeFilter");
			}
			if (StringUtils.isBlank(postData.getEmailFrom())) {
				missingParameters.add("emailFrom");
			}
			if (StringUtils.isBlank(postData.getSubject())) {
				missingParameters.add("subject");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void remove(String notificationCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(notificationCode)) {
			EmailNotification notif = emailNotificationService.findByCode(notificationCode, provider);

			if (notif == null) {
				throw new EntityDoesNotExistsException(EmailNotification.class, notificationCode);
			}

			emailNotificationService.remove(notif);
		} else {
			missingParameters.add("code");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

}
