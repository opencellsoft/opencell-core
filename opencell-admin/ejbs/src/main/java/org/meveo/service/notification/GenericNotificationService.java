package org.meveo.service.notification;

import org.meveo.cache.NotificationCacheContainerProvider;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.AuditableEntity;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.notification.Notification;
import org.meveo.model.notification.NotificationEventTypeEnum;
import org.meveo.service.base.BusinessService;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tyshan Shi(tyshan@manaty.net)
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */

@Stateless
public class GenericNotificationService extends BusinessService<Notification> {

    @Inject
    private NotificationCacheContainerProvider notificationCacheContainerProvider;

    static boolean useNotificationCache = true;

    @PostConstruct
    private void init() {
        useNotificationCache = Boolean.parseBoolean(paramBeanFactory.getInstance().getProperty("cache.cacheNotification", "true"));
    }

    /**
     * Get a list of notifications that match event type and entity class. Notifications are looked up from cache or retrieved from DB.
     * 
     * @param eventType Event type
     * @param entityOrEvent Entity involved or event containing the entity involved
     * @return A list of notifications
     */
    public List<Notification> getApplicableNotifications(NotificationEventTypeEnum eventType, Object entityOrEvent) {

        if (useNotificationCache) {

            List<Notification> notifications = notificationCacheContainerProvider.getApplicableNotifications(eventType, entityOrEvent);

            // Populate cache if no record was found in cache
            if (notifications == null) {

                notifications = getApplicableNotificationsNoCache(eventType, entityOrEvent);
                if (notifications.isEmpty()) {
                    notificationCacheContainerProvider.markNoNotifications(eventType, entityOrEvent);
                } else {
                    notifications.forEach((notification) -> notificationCacheContainerProvider.addNotificationToCache(notification));
                }
            }

            return notifications;

        } else {
            return getApplicableNotificationsNoCache(eventType, entityOrEvent);
        }
    }

    /**
     * Get a list of notifications that match event type and entity class - always do a lookup in DB
     * 
     * @param eventType Event type
     * @param entityOrEvent Entity involved or event containing the entity involved
     * @return A list of notifications
     */
    @SuppressWarnings("unchecked")
    public List<Notification> getApplicableNotificationsNoCache(NotificationEventTypeEnum eventType, Object entityOrEvent) {

        Object entity = notificationCacheContainerProvider.getEntity(entityOrEvent);

        @SuppressWarnings("rawtypes")
        Class entityClass = entity.getClass();

        List<String> classNames = new ArrayList<>();

        while (!entityClass.isAssignableFrom(BusinessCFEntity.class) && !entityClass.isAssignableFrom(BusinessEntity.class) && !entityClass.isAssignableFrom(BaseEntity.class)
                && !entityClass.isAssignableFrom(AuditableEntity.class) && !entityClass.isAssignableFrom(Object.class)) {

            classNames.add(ReflectionUtils.getCleanClassName(entityClass.getName()));
            entityClass = entityClass.getSuperclass();
        }

        return getEntityManager().createNamedQuery("Notification.getActiveNotificationsByEventAndClasses", Notification.class).setParameter("eventTypeFilter", eventType)
            .setParameter("classNameFilter", classNames).getResultList();
    }

    /**
     * Get a list of notifications to populate a cache
     * 
     * @return A list of active notifications
     */
    public List<Notification> getNotificationsForCache() {
        return getEntityManager().createNamedQuery("Notification.getNotificationsForCache", Notification.class).getResultList();
    }
}