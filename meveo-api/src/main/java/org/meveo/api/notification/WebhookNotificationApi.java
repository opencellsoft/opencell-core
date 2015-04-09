package org.meveo.api.notification;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.notification.WebhookNotificationDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidEnumValue;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.notification.NotificationEventTypeEnum;
import org.meveo.model.notification.WebHook;
import org.meveo.model.notification.WebHookMethodEnum;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.notification.WebHookService;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class WebhookNotificationApi extends BaseApi {

	@Inject
	private Logger log;

	@Inject
	private WebHookService webHookService;

	@SuppressWarnings("rawtypes")
	@Inject
	private CounterTemplateService counterTemplateService;

	public void create(WebhookNotificationDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getClassNameFilter()) && !StringUtils.isBlank(postData.getEventTypeFilter())
				&& !StringUtils.isBlank(postData.getHost()) && !StringUtils.isBlank(postData.getPage()) && !StringUtils.isBlank(postData.getHttpMethod())) {
			if (webHookService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
				throw new EntityAlreadyExistsException(WebHook.class, postData.getCode());
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

			WebHook webHook = new WebHook();
			webHook.setProvider(currentUser.getProvider());
			webHook.setCode(postData.getCode());
			webHook.setClassNameFilter(postData.getClassNameFilter());
			webHook.setEventTypeFilter(notificationEventType);
			webHook.setElAction(postData.getElAction());
			webHook.setElFilter(postData.getElFilter());
			webHook.setCounterTemplate(counterTemplate);

			webHook.setHost(postData.getHost());
			webHook.setPort(postData.getPort());
			webHook.setPage(postData.getPage());
			try {
				webHook.setHttpMethod(WebHookMethodEnum.valueOf(postData.getHttpMethod()));
			} catch (IllegalArgumentException e) {
				log.error("enum: {}", e.getMessage());
				throw new InvalidEnumValue(WebHookMethodEnum.class.getName(), postData.getHttpMethod());
			}
			webHook.setUsername(postData.getUsername());
			webHook.setPassword(postData.getPassword());
			if (postData.getHeaders() != null) {
				webHook.getHeaders().putAll(postData.getHeaders());
			}
			if (postData.getParams() != null) {
				webHook.getParams().putAll(postData.getParams());
			}

			webHookService.create(webHook, currentUser, currentUser.getProvider());
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
			if (StringUtils.isBlank(postData.getHost())) {
				missingParameters.add("host");
			}
			if (StringUtils.isBlank(postData.getPage())) {
				missingParameters.add("page");
			}
			if (StringUtils.isBlank(postData.getHttpMethod())) {
				missingParameters.add("httpMethod");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public WebhookNotificationDto find(String notificationCode, Provider provider) throws MeveoApiException {
		WebhookNotificationDto result = new WebhookNotificationDto();

		if (!StringUtils.isBlank(notificationCode)) {
			WebHook notif = webHookService.findByCode(notificationCode, provider);

			if (notif == null) {
				throw new EntityDoesNotExistsException(WebHook.class, notificationCode);
			}

			result = new WebhookNotificationDto(notif);
		} else {
			missingParameters.add("code");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}

		return result;
	}

	public void update(WebhookNotificationDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getClassNameFilter()) && !StringUtils.isBlank(postData.getEventTypeFilter())
				&& !StringUtils.isBlank(postData.getHost()) && !StringUtils.isBlank(postData.getPage()) && !StringUtils.isBlank(postData.getHttpMethod())) {
			WebHook webHook = webHookService.findByCode(postData.getCode(), currentUser.getProvider());
			if (webHook == null) {
				throw new EntityDoesNotExistsException(WebHook.class, postData.getCode());
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

			webHook.setClassNameFilter(postData.getClassNameFilter());
			webHook.setEventTypeFilter(notificationEventType);
			webHook.setElAction(postData.getElAction());
			webHook.setElFilter(postData.getElFilter());
			webHook.setCounterTemplate(counterTemplate);

			webHook.setHost(postData.getHost());
			webHook.setPort(postData.getPort());
			webHook.setPage(postData.getPage());
			try {
				webHook.setHttpMethod(WebHookMethodEnum.valueOf(postData.getHttpMethod()));
			} catch (IllegalArgumentException e) {
				log.error("enum: {}", e.getMessage());
				throw new InvalidEnumValue(WebHookMethodEnum.class.getName(), postData.getHttpMethod());
			}
			webHook.setUsername(postData.getUsername());
			webHook.setPassword(postData.getPassword());
			if (postData.getHeaders() != null) {
				webHook.getHeaders().putAll(postData.getHeaders());
			}
			if (postData.getParams() != null) {
				webHook.getParams().putAll(postData.getParams());
			}

			webHookService.update(webHook, currentUser);
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
			if (StringUtils.isBlank(postData.getHost())) {
				missingParameters.add("host");
			}
			if (StringUtils.isBlank(postData.getPage())) {
				missingParameters.add("page");
			}
			if (StringUtils.isBlank(postData.getHttpMethod())) {
				missingParameters.add("httpMethod");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void remove(String notificationCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(notificationCode)) {
			WebHook webHook = webHookService.findByCode(notificationCode, provider);

			if (webHook == null) {
				throw new EntityDoesNotExistsException(WebHook.class, notificationCode);
			}

			webHookService.remove(webHook);
		} else {
			missingParameters.add("code");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

}
