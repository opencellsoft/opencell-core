package org.meveo.cache;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.catalog.CalendarDaily;
import org.meveo.model.catalog.CalendarInterval;
import org.meveo.model.catalog.CalendarYearly;
import org.meveo.model.crm.CustomFieldInstance;
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
     * How long custom field period value should be cached. Key format: <custom field template code>. Value is a number of days to cache custom field value
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-cft-timeout-cache")
    private Cache<String, Integer> cfValueCacheTime;

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

    /**
     * Contains association between entity, and custom field value(s). Key format: <entity class>_<entity id>. Value is a map where key is CFI code and value is a list of values
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-cfv-cache")
    private Cache<String, Map<String, List<CachedCFPeriodValue>>> customFieldValueCache;

    // @Resource(name = "java:jboss/infinispan/container/meveo")
    // private CacheContainer meveoContainer;

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

        cfValueCacheTime.startBatch();
        cftsByAppliesTo.startBatch();

        cfValueCacheTime.clear();
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
                cft.getListValues().values().toArray(new String[]{});
            }

            customFieldTemplateService.detach(cft);

            cftsSameAppliesTo.put(cft.getCode(), cft);

            // Remember custom field versionable value cache time
            if (cft.isVersionable() && cft.getCacheValueTimeperiod() != null) {
                cfValueCacheTime.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(getCFTCacheKeyByAppliesToAndCode(cft), cft.getCacheValueTimeperiod());

                Calendar calendar = new GregorianCalendar();
                calendar.add(Calendar.DATE, (-1) * cft.getCacheValueTimeperiod().intValue());
            }
        }

        if (cftsSameAppliesTo != null && !cftsSameAppliesTo.isEmpty()) {
            cftsByAppliesTo.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(lastAppliesTo, cftsSameAppliesTo);
        }

        cfValueCacheTime.endBatch(true);
        cftsByAppliesTo.endBatch(true);

        log.info("Custom field value caching time populated with {} values. CFT cache populated with {} values", cfValueCacheTime.size(), cfts.size());

    }

    /**
     * Populate custom entity template cache
     */
    private void populateCETCache() {

        // Start to populate custom entity template cache
        log.debug("Start to populate custom entity template cache");

        cetsByCode.startBatch();
        cetsByCode.clear();

        // Cache custom entity templates sorted by a cet.name
        List<CustomEntityTemplate> allCets = customEntityTemplateService.getCETForCache();

        for (CustomEntityTemplate cet : allCets) {
            customEntityTemplateService.detach(cet);
            cetsByCode.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cet.getCode(), cet);
        }

        cetsByCode.endBatch(true);
        log.info("Custom entity template cache populated with {} values.", allCets.size());
    }

    private void populateCFValueCache() {

        // Start to populate custom field value cache
        log.debug("Start to populate custom field value cache");

        // Calculate cache storage cuttoff time for each CFT
        Map<String, Date> cfValueCacheTimeAsDate = new HashMap<String, Date>();

        for (Entry<String, Integer> timeoutInfo : cfValueCacheTime.entrySet()) {

            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DATE, (-1) * timeoutInfo.getValue().intValue());

            cfValueCacheTimeAsDate.put(timeoutInfo.getKey().split("_")[1], calendar.getTime()); // Need to split and retrieve field code only, as later in code when calculating
                                                                                                // timeout date, CFI does not know what CFT it is related to. TODO need to fix this
        }

        customFieldValueCache.startBatch();
        customFieldValueCache.clear();

        int cfiCount = 0;

        String lastCacheKey = null;
        Map<String, List<CachedCFPeriodValue>> cfvSameUUID = null;

        // Get a list of entity id and CFI instances for a given entity class
        List<CustomFieldInstance> cfis = customFieldInstanceService.getCFIForCache();

        for (CustomFieldInstance cfi : cfis) {

            String cacheKey = getCacheKey(cfi);

            if (lastCacheKey == null) {
                cfvSameUUID = new TreeMap<String, List<CachedCFPeriodValue>>();
                lastCacheKey = cacheKey;

            } else if (!lastCacheKey.equals(cacheKey)) {
                customFieldValueCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(lastCacheKey, cfvSameUUID);
                cfvSameUUID = new TreeMap<String, List<CachedCFPeriodValue>>();
                lastCacheKey = cacheKey;
            }

            // CustomFieldTemplate cft = getCustomFieldTemplate(cfi.getCode(), cfi.getAppliesToEntity());
            // if (!cft.isCacheValue()) {
            // continue;
            // }

            // log.trace("Add CustomFieldInstance {} to CustomFieldInstance cache for entity uuid {}", cfi.getCode(), cacheKey);

            CachedCFPeriodValue cfvValue = convertFromCFI(cfi, cfValueCacheTimeAsDate.get(cfi.getCode()));
            if (cfvValue != null) {
                cfvSameUUID.putIfAbsent(cfi.getCode(), new ArrayList<CachedCFPeriodValue>());
                cfvSameUUID.get(cfi.getCode()).add(cfvValue);
                cfiCount++;
            }
        }

        if (cfvSameUUID != null && !cfvSameUUID.isEmpty()) {
            customFieldValueCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(lastCacheKey, cfvSameUUID);
        }

        customFieldValueCache.endBatch(true);

        log.info("Custom field value cache populated with {} values", cfiCount);
    }

    /**
     * Add or update custom field instance value for an entity in cache.
     * 
     * @param cfi Entity with custom fields
     */
    public void addUpdateCustomFieldInCache(ICustomFieldEntity entity, CustomFieldInstance cfi) {

        String cacheKey = getCacheKey(cfi);
        CustomFieldTemplate cft = getCustomFieldTemplate(cfi.getCode(), entity);

        // // Nothing to do if field is not cacheable
        // if (!cft.isCacheValue()) {
        // return;
        // }

        Object value = cfi.getValue();

        // Do not store null values if no default value is provided (otherwise the next get will instantiate CF value again)
        if (value == null && cft.getDefaultValue() == null) {
            removeCustomFieldFromCache(entity, cfi.getCode());

        } else {

            // If value is still cacheable - update (remove and add) the value
            CachedCFPeriodValue cfvValue = convertFromCFI(cfi, calculateCutoffDate(cft));
            if (cfvValue != null) {

                log.trace("Updating custom field {} from CustomFieldInstance cache for entity uuid {}", cfi.getCode(), cacheKey);

                Map<String, List<CachedCFPeriodValue>> entityCFValues = customFieldValueCache.getAdvancedCache().withFlags(Flag.FORCE_WRITE_LOCK).get(cacheKey);
                if (entityCFValues == null) {
                    entityCFValues = new TreeMap<String, List<CachedCFPeriodValue>>();
                }

                entityCFValues.putIfAbsent(cfi.getCode(), new ArrayList<CachedCFPeriodValue>());
                entityCFValues.get(cfi.getCode()).remove(cfvValue);
                entityCFValues.get(cfi.getCode()).add(cfvValue);
                customFieldValueCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cacheKey, entityCFValues);

                // If no longer cacheable - remove it. If nothing left, remove altogether cache entry for and entity.
            } else {
                removeCustomFieldFromCache(cfi);
            }
        }
    }

    /**
     * Remove all custom field instances from cache for a given entity
     * 
     * @param entity Entity with custom fields
     */
    public void removeCustomFieldsFromCache(ICustomFieldEntity entity) {

        String cacheKey = getCacheKey(entity);

        log.trace("Removing all custom fields from CustomFieldInstance cache for entity uuid {}", cacheKey);

        customFieldValueCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).remove(cacheKey);
    }

    /**
     * Remove a particular custom field instance from cache
     * 
     * @param cfi Custom field instance
     */
    public void removeCustomFieldFromCache(CustomFieldInstance cfi) {
        String cacheKey = cfi.getAppliesToEntity();

        CachedCFPeriodValue cfvValue = convertFromCFI(cfi, null);

        log.trace("Removing custom field {} value from CustomFieldInstance cache for entity uuid {}. Value removed {}", cfi.getCode(), cacheKey, cfvValue);

        Map<String, List<CachedCFPeriodValue>> entityCFValues = customFieldValueCache.getAdvancedCache().withFlags(Flag.FORCE_WRITE_LOCK).get(cacheKey);

        // Remove no only CFI record, but also CF group if no CFIs are left after removal
        boolean needUpdate = false;
        if (entityCFValues != null && entityCFValues.containsKey(cfi.getCode())) {

            needUpdate = entityCFValues.get(cfi.getCode()).remove(cfvValue);

            if (needUpdate && entityCFValues.get(cfi.getCode()).isEmpty()) {
                entityCFValues.remove(cfi.getCode());
            }
        }
        if (needUpdate) {
            if (entityCFValues.isEmpty()) {
                customFieldValueCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).remove(cacheKey);
            } else {
                customFieldValueCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cacheKey, entityCFValues);
            }

            log.trace("Removed custom field {} value from CustomFieldInstance cache for entity uuid {}. Value removed {}", cfi.getCode(), cacheKey, cfvValue);
        }
    }

    /**
     * Remove all custom field instances of a given CF from cache for a given entity
     * 
     * @param entity Entity with custom fields
     */
    public void removeCustomFieldFromCache(ICustomFieldEntity entity, String code) {

        String cacheKey = getCacheKey(entity);
        log.trace("Removing custom field {} from CustomFieldInstance cache for entity uuid {}", code, cacheKey);

        Map<String, List<CachedCFPeriodValue>> cachedCFValues = customFieldValueCache.getAdvancedCache().withFlags(Flag.FORCE_WRITE_LOCK).get(cacheKey);

        if (cachedCFValues == null || !cachedCFValues.containsKey(code)) {
            return;
        }

        cachedCFValues.remove(code);

        if (cachedCFValues.isEmpty()) {
            customFieldValueCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).remove(cacheKey);
        } else {
            customFieldValueCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cacheKey, cachedCFValues);
        }

        log.trace("Removed custom field {} from CustomFieldInstance cache for entity uuid {}", code, cacheKey);
    }

    /**
     * Get a map of custom field values (not versioned) mapped by a CF code that apply to an entity
     * 
     * @param entity Entity to match
     * @return A map of CachedCFPeriodValue values with CF code as a key
     */
    public Map<String, Object> getValues(ICustomFieldEntity entity) {
        return getValues(entity.getUuid());
    }

    /**
     * Get a map of custom field values (not versioned) mapped by a CF code that apply to an entity
     * 
     * @param entityIdentifier Unique entity identifier
     * @return A map of CachedCFPeriodValue values with CF code as a key
     */
    public Map<String, Object> getValues(String entityIdentifier) {

        Map<String, Object> values = new HashMap<String, Object>();

        String cacheKey = entityIdentifier;

        Map<String, List<CachedCFPeriodValue>> cachedValues = customFieldValueCache.get(cacheKey);
        if (cachedValues == null) {
            return values;
        }

        // Add only values that are not versioned
        for (Entry<String, List<CachedCFPeriodValue>> cfValuesInfo : cachedValues.entrySet()) {
            for (CachedCFPeriodValue cfValue : cfValuesInfo.getValue()) {
                if (!cfValue.isVersioned()) {
                    values.put(cfValuesInfo.getKey(), cfValue.getValue());
                    break;
                }
            }

        }

        return values;
    }

    /**
     * Get a single custom field value (not versioned) for a given custom field code that applies to an entity
     * 
     * @param entity Entity to match
     * @param code Custom field code
     * @return A single custom field value
     */
    public Object getValue(ICustomFieldEntity entity, String code) {
        return getValue(entity.getUuid(), code);
    }

    /**
     * Get a single custom field value (not versioned) for a given custom field code that applies to an entity
     * 
     * @param entityIdentifier Unique entity identifier
     * @param code Custom field code
     * @return A single custom field value
     */
    public Object getValue(String entityIdentifier, String code) {

        String cacheKey = entityIdentifier;

        Map<String, List<CachedCFPeriodValue>> cachedValues = customFieldValueCache.get(cacheKey);

        if (cachedValues == null || !cachedValues.containsKey(code)) {
            return null;
        }

        // Only value that is not versioned
        for (CachedCFPeriodValue cfValue : cachedValues.get(code)) {
            if (!cfValue.isVersioned()) {
                return cfValue.getValue();
            }
        }

        return null;
    }

    /**
     * Get a map of custom field values mapped by a CF code that apply to an entity for a given date
     * 
     * @param entity Entity to match
     * @param date Date to match
     * @return A map of CachedCFPeriodValue's value with CF code as a key
     */
    public Map<String, Object> getValues(ICustomFieldEntity entity, Date date) {
        return getValues(entity.getUuid(), date);
    }

    /**
     * Get a map of custom field values mapped by a CF code that apply to an entity for a given date
     * 
     * @param entityIdentifier Unique entity identifier
     * @param date Date to match
     * @return A map of CachedCFPeriodValue's value with CF code as a key
     */
    public Map<String, Object> getValues(String entityIdentifier, Date date) {

        Map<String, Object> values = new HashMap<String, Object>();

        String cacheKey = entityIdentifier;

        Map<String, List<CachedCFPeriodValue>> cachedValues = customFieldValueCache.get(cacheKey);

        if (cachedValues == null) {
            return values;
        }

        // Add only values that are versioned, with highest priority
        for (Entry<String, List<CachedCFPeriodValue>> cfValuesInfo : cachedValues.entrySet()) {
            CachedCFPeriodValue periodFound = null;
            for (CachedCFPeriodValue period : cfValuesInfo.getValue()) {
                if (period.isVersioned() && period.isCorrespondsToPeriod(date)) {
                    if (periodFound == null || periodFound.getPriority() < period.getPriority()) {
                        periodFound = period;
                    }
                }
            }
            if (periodFound != null) {
                values.put(cfValuesInfo.getKey(), periodFound.getValue());
            }
        }

        return values;
    }

    /**
     * Get a single custom field value (versioned) for a given custom field code that applies to an entity in a given date
     * 
     * @param entity Entity to match
     * @param code Custom field code
     * @param date Date to match
     * @return A single custom field value
     */
    public Object getValue(ICustomFieldEntity entity, String code, Date date) {
        return getValue(entity.getUuid(), code, date);
    }

    /**
     * Get a single custom field value (versioned) for a given custom field code that applies to an entity in a given date
     * 
     * @param entityIdentifier Unique entity identifier
     * @param code Custom field code
     * @param date Date to match
     * @return A single custom field value
     */
    public Object getValue(String entityIdentifier, String code, Date date) {

        String cacheKey = entityIdentifier;

        Map<String, List<CachedCFPeriodValue>> cachedValues = customFieldValueCache.get(cacheKey);

        if (cachedValues == null || !cachedValues.containsKey(code)) {
            return null;
        }

        // Add only values that are versioned, with highest priority
        CachedCFPeriodValue periodFound = null;
        for (CachedCFPeriodValue period : cachedValues.get(code)) {
            if (period.isVersioned() && period.isCorrespondsToPeriod(date)) {
                if (periodFound == null || periodFound.getPriority() < period.getPriority()) {
                    periodFound = period;
                }
            }
            if (periodFound != null) {
                return periodFound.getValue();
            }
        }

        return null;
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
        summaryOfCaches.put(cfValueCacheTime.getName(), cfValueCacheTime);
        summaryOfCaches.put(cftsByAppliesTo.getName(), cftsByAppliesTo);
        summaryOfCaches.put(cetsByCode.getName(), cetsByCode);
        summaryOfCaches.put(customFieldValueCache.getName(), customFieldValueCache);

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

        if (cacheName == null || cacheName.equals(cfValueCacheTime.getName()) || cacheName.contains(cfValueCacheTime.getName()) || cacheName.equals(cftsByAppliesTo.getName())
                || cacheName.contains(cftsByAppliesTo.getName())) {
            populateCFTCache();
        }
        if (cacheName == null || cacheName.equals(cetsByCode.getName()) || cacheName.contains(cetsByCode.getName())) {
            populateCETCache();
        }
        if (cacheName == null || cacheName.equals(customFieldValueCache.getName()) || cacheName.contains(customFieldValueCache.getName())) {
            populateCFValueCache();
        }

    }

    private CachedCFPeriodValue convertFromCFI(CustomFieldInstance cfi, Date cutoffDate) {

        if (cutoffDate == null || cfi.getPeriodRaw() == null
                || (cfi.getPeriodRaw() != null && (cfi.getPeriod().getTo() == null || (cfi.getPeriod().getTo() != null && cutoffDate.before(cfi.getPeriod().getTo()))))) {
            cfi.deserializeValue();
            CachedCFPeriodValue value = new CachedCFPeriodValue(cfi.getCfValue().getValue(), cfi.getPriority(), cfi.getPeriod().getFrom(), cfi.getPeriod().getTo());
            return value;
        }

        return null;

    }

    /**
     * Calculate until what date the versionable custom field value should be stored in cache
     * 
     * @param cft Custom field template
     * @return A date or null if not applicable
     */
    private Date calculateCutoffDate(CustomFieldTemplate cft) {

        Integer cutoffPeriod = cfValueCacheTime.get(getCFTCacheKeyByAppliesToAndCode(cft));
        if (cutoffPeriod == null) {
            return null;
        }

        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DATE, (-1) * cutoffPeriod.intValue());
        Date cutoffDate = calendar.getTime();

        return cutoffDate;
    }

    /**
     * Calculate custom field value cache key for a given entity
     * 
     * @param entity Entity
     * @return String cache key
     */
    private String getCacheKey(ICustomFieldEntity entity) {
        return entity.getUuid();
    }

    /**
     * Calculate custom field value cache key for a given custom field instance
     * 
     * @param cfi Custom Field Instance
     * @return String cache key
     */
    private String getCacheKey(CustomFieldInstance cfi) {
        return cfi.getAppliesToEntity();
    }

    /**
     * Store mapping between CF code and value storage in cache time period and cache by CFT appliesTo value
     * 
     * @param cft Custom field template definition
     */
    public void addUpdateCustomFieldTemplate(CustomFieldTemplate cft) {

        String cacheKeyByAppliesTo = getCFTCacheKeyByAppliesTo(cft);

        log.trace("Adding/updating custom field template {} for {} to custom field template cache", cft.getCode(), cacheKeyByAppliesTo);

        Map<String, CustomFieldTemplate> cfts = cftsByAppliesTo.getAdvancedCache().withFlags(Flag.FORCE_WRITE_LOCK).get(cacheKeyByAppliesTo);
        if (cfts == null) {
            cfts = new TreeMap<String, CustomFieldTemplate>();
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
            cft.getListValues().values().toArray(new String[]{});
        }

        try {
            cft = (CustomFieldTemplate) BeanUtils.cloneBean(cft);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            log.error("Failed to clone a CFT entity {} for storage in CFT cache", cft.getCode());
        }

        cfts.put(cft.getCode(), cft);
        cftsByAppliesTo.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cacheKeyByAppliesTo, cfts);

        if (cft.isVersionable()) {
            String cacheKeyByAppliesToAndCode = getCFTCacheKeyByAppliesToAndCode(cft);
            if (cft.getCacheValueTimeperiod() != null) {
                cfValueCacheTime.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cacheKeyByAppliesToAndCode, cft.getCacheValueTimeperiod());
                log.trace("Added CFT timeout value for CFT {} for {} to custom field template timeout cache", cft.getCode(), cacheKeyByAppliesToAndCode);

            } else {
                cfValueCacheTime.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).remove(cacheKeyByAppliesToAndCode);
                log.trace("Removed CFT timeout value for CFT {} for {} from custom field template timeout cache", cft.getCode(), cacheKeyByAppliesToAndCode);
            }
        }
    }

    /**
     * Remove mapping between CF code and value storage in cache time period and remove from cache by CFT appliesTo value
     * 
     * @param cft Custom field template definition
     */
    public void removeCustomFieldTemplate(CustomFieldTemplate cft) {

        String cacheKeyByAppliesTo = getCFTCacheKeyByAppliesTo(cft);
        String cacheKeyByAppliesToAndCode = getCFTCacheKeyByAppliesToAndCode(cft);

        log.trace("Removing custom field template {} for {} from custom field template cache", cft.getCode(), cacheKeyByAppliesTo);

        Map<String, CustomFieldTemplate> cfts = cftsByAppliesTo.getAdvancedCache().withFlags(Flag.FORCE_WRITE_LOCK).get(cacheKeyByAppliesTo);
        if (cfts != null && cfts.containsKey(cft.getCode())) {
            cfts.remove(cft.getCode());
            cftsByAppliesTo.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cacheKeyByAppliesTo, cfts);
            log.trace("Removed custom field template {} for {} from custom field template cache", cft.getCode(), cacheKeyByAppliesTo);
        }

        if (cft.isVersionable()) {
            cfValueCacheTime.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).remove(cacheKeyByAppliesToAndCode);
            log.trace("Removed CFT timeout value for CFT {} for {} from custom field template timeout cache", cft.getCode(), cacheKeyByAppliesToAndCode);
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

    private String getCFTCacheKeyByAppliesToAndCode(CustomFieldTemplate cft) {
        return cft.getAppliesTo() + "_" + cft.getCode();
    }

    private String getCFTCacheKeyByAppliesTo(CustomFieldTemplate cft) {
        return cft.getAppliesTo();
    }

    /**
     * Get custom field templates for a given entity (appliesTo value)
     * 
     * @param appliesTo entity (appliesTo value)
     * @param provider Provider
     * @return A map of custom field templates with template code as a key
     */
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
    public Collection<CustomEntityTemplate> getCustomEntityTemplates() {

        return cetsByCode.values();
    }

    /**
     * Get custom entity template by code
     * 
     * @param code Custom entity template code
     * @return A list of custom entity templates
     */
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
    public CustomFieldTemplate getCustomFieldTemplate(String code, String appliesTo) {

        Map<String, CustomFieldTemplate> cfts = cftsByAppliesTo.get(appliesTo);
        if (cfts != null) {
            return cfts.get(code);
        }
        return null;
    }

    /**
     * Check if entity has a custom field value for a given custom field code (versioned or unversioned)
     * 
     * @param entity Entity to match
     * @param code Custom field code
     * @return True if value is found (any period for versioned fields)
     */
    public boolean hasValue(ICustomFieldEntity entity, String code) {
        return hasValue(entity.getUuid(), code);
    }

    /**
     * Check if entity has a custom field value for a given custom field code (versioned or unversioned)
     * 
     * @param entityIdentifier Unique entity identifier
     * @param code Custom field code
     * @return A single custom field value
     */
    public boolean hasValue(String entityIdentifier, String code) {

        String cacheKey = entityIdentifier;

        Map<String, List<CachedCFPeriodValue>> cachedValues = customFieldValueCache.get(cacheKey);

        return cachedValues != null && cachedValues.containsKey(code) && !cachedValues.get(code).isEmpty();
    }
}