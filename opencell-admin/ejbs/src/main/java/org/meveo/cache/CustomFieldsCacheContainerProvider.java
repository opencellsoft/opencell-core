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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.SerializationUtils;
import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.PersistenceUtils;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.catalog.CalendarBanking;
import org.meveo.model.catalog.CalendarDaily;
import org.meveo.model.catalog.CalendarInterval;
import org.meveo.model.catalog.CalendarYearly;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.crm.impl.CustomFieldException;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.slf4j.Logger;

/**
 * Provides cache related services (loading, update) for custom field value related operations
 * 
 * @author Andrius Karpavicius
 * @author Wassim Drira
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class CustomFieldsCacheContainerProvider implements Serializable { // CacheContainerProvider, Serializable {

    private static final long serialVersionUID = 180156064688145292L;

    @Inject
    protected Logger log;

    @EJB
    private CustomFieldInstanceService customFieldInstanceService;

    @EJB
    private CustomFieldTemplateService customFieldTemplateService;

    @EJB
    private CustomEntityTemplateService customEntityTemplateService;

    private ParamBean paramBean = ParamBeanFactory.getAppScopeInstance();

    private static boolean useCFTCache = true;
    private static boolean useCETCache = true;

    /**
     * Groups custom field templates applicable to the same entity type. Key format: &lt;custom field template appliesTo code&gt;. Value is a map of custom field templates
     * identified by a template code
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-cft-cache")
    private Cache<CacheKeyStr, Map<String, CustomFieldTemplate>> cftsByAppliesTo;

    /**
     * Contains custom entity templates.Key format: &lt;CET code&gt;, value: &lt;CustomEntityTemplate&gt;
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-cet-cache")
    private Cache<CacheKeyStr, CustomEntityTemplate> cetsByCode;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    static {
        ParamBean tmpParamBean = ParamBeanFactory.getAppScopeInstance();
        useCFTCache = Boolean.parseBoolean(tmpParamBean.getProperty("cache.cacheCFT", "true"));
        useCETCache = Boolean.parseBoolean(tmpParamBean.getProperty("cache.cacheCET", "true"));
    }

    /**
     * Populate custom field template cache.
     */
    private void populateCFTCache() {

        if (!useCFTCache) {
            log.info("CFT cache population will be skipped as cache will not be used");
            return;
        }

        boolean prepopulateCFTCache = Boolean.parseBoolean(paramBean.getProperty("cache.cacheCFT.prepopulate", "true"));

        if (!prepopulateCFTCache) {
            log.info("CFT cache pre-population will be skipped");
            return;
        }

        String currentProvider = currentUser.getProviderCode();

        log.debug("Start to pre-populate CFT cache for provider {}", currentProvider);

        CacheKeyStr lastAppliesTo = null;
        Map<String, CustomFieldTemplate> cftsSameAppliesTo = null;

        List<CustomFieldTemplate> cfts = customFieldTemplateService.getCFTForCache();
        for (CustomFieldTemplate cft : cfts) {

            CacheKeyStr cacheKeyByAppliesTo = getCFTCacheKeyByAppliesTo(cft);

            if (lastAppliesTo == null) {
                cftsSameAppliesTo = new TreeMap<String, CustomFieldTemplate>();
                lastAppliesTo = cacheKeyByAppliesTo;

            } else if (!lastAppliesTo.equals(cacheKeyByAppliesTo)) {
                cftsByAppliesTo.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(lastAppliesTo, cftsSameAppliesTo);
                cftsSameAppliesTo = new TreeMap<String, CustomFieldTemplate>();
                lastAppliesTo = cacheKeyByAppliesTo;
            }

            if (cft.getCalendar() != null) {
                cft.setCalendar(PersistenceUtils.initializeAndUnproxy(cft.getCalendar()));
                if (cft.getCalendar() instanceof CalendarDaily) {
                    ((CalendarDaily) cft.getCalendar()).setHours(PersistenceUtils.initializeAndUnproxy(((CalendarDaily) cft.getCalendar()).getHours()));
                } else if (cft.getCalendar() instanceof CalendarYearly) {
                    ((CalendarYearly) cft.getCalendar()).setDays(PersistenceUtils.initializeAndUnproxy(((CalendarYearly) cft.getCalendar()).getDays()));
                } else if (cft.getCalendar() instanceof CalendarInterval) {
                    ((CalendarInterval) cft.getCalendar()).setIntervals(PersistenceUtils.initializeAndUnproxy(((CalendarInterval) cft.getCalendar()).getIntervals()));
                } else if (cft.getCalendar() instanceof CalendarBanking) {
                    ((CalendarBanking)  cft.getCalendar()).setHolidays((PersistenceUtils.initializeAndUnproxy(((CalendarBanking) cft.getCalendar()).getHolidays())));
                }
            }
            if (cft.getListValues() != null) {
                cft.getListValues().values().toArray(new String[] {});
            }

            customFieldTemplateService.detach(cft);

            cftsSameAppliesTo.put(cft.getCode(), cft);
        }

        if (cftsSameAppliesTo != null && !cftsSameAppliesTo.isEmpty()) {
            cftsByAppliesTo.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(lastAppliesTo, cftsSameAppliesTo);
        }

        log.info("CFT cache populated with {} values of provider {}.", cfts.size(), currentProvider);

    }

    /**
     * Populate custom entity template cache.
     */
    private void populateCETCache() {

        if (!useCETCache) {
            log.info("CET cache population will be skipped as cache will not be used");
            return;
        }

        boolean prepopulateCETCache = Boolean.parseBoolean(paramBean.getProperty("cache.cacheCET.prepopulate", "true"));

        if (!prepopulateCETCache) {
            log.info("CET cache pre-population will be skipped");
            return;
        }

        String currentProvider = currentUser.getProviderCode();

        log.debug("Start to pre-populate CET cache for provider {}", currentProvider);

        // Cache custom entity templates sorted by a cet.name
        List<CustomEntityTemplate> allCets = customEntityTemplateService.getCETForCache();

        for (CustomEntityTemplate cet : allCets) {
            customEntityTemplateService.detach(cet);
            cetsByCode.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(new CacheKeyStr(currentProvider, cet.getCode()), cet);
        }

        log.info("CET cache populated with {} values of provider {}.", allCets.size(), currentProvider);
    }

    /**
     * Get a summary of cached information.
     * 
     * @return A map containing cache information with cache name as a key and cache as a value
     */
    // @Override
    @SuppressWarnings("rawtypes")
    public Map<String, Cache> getCaches() {
        Map<String, Cache> summaryOfCaches = new HashMap<String, Cache>();
        summaryOfCaches.put(cftsByAppliesTo.getName(), cftsByAppliesTo);
        summaryOfCaches.put(cetsByCode.getName(), cetsByCode);

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

        if (cacheName == null || cacheName.equals(cftsByAppliesTo.getName()) || cacheName.contains(cftsByAppliesTo.getName())) {
            cftsByAppliesToClear();
            populateCFTCache();
        }

        if (cacheName == null || cacheName.equals(cetsByCode.getName()) || cacheName.contains(cetsByCode.getName())) {
            cetsByCodeClear();
            populateCETCache();
        }
    }

    /**
     * Populate cache by name
     * 
     * @param cacheName Name of cache to populate or null to populate all caches
     */
    // @Override
    public void populateCache(String cacheName) {

        if (cacheName == null || cacheName.equals(cftsByAppliesTo.getName()) || cacheName.contains(cftsByAppliesTo.getName())) {
            populateCFTCache();
        }

        if (cacheName == null || cacheName.equals(cetsByCode.getName()) || cacheName.contains(cetsByCode.getName())) {
            populateCETCache();
        }
    }

    /**
     * Store mapping between CF code and value storage in cache time period and cache by CFT appliesTo value.
     * 
     * @param cft Custom field template definition
     */
    public void addUpdateCustomFieldTemplate(CustomFieldTemplate cft) {

        if (!useCFTCache) {
            return;
        }

        CacheKeyStr cacheKeyByAppliesTo = getCFTCacheKeyByAppliesTo(cft);

        log.trace("Adding/updating custom field template {} for {} to CFT cache of Provider {}.", cft.getCode(), cacheKeyByAppliesTo, currentUser.getProviderCode());

        Map<String, CustomFieldTemplate> cftsOld = cftsByAppliesTo.getAdvancedCache().withFlags(Flag.FORCE_WRITE_LOCK).get(cacheKeyByAppliesTo);
        Map<String, CustomFieldTemplate> cfts = new TreeMap<String, CustomFieldTemplate>();
        if (cftsOld != null) {
            cfts.putAll(cftsOld);
        }

        // Load calendar for lazy loading
        if (cft.getCalendar() != null) {
            cft.setCalendar(PersistenceUtils.initializeAndUnproxy(cft.getCalendar()));
            if (cft.getCalendar() instanceof CalendarDaily) {
                ((CalendarDaily) cft.getCalendar()).setHours(PersistenceUtils.initializeAndUnproxy(((CalendarDaily) cft.getCalendar()).getHours()));
                ((CalendarDaily) cft.getCalendar()).nextCalendarDate(new Date());
            } else if (cft.getCalendar() instanceof CalendarYearly) {
                ((CalendarYearly) cft.getCalendar()).setDays(PersistenceUtils.initializeAndUnproxy(((CalendarYearly) cft.getCalendar()).getDays()));
                ((CalendarYearly) cft.getCalendar()).nextCalendarDate(new Date());
            } else if (cft.getCalendar() instanceof CalendarInterval) {
                ((CalendarInterval) cft.getCalendar()).setIntervals(PersistenceUtils.initializeAndUnproxy(((CalendarInterval) cft.getCalendar()).getIntervals()));
                ((CalendarInterval) cft.getCalendar()).nextCalendarDate(new Date());
            } else if (cft.getCalendar() instanceof CalendarBanking) {
                ((CalendarBanking)  cft.getCalendar()).setHolidays((PersistenceUtils.initializeAndUnproxy(((CalendarBanking) cft.getCalendar()).getHolidays())));
                ((CalendarInterval) cft.getCalendar()).nextCalendarDate(new Date());
            }
        }
        if (cft.getListValues() != null) {
            cft.getListValues().values().toArray(new String[] {});
        }

        cft = SerializationUtils.clone(cft);

        cfts.put(cft.getCode(), cft);
        cftsByAppliesTo.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cacheKeyByAppliesTo, cfts);
    }

    /**
     * Remove mapping between CF code and value storage in cache time period and remove from cache by CFT appliesTo value.
     * 
     * @param cft Custom field template definition
     */
    public void removeCustomFieldTemplate(CustomFieldTemplate cft) {

        if (!useCFTCache) {
            return;
        }

        CacheKeyStr cacheKeyByAppliesTo = getCFTCacheKeyByAppliesTo(cft);

        String currentProvider = currentUser.getProviderCode();
        log.trace("Removing custom field template {} for {} from CFT cache of Provider {}.", cft.getCode(), cacheKeyByAppliesTo, currentProvider);

        Map<String, CustomFieldTemplate> cftsOld = cftsByAppliesTo.getAdvancedCache().withFlags(Flag.FORCE_WRITE_LOCK).get(cacheKeyByAppliesTo);

        if (cftsOld != null && cftsOld.containsKey(cft.getCode())) {

            Map<String, CustomFieldTemplate> cfts = new TreeMap<String, CustomFieldTemplate>(cftsOld);
            cfts.remove(cft.getCode());

            // If no value are left in the map - LEAVE, as cache can be populated at runtime
            // instead of at application start and need to distinguish
            // between not cached key and key with no records
            cftsByAppliesTo.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cacheKeyByAppliesTo, cfts);

            log.trace("Removed custom field template {} for {} from CFT cache for Provider {}.", cft.getCode(), cacheKeyByAppliesTo, currentProvider);
        }
    }

    /**
     * Mark in cache that there are no custom field templates cached under this cache key
     * 
     * @param appliesTo AlliesTo value
     */
    public void markNoCustomFieldTemplates(String appliesTo) {

        CacheKeyStr cacheKeyByAppliesTo = new CacheKeyStr(currentUser.getProviderCode(), appliesTo);
        if (!cftsByAppliesTo.getAdvancedCache().containsKey(cacheKeyByAppliesTo)) {
            cftsByAppliesTo.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cacheKeyByAppliesTo, new HashMap<String, CustomFieldTemplate>());
        }
    }

    /**
     * Store custom entity template to cache.
     * 
     * @param cet Custom entity template definition
     */
    public void addUpdateCustomEntityTemplate(CustomEntityTemplate cet) {

        if (!useCETCache) {
            return;
        }

        log.trace("Adding CET template {} to CET cache", cet.getCode());

        cetsByCode.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(new CacheKeyStr(currentUser.getProviderCode(), cet.getCode()), cet);

        // Sort values by cet.name
        // Collections.sort(cetsByProvider);

    }

    /**
     * Remove custom entity template from cache.
     * 
     * @param cet Custom entity template definition
     */
    public void removeCustomEntityTemplate(CustomEntityTemplate cet) {

        if (!useCETCache) {
            return;
        }

        log.trace("Removing CET template {} from CET cache", cet.getCode());

        cetsByCode.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).remove(new CacheKeyStr(currentUser.getProviderCode(), cet.getCode()));
    }

    private CacheKeyStr getCFTCacheKeyByAppliesTo(CustomFieldTemplate cft) {
        CacheKeyStr key = new CacheKeyStr(currentUser.getProviderCode(), cft.getAppliesTo());
        return key;
    }

    /**
     * Get custom field templates for a given entity (appliesTo value).
     * 
     * @param appliesTo entity (appliesTo value)
     * @return A map of custom field templates with template code as a key or NULL if cache key not found
     */
    public Map<String, CustomFieldTemplate> getCustomFieldTemplates(String appliesTo) {
        CacheKeyStr key = new CacheKeyStr(currentUser.getProviderCode(), appliesTo);
        Map<String, CustomFieldTemplate> cfts = cftsByAppliesTo.get(key);
        return cfts;
    }

    /**
     * Get custom entity templates
     * 
     * @return A list of custom entity templates
     */
    public Collection<CustomEntityTemplate> getCustomEntityTemplates() {
    	String providerCode=currentUser.getProviderCode();
    	return cetsByCode.entrySet().stream()
    			.filter(x-> ((providerCode==null && x.getKey().getProvider()==null) 
    					|| (providerCode!=null && providerCode.equals(x.getKey().getProvider()))))
    			.map(x -> x.getValue()).collect(()-> Collectors.toList());
    }

    /**
     * Get custom entity template by code
     * 
     * @param code Custom entity template code
     * @return Custom entity template or NULL if not found
     */
    public CustomEntityTemplate getCustomEntityTemplate(String code) {
        CacheKeyStr key = new CacheKeyStr(currentUser.getProviderCode(), code);
        return cetsByCode.get(key);
    }

    /**
     * Get custom field template of a given code, applicable to a given entity
     * 
     * @param code Custom field template code
     * @param entity Entity
     * @return Custom field template
     */
    public CustomFieldTemplate getCustomFieldTemplate(String code, ICustomFieldEntity entity) {
        try {
            return getCustomFieldTemplate(code, CustomFieldTemplateService.calculateAppliesToValue(entity));

        } catch (CustomFieldException e) {
            log.error("Can not determine applicable CFT type for entity of {} class.", entity.getClass().getSimpleName());
        }
        return null;
    }

    /**
     * Get custom field template of a given code, applicable to a given entity
     * 
     * @param code Custom field template code
     * @param appliesTo Entity appliesTo value
     * @return Custom field template or NULL if not found
     */
    public CustomFieldTemplate getCustomFieldTemplate(String code, String appliesTo) {

        Map<String, CustomFieldTemplate> cfts = getCustomFieldTemplates(appliesTo);
        if (cfts != null) {
            return cfts.get(code);
        }
        return null;
    }

    /**
     * Clear the data belonging to the current provider from cache
     */
    public void cetsByCodeClear() {
        String currentProvider = currentUser.getProviderCode();
        log.debug("cetsByCodeClear() => " + currentProvider + ".");
        // cetsByCode.keySet().removeIf(key -> (key.getProvider() == null) ? currentProvider == null : key.getProvider().equals(currentProvider));
        Iterator<Entry<CacheKeyStr, CustomEntityTemplate>> iter = cetsByCode.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).entrySet().iterator();
        ArrayList<CacheKeyStr> itemsToBeRemoved = new ArrayList<>();
        while (iter.hasNext()) {
            Entry<CacheKeyStr, CustomEntityTemplate> entry = iter.next();
            boolean comparison = (entry.getKey().getProvider() == null) ? currentProvider == null : entry.getKey().getProvider().equals(currentProvider);
            if (comparison) {
                itemsToBeRemoved.add(entry.getKey());
            }
        }

        for (CacheKeyStr elem : itemsToBeRemoved) {
            log.debug("Remove element Provider:" + elem.getProvider() + " Key:" + elem.getKey() + ".");
            cetsByCode.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).remove(elem);
        }
    }

    /**
     * Clear all the data in CET cache
     */
    public void cetsByCodeClearAll() {
        cetsByCode.clear();
    }

    /**
     * Clear the data belonging to the current provider from cache
     */
    public void cftsByAppliesToClear() {
        String currentProvider = currentUser.getProviderCode();
        log.info("Clear CFTS cache for {}/{} ", currentProvider, currentUser);
        // cftsByAppliesTo.keySet().removeIf(key -> (key.getProvider() == null) ? currentProvider == null : key.getProvider().equals(currentProvider));
        Iterator<Entry<CacheKeyStr, Map<String, CustomFieldTemplate>>> iter = cftsByAppliesTo.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).entrySet().iterator();
        ArrayList<CacheKeyStr> itemsToBeRemoved = new ArrayList<>();
        while (iter.hasNext()) {
            Entry<CacheKeyStr, Map<String, CustomFieldTemplate>> entry = iter.next();
            boolean comparison = (entry.getKey().getProvider() == null) ? currentProvider == null : entry.getKey().getProvider().equals(currentProvider);
            if (comparison) {
                itemsToBeRemoved.add(entry.getKey());
            }
        }

        for (CacheKeyStr elem : itemsToBeRemoved) {
            log.debug("Remove element Provider:" + elem.getProvider() + " Key:" + elem.getKey() + ".");
            cftsByAppliesTo.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).remove(elem);
        }
    }

    /**
     * Clear all the data in CFT cache
     */
    public void cftsByAppliesToClearAll() {
        cftsByAppliesTo.clear();
    }
}