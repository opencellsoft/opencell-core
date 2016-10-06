package org.meveo.api.notification;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.notification.InboundRequestDto;
import org.meveo.api.dto.notification.InboundRequestsDto;
import org.meveo.api.dto.notification.NotificationDto;
import org.meveo.api.dto.notification.NotificationHistoriesDto;
import org.meveo.api.dto.notification.NotificationHistoryDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.notification.InboundRequest;
import org.meveo.model.notification.Notification;
import org.meveo.model.notification.NotificationHistory;
import org.meveo.model.notification.ScriptNotification;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.notification.InboundRequestService;
import org.meveo.service.notification.NotificationHistoryService;
import org.meveo.service.notification.NotificationService;
import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class NotificationApi extends BaseCrudApi<Notification, NotificationDto> {

    @Inject
    private NotificationService notificationService;

    @SuppressWarnings("rawtypes")
    @Inject
    private CounterTemplateService counterTemplateService;

    @Inject
    private NotificationHistoryService notificationHistoryService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private InboundRequestService inboundRequestService;

    public Notification create(NotificationDto postData, User currentUser) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getClassNameFilter())) {
            missingParameters.add("classNameFilter");
        }
        if (postData.getEventTypeFilter() == null) {
            missingParameters.add("eventTypeFilter");
        }

        handleMissingParameters();

        if (notificationService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
            throw new EntityAlreadyExistsException(Notification.class, postData.getCode());
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

        ScriptNotification notif = new ScriptNotification();
        notif.setProvider(currentUser.getProvider());
        notif.setCode(postData.getCode());
        notif.setClassNameFilter(postData.getClassNameFilter());
        notif.setEventTypeFilter(postData.getEventTypeFilter());
        notif.setScriptInstance(scriptInstance);
        notif.setParams(postData.getScriptParams());
        notif.setElFilter(postData.getElFilter());
        notif.setCounterTemplate(counterTemplate);

        notificationService.create(notif, currentUser);

        return notif;
    }

    @Override
    public NotificationDto find(String notificationCode, User currentUser) throws MeveoApiException {
        NotificationDto result = new NotificationDto();

        if (!StringUtils.isBlank(notificationCode)) {
            ScriptNotification notif = notificationService.findByCode(notificationCode, currentUser.getProvider());

            if (notif == null) {
                throw new EntityDoesNotExistsException(Notification.class, notificationCode);
            }

            result = new NotificationDto(notif);
        } else {
            missingParameters.add("code");

            handleMissingParameters();
        }

        return result;
    }

    public Notification update(NotificationDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getClassNameFilter())) {
            missingParameters.add("classNameFilter");
        }
        if (postData.getEventTypeFilter() == null) {
            missingParameters.add("eventTypeFilter");
        }

        handleMissingParameters();

        ScriptNotification notif = notificationService.findByCode(postData.getCode(), currentUser.getProvider());
        if (notif == null) {
            throw new EntityDoesNotExistsException(Notification.class, postData.getCode());
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
        notif.setElFilter(postData.getElFilter());
        notif.setCounterTemplate(counterTemplate);
        notif.setParams(postData.getScriptParams());

        notif = notificationService.update(notif, currentUser);

        return notif;
    }

    public void remove(String notificationCode, User currentUser) throws MeveoApiException, BusinessException {
        if (!StringUtils.isBlank(notificationCode)) {
            ScriptNotification notif = notificationService.findByCode(notificationCode, currentUser.getProvider());

            if (notif == null) {
                throw new EntityDoesNotExistsException(Notification.class, notificationCode);
            }

            notificationService.remove(notif, currentUser);
        } else {
            missingParameters.add("code");

            handleMissingParameters();
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

    @Override
    public Notification createOrUpdate(NotificationDto postData, User currentUser) throws MeveoApiException, BusinessException {
        if (notificationService.findByCode(postData.getCode(), currentUser.getProvider()) == null) {
            return create(postData, currentUser);
        } else {
            return update(postData, currentUser);
        }
    }
}
