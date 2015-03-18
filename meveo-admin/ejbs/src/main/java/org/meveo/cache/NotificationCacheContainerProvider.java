package org.meveo.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

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

    @EJB
    private NotificationService notificationService;

    /**
     * Contains association between event type, entity class and notifications. Key format: <eventTypeFilter> //<provider id>_<eventTypeFilter>
     */
    // @Resource(lookup = "java:jboss/infinispan/cache/meveo/meveo-notification-cache")
    private BasicCache<String, HashMap<Class<BusinessEntity>, List<Notification>>> eventNotificationCache;

    @Resource(name = "java:jboss/infinispan/container/meveo")
    private CacheContainer meveoContainer;

    @PostConstruct
    private void init() {
        try {
            log.debug("NotificationCacheContainerProvider initializing...");
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
        log.info("Start to populate notification cache");
        List<Notification> activeNotifications = notificationService.getNotificationsForCache();
        for (Notification notif : activeNotifications) {
            addNotificationToCache(notif);
        }
        log.debug("Notification cache populated with {} notifications", activeNotifications.size());
    }

    /**
     * Add notification to a cache
     * 
     * @param notif Notification to add
     */
    public void addNotificationToCache(Notification notif) {

        try {
            String cacheKey = notif.getEventTypeFilter().name(); // notif.getProvider().getId() + "_" + notif.getEventTypeFilter().name();
            @SuppressWarnings("unchecked")
            Class<BusinessEntity> c = (Class<BusinessEntity>) Class.forName(notif.getClassNameFilter());
            eventNotificationCache.putIfAbsent(cacheKey, new HashMap<Class<BusinessEntity>, List<Notification>>());
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
        if (eventNotificationCache.containsKey(cacheKey)) {
            for (Class<BusinessEntity> c : eventNotificationCache.get(cacheKey).keySet()) {
                eventNotificationCache.get(cacheKey).get(c).remove(notif);
                log.info("Remove notification {} from notification cache", notif);
            }
        }
    }

    /**
     * Update notification in cache
     * 
     * @param notif Notification to update
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
        if (eventNotificationCache.containsKey(cacheKey)) {
            for (Class<BusinessEntity> c : eventNotificationCache.get(cacheKey).keySet()) {
                if (c.isAssignableFrom(entity.getClass())) {
                    notifications.addAll(eventNotificationCache.get(cacheKey).get(c));
                }
            }
        }
        return notifications;
    }
}