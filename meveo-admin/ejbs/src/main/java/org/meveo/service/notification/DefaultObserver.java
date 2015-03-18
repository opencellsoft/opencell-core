package org.meveo.service.notification;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.cache.NotificationCacheContainerProvider;
import org.meveo.commons.utils.StringUtils;
import org.meveo.event.qualifier.Created;
import org.meveo.event.qualifier.Disabled;
import org.meveo.event.qualifier.Enabled;
import org.meveo.event.qualifier.InboundRequestReceived;
import org.meveo.event.qualifier.LoggedIn;
import org.meveo.event.qualifier.Processed;
import org.meveo.event.qualifier.Rejected;
import org.meveo.event.qualifier.RejectedCDR;
import org.meveo.event.qualifier.Removed;
import org.meveo.event.qualifier.Terminated;
import org.meveo.event.qualifier.Updated;
import org.meveo.model.BaseEntity;
import org.meveo.model.IEntity;
import org.meveo.model.admin.User;
import org.meveo.model.notification.EmailNotification;
import org.meveo.model.notification.InboundRequest;
import org.meveo.model.notification.InstantMessagingNotification;
import org.meveo.model.notification.Notification;
import org.meveo.model.notification.NotificationEventTypeEnum;
import org.meveo.model.notification.NotificationHistory;
import org.meveo.model.notification.NotificationHistoryStatusEnum;
import org.meveo.model.notification.WebHook;
import org.meveo.service.billing.impl.CounterInstanceService;
import org.meveo.service.billing.impl.CounterValueInsufficientException;
import org.meveo.service.billing.impl.RatingService;
import org.slf4j.Logger;

@Singleton
@Startup
public class DefaultObserver {

    @Inject
    private Logger log;

    @Inject
    private BeanManager manager;

    @Inject
    private NotificationHistoryService notificationHistoryService;

    @Inject
    private EmailNotifier emailNotifier;

    @Inject
    private WebHookNotifier webHookNotifier;

    @Inject
    private InstantMessagingNotifier imNotifier;

    @Inject
    private CounterInstanceService counterInstanceService;

    @Inject
    private NotificationCacheContainerProvider notificationCacheContainerProvider;

