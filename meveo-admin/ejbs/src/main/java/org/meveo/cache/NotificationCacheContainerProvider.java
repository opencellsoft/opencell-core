package org.meveo.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.naming.InitialContext;

import org.infinispan.api.BasicCache;
import org.infinispan.manager.CacheContainer;
import org.meveo.model.BusinessEntity;
import org.meveo.model.notification.Notification;
import org.meveo.model.notification.NotificationEventTypeEnum;
import org.meveo.service.notification.NotificationService;
import org.slf4j.Logger;

/**
 * Provides cache related services (loading, update) for event notification related operations
 * 
 * @author Andrius Karpavicius
 */
@Startup
@Singleton
public class NotificationCacheContainerProvider {

    @Inject
    protected Logger log;

    /**
     * Contains association between event type, entity class and notifications. Key format: <eventTypeFilter> //<provider id>_<eventTypeFilter>
     */
    private BasicCache<String, HashMap<Class<BusinessEntity>, List<Notification>>> eventNotificationCache;

    @Inject
    private NotificationService notificationService;

    @PostConstruct
    private void init() {
        try {
            log.debug("NotificationCacheContainerProvider initializing...");
            CacheContainer meveoContainer = (CacheContainer) new InitialContext().lookup("java:jboss/infinispan/container/meveo");
            eventNotificationCache = meveoContainer.getCache("meveo-notification-cache");
            populateNotificationCache();
            log.debug("NotificationCacheContainerProvider initialized");

        } catch (Exception e) {
            log.error("NotificationCacheContainerProvider init() error", e);
        }
    }

    /**
     * Populate notification cache
     */
    private void populateNotificationCache() {
        log.info("start to populate notification cache");
        List<Notification> allNotif = notificationService.listAll();
        for (Notification notif : allNotif) {
            addNotificationToCache(notif);
        }
        log.debug("notification cache populated with {} notifications", allNotif.size());
    }

    /**
     * Add notification to a cache
     * 
     * @param notif Notification to add
     */
    public void addNotificationToCache(Notification notif) {
        if (notif.isDisabled()) {
            return;
        }
        try {
            String cacheKey = notif.getEventTypeFilter().name(); // notif.getProvider().getId() + "_" + notif.getEventTypeFilter().name();
            @SuppressWarnings("unchecked")
            Class<BusinessEntity> c = (Class<BusinessEntity>) Class.forName(notif.getClassNameFilter());
            if (!eventNotificationCache.get(cacheKey).containsKey(c)) {
                eventNotificationCache.get(cacheKey).put(c, new ArrayList<Notification>());
            }
            log.info("Add notification {} to notification cache", notif);
            eventNotificationCache.get(cacheKey).get(c).add(notif);

        } catch (ClassNotFoundException e) {
            log.error("No class found for {}. Notification {} will be ignored", notif.getClassNameFilter(), notif.getId());
        }

    }

    /**
     * Remove notification from cache
     * 
     * @param notif Notification to remove
     */
    public void removeNotificationFromCache(Notification notif) {
        String cacheKey = notif.getEventTypeFilter().name(); // notif.getProvider().getId() + "_" + notif.getEventTypeFilter().name();
        for (Class<BusinessEntity> c : eventNotificationCache.get(cacheKey).keySet()) {
            eventNotificationCache.get(cacheKey).get(c).remove(notif);
            log.info("Remove notification {} from notification cache", notif);
        }
    }

    /**
     * Update notification in cache
     * 
     * @param notif Notification to upate
     */
    public void updateNotificationInCache(Notification notif) {
        removeNotificationFromCache(notif);
        addNotificationToCache(notif);
    }

    /**
     * Get a list of notifications that match event type and entity class
     * 
     * @param eventType Event type
     * @param entity Entity involved
     * @return A list of notifications
     */
    public List<Notification> getApplicableNotifications(NotificationEventTypeEnum eventType, Serializable entity) {
        List<Notification> notifications = new ArrayList<Notification>();

        String cacheKey = eventType.name(); // entity.getProvider().getId() + "_" + eventType.name();
        for (Class<BusinessEntity> c : eventNotificationCache.get(cacheKey).keySet()) {
            if (c.isAssignableFrom(entity.getClass())) {
                notifications.addAll(eventNotificationCache.get(cacheKey).get(c));
            }
        }
        return notifications;
    }
}