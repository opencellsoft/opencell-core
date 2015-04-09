package org.meveo.api.notification;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.notification.InboundRequestDto;
import org.meveo.api.dto.notification.InboundRequestsDto;
import org.meveo.api.dto.notification.NotificationDto;
import org.meveo.api.dto.notification.NotificationHistoriesDto;
import org.meveo.api.dto.notification.NotificationHistoryDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidEnumValue;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.notification.InboundRequest;
import org.meveo.model.notification.Notification;
import org.meveo.model.notification.NotificationEventTypeEnum;
import org.meveo.model.notification.NotificationHistory;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.notification.InboundRequestService;
import org.meveo.service.notification.NotificationHistoryService;
import org.meveo.service.notification.NotificationService;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class NotificationApi extends BaseApi {

	@Inject
	private Logger log;

	@Inject
	private NotificationService notificationService;

	@SuppressWarnings("rawtypes")
	@Inject
	private CounterTemplateService counterTemplateService;

	@Inject
	private NotificationHistoryService notificationHistoryService;

	@Inject
	private InboundRequestService inboundRequestService;

	public void create(NotificationDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getClassNameFilter()) && !StringUtils.isBlank(postData.getEventTypeFilter())) {
			if (notificationService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
				throw new EntityAlreadyExistsException(Notification.class, postData.getCode());
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

			Notification notif = new Notification();
			notif.setProvider(currentUser.getProvider());
			notif.setCode(postData.getCode());
			notif.setClassNameFilter(postData.getClassNameFilter());
			notif.setEventTypeFilter(notificationEventType);
			notif.setElAction(postData.getElAction());
			notif.setElFilter(postData.getElFilter());
			notif.setCounterTemplate(counterTemplate);

			notificationService.create(notif, currentUser, currentUser.getProvider());
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

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public NotificationDto find(String notificationCode, Provider provider) throws MeveoApiException {
		NotificationDto result = new NotificationDto();

		if (!StringUtils.isBlank(notificationCode)) {
			Notification notif = notificationService.findByCode(notificationCode, provider);

			if (notif == null) {
				throw new EntityDoesNotExistsException(Notification.class, notificationCode);
			}

			result = new NotificationDto(notif);
		} else {
			missingParameters.add("code");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}

		return result;
	}

	public void update(NotificationDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getClassNameFilter()) && !StringUtils.isBlank(postData.getEventTypeFilter())) {
			Notification notif = notificationService.findByCode(postData.getCode(), currentUser.getProvider());
			if (notif == null) {
				throw new EntityDoesNotExistsException(Notification.class, postData.getCode());
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

			notificationService.update(notif, currentUser);
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

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void remove(String notificationCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(notificationCode)) {
			Notification notif = notificationService.findByCode(notificationCode, provider);

			if (notif == null) {
				throw new EntityDoesNotExistsException(Notification.class, notificationCode);
			}

			notificationService.remove(notif);
		} else {
			missingParameters.add("code");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public NotificationHistoriesDto listNotificationHistory(Provider provider) throws MeveoApiException {
		NotificationHistoriesDto result = new NotificationHistoriesDto();

		List<NotificationHistory> notificationHistories = notificationHistoryService.list(provider);
		if (notificationHistories != null) {
			for (NotificationHistory nh : notificationHistories) {
				result.getNotificationHistory().add(new NotificationHistoryDto(nh));
			}
		}

		return result;
	}

	public InboundRequestsDto listInboundRequest(Provider provider) throws MeveoApiException {
		InboundRequestsDto result = new InboundRequestsDto();

		List<InboundRequest> inboundRequests = inboundRequestService.list(provider);
		if (inboundRequests != null) {
			for (InboundRequest ir : inboundRequests) {
				result.getInboundRequest().add(new InboundRequestDto(ir));
			}
		}

		return result;
	}

}
