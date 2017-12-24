package org.meveo.cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.apache.commons.lang3.SerializationUtils;
import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.catalog.CalendarDaily;
import org.meveo.model.catalog.CalendarInterval;
import org.meveo.model.catalog.CalendarYearly;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.crm.impl.CustomFieldException;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.util.PersistenceUtils;
import org.slf4j.Logger;

/**
 * Provides cache related services (loading, update) for custom field value related operations
 * 
 * @author Andrius Karpavicius
 */
@Startup
@Singleton
@Lock(LockType.READ)
public class CustomFieldsCacheContainerProvider implements Serializable { // CacheContainerProvider, Serializable {

    private static final long serialVersionUID = 180156064688145292L;

    @Inject
    protected Logger log;

    @EJB
    private CustomFieldInstanceService customFieldInstanceService;

    @EJB
    private CustomFieldTemplateService customFieldTemplateService;

    @EJB
    CustomEntityTemplateService customEntityTemplateService;

    /**
     * Groups custom field templates applicable to the same entity type. Key format: <custom field template appliesTo code>. Value is a map of custom field templates identified by
     * a template code
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-cft-cache")
    private Cache<String, Map<String, CustomFieldTemplate>> cftsByAppliesTo;

    /**
     * Contains custom entity templates.Key format: <CET code>, value: <CustomEntityTemplate>
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-cet-cache")
    private Cache<String, CustomEntityTemplate> cetsByCode;

    @PostConstruct
    private void init() {
        try {
            log.debug("CustomFieldsCacheContainerProvider initializing...");
            // customFieldValueCache = meveoContainer.getCache("meveo-cfv-cache");

            refreshCache(System.getProperty(CacheContainerProvider.SYSTEM_PROPERTY_CACHES_TO_LOAD));

            log.info("CustomFieldsCacheContainerProvider initialized");

        } catch (Exception e) {
            log.error("CustomFieldsCacheContainerProvider init() error", e);
            throw e;
        }
    }

    /**
     * Populate custom field template cache
     */
    private void populateCFTCache() {

        // Start to populate custom field value caching time and appliesTo cache
        log.debug("Start to populate custom field value caching time and appliesTo cache");

        cftsByAppliesTo.clear();

        String lastAppliesTo = null;
        Map<String, CustomFieldTemplate> cftsSameAppliesTo = null;

        List<CustomFieldTemplate> cfts = customFieldTemplateService.getCFTForCache();
        for (CustomFieldTemplate cft : cfts) {

            String cacheKeyByAppliesTo = getCFTCacheKeyByAppliesTo(cft);

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

        log.info("CFT cache populated with {} values", cfts.size());

    }

    /**
     * Populate custom entity template cache
     */
    private void populateCETCache() {

        // Start to populate custom entity template cache
        log.debug("Start to populate custom entity template cache");

        cetsByCode.clear();

        // Cache custom entity templates sorted by a cet.name
        List<CustomEntityTemplate> allCets = customEntityTemplateService.getCETForCache();

        for (CustomEntityTemplate cet : allCets) {
            customEntityTemplateService.detach(cet);
            cetsByCode.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cet.getCode(), cet);
        }

        log.info("Custom entity template cache populated with {} values.", allCets.size());
    }

    /**
     * Get a summary of cached information
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
     * Refresh cache by name
     * 
     * @param cacheName Name of cache to refresh or null to refresh all caches
     */
    // @Override
    @Asynchronous
    public void refreshCache(String cacheName) {

        if (cacheName == null || cacheName.equals(cftsByAppliesTo.getName()) || cacheName.contains(cftsByAppliesTo.getName())) {
            populateCFTCache();
        }
        if (cacheName == null || cacheName.equals(cetsByCode.getName()) || cacheName.contains(cetsByCode.getName())) {
            populateCETCache();
        }
    }