    private boolean matchExpression(String expression, Object o) throws BusinessException {
        Boolean result = true;
        if (StringUtils.isBlank(expression)) {
            return result;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("event", o);

        Object res = RatingService.evaluateExpression(expression, userMap, Boolean.class);
        try {
            result = (Boolean) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to boolean but " + res);
        }
        return result;
    }

    private String executeAction(String expression, Object o) throws BusinessException {
        log.debug("execute notification action: {}", expression);
        if (StringUtils.isBlank(expression)) {
            return "";
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("event", o);
        userMap.put("manager", manager);
        return (String) RatingService.evaluateExpression(expression, userMap, String.class);
    }

    private void fireNotification(Notification notif, IEntity e) {
        log.debug("Fire Notification for notif with {} and entity with id={}", notif, e.getId());
        try {
            if (!matchExpression(notif.getElFilter(), e)) {
                return;
            }

            // we first perform the EL actions
            if (!(notif instanceof WebHook)) {
                executeAction(notif.getElAction(), e);
            }

            boolean sendNotify = true;
            // Check if the counter associated to notification was not exhausted
            // yet
            if (notif.getCounterInstance() != null) {
                try {
                    counterInstanceService.deduceCounterValue(notif.getCounterInstance(), new Date(), notif.getAuditable().getCreated(), new BigDecimal(1), notif.getAuditable()
                        .getCreator());
                } catch (CounterValueInsufficientException ex) {
                    sendNotify = false;
                }
            }

            if (!sendNotify) {
                return;
            }
            // then the notification itself
            if (notif instanceof EmailNotification) {
                emailNotifier.sendEmail((EmailNotification) notif, e);
            } else if (notif instanceof WebHook) {
                webHookNotifier.sendRequest((WebHook) notif, e);
            } else if (notif instanceof InstantMessagingNotification) {
                imNotifier.sendInstantMessage((InstantMessagingNotification) notif, e);
            }
            if (notif.getEventTypeFilter() == NotificationEventTypeEnum.INBOUND_REQ) {
                NotificationHistory histo = notificationHistoryService.create(notif, e, "", NotificationHistoryStatusEnum.SENT);
                ((InboundRequest) e).add(histo);
            }

        } catch (BusinessException e1) {
            log.error("Error while firing notification {} for provider {}: {} ", notif.getCode(), notif.getProvider().getCode(), e1.getMessage());
            try {
                NotificationHistory notificationHistory = notificationHistoryService.create(notif, e, e1.getMessage(), NotificationHistoryStatusEnum.FAILED);
                if (e instanceof InboundRequest) {
                    ((InboundRequest) e).add(notificationHistory);
                }
            } catch (BusinessException e2) {
                e2.printStackTrace();
            }
        }
    }

    private void fireCdrNotification(Notification notif, Serializable cdr) {
        log.debug("Fire Cdr Notification for notif {} and  cdr {}", notif, cdr);
        try {
            if (!StringUtils.isBlank(notif.getElAction()) && matchExpression(notif.getElFilter(), cdr)) {
                executeAction(notif.getElAction(), cdr);
            }
        } catch (BusinessException e1) {
            log.error("Error while firing notification {} for provider {}: {} ", notif.getCode(), notif.getProvider().getCode(), e1.getMessage());
        }

    }

    private void checkEvent(NotificationEventTypeEnum type, BaseEntity entity) {

        for (Notification notif : notificationCacheContainerProvider.getApplicableNotifications(type, entity)) {
            fireNotification(notif, entity);
        }
    }

    public void entityCreated(@Observes @Created BaseEntity e) {
        log.debug("Defaut observer : Entity {} with id {} created", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.CREATED, e);
    }

    public void entityUpdated(@Observes @Updated BaseEntity e) {
        log.debug("Defaut observer : Entity {} with id {} updated", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.UPDATED, e);
    }

    public void entityRemoved(@Observes @Removed BaseEntity e) {
        log.debug("Defaut observer : Entity {} with id {} removed", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.REMOVED, e);
    }

    public void entityDisabled(@Observes @Disabled BaseEntity e) {
        log.debug("Defaut observer : Entity {} with id {} disabled", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.DISABLED, e);
    }

    public void entityEnabled(@Observes @Enabled BaseEntity e) {
        log.debug("Defaut observer : Entity {} with id {} enabled", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.ENABLED, e);
    }

    public void entityTerminated(@Observes @Terminated BaseEntity e) {
        log.debug("Defaut observer : Entity {} with id {} terminated", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.TERMINATED, e);
    }

    public void entityProcessed(@Observes @Processed BaseEntity e) {
        log.debug("Defaut observer : Entity {} with id {} processed", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.PROCESSED, e);
    }

    public void entityRejected(@Observes @Rejected BaseEntity e) {
        log.debug("Defaut observer : Entity {} with id {} rejected", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.REJECTED, e);
    }

    public void cdrRejected(@Observes @RejectedCDR Serializable cdr) {
        log.debug("Defaut observer : cdr {} rejected", cdr);
        for (Notification notif : notificationCacheContainerProvider.getApplicableNotifications(NotificationEventTypeEnum.REJECTED_CDR, cdr)) {
            fireCdrNotification(notif, cdr);
        }
    }

    public void loggedIn(@Observes @LoggedIn User e) {
        log.debug("Defaut observer : logged in class={} ", e.getClass().getName());
        checkEvent(NotificationEventTypeEnum.LOGGED_IN, e);
    }

    public void inboundRequest(@Observes @InboundRequestReceived InboundRequest e) {
        log.debug("Defaut observer : inbound request {} ", e.getCode());
        checkEvent(NotificationEventTypeEnum.INBOUND_REQ, e);
    }
}