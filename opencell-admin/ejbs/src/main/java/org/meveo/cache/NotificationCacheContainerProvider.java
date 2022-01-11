/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.cache;

import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.event.IEvent;
import org.meveo.model.AuditableEntity;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IEntity;
import org.meveo.model.notification.EmailNotification;
import org.meveo.model.notification.Notification;
import org.meveo.model.notification.NotificationEventTypeEnum;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.notification.GenericNotificationService;
import org.slf4j.Logger;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides cache related services (loading, update) for event notification related operations
 * 
 * @author Andrius Karpavicius
 * @author Wassim Drira
 * @author Abdellatif BARI
 * @author Mounir BAHIJE
 * @lastModifiedVersion 7.0
 *
 */
@Stateless
public class NotificationCacheContainerProvider implements Serializable { // CacheContainerProvider, Serializable {

    private static final long serialVersionUID = 358151068726872948L;

    @Inject
    protected Logger log;

    @EJB
    private GenericNotificationService notificationService;

    private ParamBean paramBean = ParamBeanFactory.getAppScopeInstance();

    private static boolean useNotificationCache = true;

    /**
     * Contains association between event type, entity class and notifications. Key format: &lt;eventTypeFilter&gt;-&lt;entity class&gt;
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-notification-cache")
    private Cache<CacheKeyStr, Set<Notification>> eventNotificationCache;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    static {
        ParamBean tmpParamBean = ParamBeanFactory.getAppScopeInstance();
        useNotificationCache = Boolean.parseBoolean(tmpParamBean.getProperty("cache.cacheNotification", "true"));
    }

    /**
     * Populate notification cache.
     */
    private void populateNotificationCache() {

        if (!useNotificationCache) {
            log.info("Notification cache population will be skipped as cache will not be used");
            return;
        }

        boolean prepopulateNotificationCache = Boolean.parseBoolean(paramBean.getProperty("cache.cacheNotification.prepopulate", "true"));

        if (!prepopulateNotificationCache) {
            log.info("Notification cache pre-population will be skipped");
            return;
        }

        String provider = currentUser.getProviderCode();

        log.debug("Start to pre-populate Notification cache for provider {}.", provider);

        List<Notification> activeNotifications = notificationService.getNotificationsForCache();
        for (Notification notif : activeNotifications) {
            addNotificationToCache(notif);
        }

        log.info("Notification cache populated with {} notifications for provider {}.", activeNotifications.size(), provider);
    }

