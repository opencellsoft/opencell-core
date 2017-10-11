package org.meveo.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.event.IEvent;
import org.meveo.model.AuditableEntity;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IEntity;
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
@Lock(LockType.READ)
public class NotificationCacheContainerProvider implements Serializable { // CacheContainerProvider, Serializable {

    private static final long serialVersionUID = 358151068726872948L;

    @Inject
    protected Logger log;

    @EJB
    private NotificationService notificationService;

    /**
     * Contains association between event type, entity class and notifications. Key format: <eventTypeFilter>-<entity class>
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-notification-cache")
    private Cache<String, List<Notification>> eventNotificationCache;

    // @Resource(name = "java:jboss/infinispan/container/meveo")
    // private CacheContainer meveoContainer;

    @PostConstruct
    private void init() {
        try {
            log.debug("NotificationCacheContainerProvider initializing...");
            // eventNotificationCache = meveoContainer.getCache("meveo-notification-cache");

            refreshCache(System.getProperty(CacheContainerProvider.SYSTEM_PROPERTY_CACHES_TO_LOAD));

            log.info("NotificationCacheContainerProvider initialized");

        } catch (Exception e) {
            log.error("NotificationCacheContainerProvider init() error", e);
            throw e;
        }
    }

    /**
     * Populate notification cache
     */
    private void populateNotificationCache() {
        log.debug("Start to populate notification cache");

        eventNotificationCache.clear();

        List<Notification> activeNotifications = notificationService.getNotificationsForCache();
        for (Notification notif : activeNotifications) {
            addNotificationToCache(notif);
        }

        log.info("Notification cache populated with {} notifications", activeNotifications.size());
    }

    /**
     * Add notification to a cache
     * 
     * @param notif Notification to add
     */
    // @Lock(LockType.WRITE)
    public void addNotificationToCache(Notification notif) {

        String cacheKey = getCacheKey(notif);

        log.trace("Adding notification {} to notification cache under key {}", notif.getId(), cacheKey);
        // Solve lazy loading issues when firing notification
        if (notif.getScriptInstance() != null) {
            notif.getScriptInstance().getCode();
        }

        try {

            List<Notification> notificationsOld = eventNotificationCache.getAdvancedCache().withFlags(Flag.FORCE_WRITE_LOCK).get(cacheKey);

            List<Notification> notifications = new ArrayList<Notification>();
            if (notificationsOld != null) {
                notifications.addAll(notificationsOld);
            }
            notifications.add(notif);
            eventNotificationCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cacheKey, notifications);

        } catch (Exception e) {
            log.error("Failed to add Notification {} to cache under key {}", notif.getId(), cacheKey);
        }
    }

    /**
     * Remove notification from cache
     * 
     * @param notif Notification to remove
     */
    public void removeNotificationFromCache(Notification notif) {

        String cacheKey = getCacheKey(notif);

        log.trace("Removing notification {} from notification cache under key {}", notif.getId(), cacheKey);

        List<Notification> notifsOld = eventNotificationCache.getAdvancedCache().withFlags(Flag.FORCE_WRITE_LOCK).get(cacheKey);

        if (notifsOld != null && !notifsOld.isEmpty()) {
            List<Notification> notifs = new ArrayList<>(notifsOld);
            boolean removed = notifs.remove(notif);
            if (removed) {
                // Remove cached value altogether if no value are left in the list
                if (notifs.isEmpty()) {
                    eventNotificationCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).remove(cacheKey);
                } else {
                    eventNotificationCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cacheKey, notifs);
                }
                log.trace("Removed notification {} from notification cache under key {}", notif.getId(), cacheKey);
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
     * @param entityOrEvent Entity involved or event containing the entity involved
     * @return A list of notifications
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<Notification> getApplicableNotifications(NotificationEventTypeEnum eventType, Object entityOrEvent) {

        Object entity = null;
        if (entityOrEvent instanceof IEntity) {
            entity = (IEntity) entityOrEvent;
        } else if (entityOrEvent instanceof IEvent) {
            entity = (IEntity) ((IEvent) entityOrEvent).getEntity();
        } else {
            entity = entityOrEvent;
        }

        List<Notification> notifications = new ArrayList<Notification>();

        Class entityClass = entity.getClass();

        while (!entityClass.isAssignableFrom(BusinessCFEntity.class) && !entityClass.isAssignableFrom(BusinessEntity.class) && !entityClass.isAssignableFrom(BaseEntity.class)
                && !entityClass.isAssignableFrom(AuditableEntity.class) && !entityClass.isAssignableFrom(Object.class)) {
            String cacheKey = getCacheKey(eventType, entityClass);
            if (eventNotificationCache.containsKey(cacheKey)) {
                notifications.addAll(eventNotificationCache.get(cacheKey));

            }
            entityClass = entityClass.getSuperclass();
        }

        Collections.sort(notifications, (o1, o2) -> o1.getPriority() - o2.getPriority());

        return notifications;
    }

    /**
     * Get a summary of cached information
     * 
     * @return A list of a map containing cache information with cache name as a key and cache as a value
     */
    // @Override
    @SuppressWarnings("rawtypes")
    public Map<String, Cache> getCaches() {
        Map<String, Cache> summaryOfCaches = new HashMap<String, Cache>();
        summaryOfCaches.put(eventNotificationCache.getName(), eventNotificationCache);

        return summaryOfCaches;
    }

    /**
     * Refresh cache by name
     * 
     * @param cacheName Name of cache to refresh or null to refresh all caches
     */
    // @Override
    @Asynchronous
    public void refreshCache(String cacheName) {

        if (cacheName == null || cacheName.equals(eventNotificationCache.getName()) || cacheName.contains(eventNotificationCache.getName())) {
            populateNotificationCache();
        }
    }

    private String getCacheKey(Notification notif) {
        return notif.getEventTypeFilter().name() + "_" + notif.getClassNameFilter();
    }

    @SuppressWarnings("rawtypes")
    private String getCacheKey(NotificationEventTypeEnum eventType, Class entityClass) {
        return eventType.name() + "_" + ReflectionUtils.getCleanClassName(entityClass.getName());
    }
}