    /**
     * Store mapping between CF code and value storage in cache time period and cache by CFT appliesTo value
     * 
     * @param cft Custom field template definition
     */
    public void addUpdateCustomFieldTemplate(CustomFieldTemplate cft) {

        String cacheKeyByAppliesTo = getCFTCacheKeyByAppliesTo(cft);

        log.trace("Adding/updating custom field template {} for {} to custom field template cache", cft.getCode(), cacheKeyByAppliesTo);

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
     * Remove mapping between CF code and value storage in cache time period and remove from cache by CFT appliesTo value
     * 
     * @param cft Custom field template definition
     */
    public void removeCustomFieldTemplate(CustomFieldTemplate cft) {

        String cacheKeyByAppliesTo = getCFTCacheKeyByAppliesTo(cft);

        log.trace("Removing custom field template {} for {} from custom field template cache", cft.getCode(), cacheKeyByAppliesTo);

        Map<String, CustomFieldTemplate> cftsOld = cftsByAppliesTo.getAdvancedCache().withFlags(Flag.FORCE_WRITE_LOCK).get(cacheKeyByAppliesTo);
        if (cftsOld != null && cftsOld.containsKey(cft.getCode())) {

            Map<String, CustomFieldTemplate> cfts = new TreeMap<String, CustomFieldTemplate>(cftsOld);
            cfts.remove(cft.getCode());

            // Remove cached value altogether if no value are left in the map
            if (cfts.isEmpty()) {
                cftsByAppliesTo.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).remove(cacheKeyByAppliesTo);
            } else {
                cftsByAppliesTo.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cacheKeyByAppliesTo, cfts);
            }

            log.trace("Removed custom field template {} for {} from custom field template cache", cft.getCode(), cacheKeyByAppliesTo);
        }
    }

    /**
     * Store custom entity template to cache
     * 
     * @param cet Custom entity template definition
     */
    public void addUpdateCustomEntityTemplate(CustomEntityTemplate cet) {

        log.trace("Adding CET template {} to custom entity template timeout cache", cet.getCode());

        cetsByCode.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cet.getCode(), cet);

        // Sort values by cet.name
        // Collections.sort(cetsByProvider);

    }

    /**
     * Remove custom entity template from cache
     * 
     * @param cet Custom entity template definition
     */
    public void removeCustomEntityTemplate(CustomEntityTemplate cet) {

        log.trace("Removing CET template {} from custom entity template timeout cache", cet.getCode());

        cetsByCode.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).remove(cet.getCode());
    }

    private String getCFTCacheKeyByAppliesTo(CustomFieldTemplate cft) {
        return cft.getAppliesTo();
    }

    /**
     * Get custom field templates for a given entity (appliesTo value).
     * 
     * @param appliesTo entity (appliesTo value)
     * @return A map of custom field templates with template code as a key
     */
    @Lock(LockType.READ)
    public Map<String, CustomFieldTemplate> getCustomFieldTemplates(String appliesTo) {
        String key = appliesTo;
        Map<String, CustomFieldTemplate> cfts = cftsByAppliesTo.get(key);
        if (cfts != null) {
            return cfts;
        }
        return new HashMap<String, CustomFieldTemplate>();

    }

    /**
     * Get custom entity templates
     * 
     * @return A list of custom entity templates
     */
    @Lock(LockType.READ)
    public Collection<CustomEntityTemplate> getCustomEntityTemplates() {

        return cetsByCode.values();
    }

    /**
     * Get custom entity template by code
     * 
     * @param code Custom entity template code
     * @return A list of custom entity templates
     */
    @Lock(LockType.READ)
    public CustomEntityTemplate getCustomEntityTemplate(String code) {

        return cetsByCode.get(code);
    }

    /**
     * Get custom field template of a given code, applicable to a given entity
     * 
     * @param code Custom field template code
     * @param entity Entity
     * @return Custom field template
     */
    @Lock(LockType.READ)
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
     * @return Custom field template
     */
    @Lock(LockType.READ)
    public CustomFieldTemplate getCustomFieldTemplate(String code, String appliesTo) {

        Map<String, CustomFieldTemplate> cfts = cftsByAppliesTo.get(appliesTo);
        if (cfts != null) {
            return cfts.get(code);
        }
        return null;
    }
}