    /**
     * Add notification to a cache.
     * 
     * @param notif Notification to add
     */
    // @Lock(LockType.WRITE)
    public void addNotificationToCache(Notification notif) {

        if (!useNotificationCache) {
            return;
        }
        CacheKeyStr cacheKey = getCacheKey(notif, false);

        log.trace("Adding notification {} to notification cache under key {}", notif.getId(), cacheKey);
        // Solve lazy loading issues when firing notification
        if (notif.getScriptInstance() != null) {
            notif.getScriptInstance().getCode();
        }
        if (notif instanceof EmailNotification) {
            if (((EmailNotification) notif).getEmailTemplate() != null) {
                ((EmailNotification) notif).getEmailTemplate().getCode();
            }
        }

        try {

            Set<Notification> notificationsOld = eventNotificationCache.getAdvancedCache().withFlags(Flag.FORCE_WRITE_LOCK).get(cacheKey);

            Set<Notification> notifications = new HashSet<Notification>();
            notifications.add(notif);
            if (notificationsOld != null) {
                notifications.addAll(notificationsOld);
            }
            eventNotificationCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cacheKey, notifications);

        } catch (Exception e) {
            log.error("Failed to add Notification {} to cache under key {}", notif.getId(), cacheKey);
        }
    }

    /**
     * Remove notification from cache.
     * 
     * @param notif Notification to remove
     */
    public void removeNotificationFromCache(Notification notif) {

        if (!useNotificationCache) {
            return;
        }

        CacheKeyStr cacheKey = getCacheKey(notif, true);

        log.trace("Removing notification {} from notification cache under key {}", notif.getId(), cacheKey);

        Set<Notification> notifsOld = eventNotificationCache.getAdvancedCache().withFlags(Flag.FORCE_WRITE_LOCK).get(cacheKey);

        if (notifsOld != null && !notifsOld.isEmpty()) {
            Set<Notification> notifs = new HashSet<>(notifsOld);
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

        if (!useNotificationCache) {
            return;
        }

        removeNotificationFromCache(notif);
        if (notif.isActive()) {
            addNotificationToCache(notif);
        }
    }

    /**
     * Get a list of notifications that match event type and entity class. Entity class hierarchy up is consulted if notifications are set on a parent class
     * 
     * @param eventType Event type
     * @param entityOrEvent Entity involved or event containing the entity involved
     * @return A list of notifications. A NULL is returned if cache was not prepopulated at application startup and cache contains no entry for a base entity passed.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<Notification> getApplicableNotifications(NotificationEventTypeEnum eventType, Object entityOrEvent) {

        Object entity = getEntity(entityOrEvent);

        List<Notification> notifications = new ArrayList<Notification>();

        Class entityClass = entity.getClass();
        int i = 0;

        while (!entityClass.isAssignableFrom(BusinessCFEntity.class) && !entityClass.isAssignableFrom(BusinessEntity.class) && !entityClass.isAssignableFrom(BaseEntity.class)
                && !entityClass.isAssignableFrom(AuditableEntity.class) && !entityClass.isAssignableFrom(Object.class)) {

            CacheKeyStr cacheKey = getCacheKey(eventType, entityClass);
            if (eventNotificationCache.containsKey(cacheKey)) {
                notifications.addAll(eventNotificationCache.get(cacheKey));

                // If cache was not prepopulated or cache record was removed by cache itself (limit or cache entries, expiration, etc..)
                // and there is no cache entry for the base class, then return null, as cache needs to be populated first
                // TODO there could be a problem that a cache record for parent class is expired by cache. There should be no cache limit/expiration for Notification cache
            } else if (i == 0) {
                return null;
            }
            entityClass = entityClass.getSuperclass();
            i++;
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
     * Refresh cache by name. Removes current provider's data from cache and populates it again
     * 
     * @param cacheName Name of cache to refresh or null to refresh all caches
     */
    // @Override
    @Asynchronous
    public void refreshCache(String cacheName) {

        if (cacheName == null || cacheName.equals(eventNotificationCache.getName()) || cacheName.contains(eventNotificationCache.getName())) {
            eventNotificationCache.clear();
            populateNotificationCache();
        }
    }

    /**
     * Populate cache by name
     * 
     * @param cacheName Name of cache to populate or null to populate all caches
     */
    // @Override
    public void populateCache(String cacheName) {

        if (cacheName == null || cacheName.equals(eventNotificationCache.getName()) || cacheName.contains(eventNotificationCache.getName())) {
            populateNotificationCache();
        }
    }

    private CacheKeyStr getCacheKey(Notification notif, boolean isUpdate) {
        String key = notif.getOldEventTypeFilter().name() + "_" + notif.getOldClassNameFilter();
        if(!isUpdate) {
        	key = notif.getEventTypeFilter().name() + "_" + notif.getClassNameFilter();
        }
        return new CacheKeyStr(currentUser.getProviderCode(), key);
    }

    @SuppressWarnings("rawtypes")
    private CacheKeyStr getCacheKey(NotificationEventTypeEnum eventType, Class entityClass) {
        String key = eventType.name() + "_" + ReflectionUtils.getCleanClassName(entityClass.getName());
        return new CacheKeyStr(currentUser.getProviderCode(), key);
    }

    /**
     * Mark in cache that there are no notifications cached for this base entity class and event
     * 
     * @param eventType Event type
     * @param entityOrEvent Entity involved or event containing the entity involved
     */
    public void markNoNotifications(NotificationEventTypeEnum eventType, Object entityOrEvent) {

        Object entity = getEntity(entityOrEvent);

        CacheKeyStr cacheKey = getCacheKey(eventType, entity.getClass());
        if (!eventNotificationCache.containsKey(cacheKey)) {
            eventNotificationCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).putIfAbsent(cacheKey, new HashSet<Notification>());
        }
    }

    /**
     * Get the entity
     * 
     * @param entityOrEvent entity or event
     * @return entity
     */
    public Object getEntity(Object entityOrEvent) {
        // Determine a base entity
        Object entity = null;
        if (entityOrEvent instanceof IEntity) {
            entity = (IEntity) entityOrEvent;
        } else if (entityOrEvent instanceof IEvent) {
            entity = (IEntity) ((IEvent) entityOrEvent).getEntity();
        } else {
            entity = entityOrEvent;
        }
        return entity;
    }
}