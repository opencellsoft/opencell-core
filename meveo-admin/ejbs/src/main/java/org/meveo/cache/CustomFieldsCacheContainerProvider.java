package org.meveo.cache;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
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

import org.infinispan.api.BasicCache;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.catalog.CalendarDaily;
import org.meveo.model.catalog.CalendarInterval;
import org.meveo.model.catalog.CalendarYearly;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.crm.impl.CustomFieldException;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.crm.impl.ProviderService;
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
public class CustomFieldsCacheContainerProvider {

    @Inject
    protected Logger log;

    @EJB
    private CustomFieldInstanceService customFieldInstanceService;

    @EJB
    private CustomFieldTemplateService customFieldTemplateService;

    @EJB
    CustomEntityTemplateService customEntityTemplateService;

    /**
     * How long custom field period value should be cached. Key format: <provider id>_<custom field template code>. Value is a number of days to cache custom field value
     */
    private Map<String, Integer> cfValueCacheTime = new HashMap<String, Integer>();

    /**
     * Group custom field templates applicable to the same entity type. Key format: <provider id>_<custom field template appliesTo code>. Value is a map of custom field templates
     * identified by a template code
     */
    private Map<String, Map<String, CustomFieldTemplate>> cftsByAppliesTo = new HashMap<String, Map<String, CustomFieldTemplate>>();

    /**
     * Cache custom entity templates by provider. Key format: <provider id>. Value is a map of custom entity templates identified by a template code
     */
    private Map<String, Map<String, CustomEntityTemplate>> cetsByProvider = new HashMap<String, Map<String, CustomEntityTemplate>>();

    /**
     * Contains association between entity, and custom field value(s). Key format: <entity class>_<entity id>. Value is a map where key is CFI code and value is a list of values
     */
    @Resource(lookup = "java:jboss/infinispan/cache/meveo/meveo-cfv-cache")
    private BasicCache<String, Map<String, List<CachedCFPeriodValue>>> customFieldValueCache;

    // @Resource(name = "java:jboss/infinispan/container/meveo")
    // private CacheContainer meveoContainer;

    @PostConstruct
    private void init() {
        try {
            log.debug("CustomFieldsCacheContainerProvider initializing...");
            // customFieldValueCache = meveoContainer.getCache("meveo-cfv-cache");

            populateCFValueCache();

            log.info("CustomFieldsCacheContainerProvider initialized");

        } catch (Exception e) {
            log.error("CustomFieldsCacheContainerProvider init() error", e);
            throw e;
        }
    }

    /**
     * Populate CustomFieldInstance cache
     */
    private void populateCFValueCache() {

        // Start to populate custom field value caching time and appliesTo cache
        log.debug("Start to populate custom field value caching time and appliesTo cache");

        // Calculate cache storage cuttoff time for each CFT
        Map<String, Date> cfValueCacheTimeAsDate = new HashMap<String, Date>();

        cfValueCacheTime.clear();
        cftsByAppliesTo.clear();
        List<CustomFieldTemplate> cfts = customFieldTemplateService.getCFTForCache();
        for (CustomFieldTemplate cft : cfts) {

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
                cft.setListValues(PersistenceUtils.initializeAndUnproxy(cft.getListValues()));
            }

            customFieldTemplateService.detach(cft);

            String cacheKeyByAppliesTo = getCFTCacheKeyByAppliesTo(cft);
            if (!cftsByAppliesTo.containsKey(cacheKeyByAppliesTo)) {
                cftsByAppliesTo.put(cacheKeyByAppliesTo, new TreeMap<String, CustomFieldTemplate>());
            } else {
                cftsByAppliesTo.get(cacheKeyByAppliesTo).remove(cft.getCode());
            }
            cftsByAppliesTo.get(cacheKeyByAppliesTo).put(cft.getCode(), cft);

            // Remember custom field versionable value cache time
            if (cft.isVersionable() && cft.getCacheValueTimeperiod() != null) {
                cfValueCacheTime.put(getCFTCacheKeyByCode(cft), cft.getCacheValueTimeperiod());

                Calendar calendar = new GregorianCalendar();
                calendar.add(Calendar.DATE, (-1) * cft.getCacheValueTimeperiod().intValue());

                cfValueCacheTimeAsDate.put(getCFTCacheKeyByCode(cft), calendar.getTime());
            }
        }
        log.info("Custom field value caching time populated with {} values. appliesTo cache populated with {} values", cfValueCacheTime.size(), cfts.size());

        // Start to populate custom entity template cache
        log.debug("Start to populate custom entity template cache");

        cetsByProvider.clear();

        // Cache custom entity templates sorted by a cet.name
        List<CustomEntityTemplate> cets = customEntityTemplateService.getCETForCache();
        Collections.sort(cets);

        for (CustomEntityTemplate cet : cets) {

            if (!cetsByProvider.containsKey(cet.getProvider().getId().toString())) {
                cetsByProvider.put(cet.getProvider().getId().toString(), new TreeMap<String, CustomEntityTemplate>());
            }

            cetsByProvider.get(cet.getProvider().getId().toString()).put(cet.getCode(), cet);
        }

        log.info("Custom entity template cache populated with {} values.", cets.size());

        // Start to populate custom field value cache
        log.debug("Start to populate custom field value cache");

        customFieldValueCache.clear();

        int cfiCount = 0;

        // Get a list of entity id and CFI instances for a given entity class
        List<CustomFieldInstance> cfis = customFieldInstanceService.getCFIForCache();

        for (CustomFieldInstance cfi : cfis) {

            // CustomFieldTemplate cft = getCustomFieldTemplate(cfi.getCode(), cfi.getAppliesToEntity(), cfi.getProvider());
            // if (!cft.isCacheValue()) {
            // continue;
            // }

            String cacheKey = getCacheKey(cfi);

            log.trace("Add CustomFieldInstance {} to CustomFieldInstance cache for entity uuid {}", cfi.getCode(), cacheKey);

            CachedCFPeriodValue cfvValue = convertFromCFI(cfi, cfValueCacheTimeAsDate.get(cfi.getProvider().getId() + "_" + cfi.getCode()));
            if (cfvValue != null) {
                customFieldValueCache.putIfAbsent(cacheKey, new TreeMap<String, List<CachedCFPeriodValue>>());
                if (!customFieldValueCache.get(cacheKey).containsKey(cfi.getCode())) {
                    customFieldValueCache.get(cacheKey).put(cfi.getCode(), new ArrayList<CachedCFPeriodValue>());
                }
                customFieldValueCache.get(cacheKey).get(cfi.getCode()).add(cfvValue);
                cfiCount++;
            }
        }
        log.info("Custom field value populated with {} values", cfiCount);
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
            CachedCFPeriodValue cfvValue = convertFromCFI(cfi, calculateCutoffDate(cfi.getCode(), entity));
            if (cfvValue != null) {
                customFieldValueCache.putIfAbsent(cacheKey, new TreeMap<String, List<CachedCFPeriodValue>>());
                if (!customFieldValueCache.get(cacheKey).containsKey(cfi.getCode())) {
                    customFieldValueCache.get(cacheKey).put(cfi.getCode(), new ArrayList<CachedCFPeriodValue>());
                }
                customFieldValueCache.get(cacheKey).get(cfi.getCode()).remove(cfvValue);
                customFieldValueCache.get(cacheKey).get(cfi.getCode()).add(cfvValue);

                log.trace("Updated custom field {} from CustomFieldInstance cache for entity uuid {}", cfi.getCode(), cacheKey);

                // If no longer cacheable - remove it
            } else {
                cfvValue = convertFromCFI(cfi, null);
                if (customFieldValueCache.containsKey(cacheKey) && customFieldValueCache.get(cacheKey).containsKey(cfi.getCode())) {
                    customFieldValueCache.get(cacheKey).get(cfi.getCode()).remove(cfvValue);

                    log.trace("Removed custom field {} value from CustomFieldInstance cache for entity uuid {}. Value removed {}", cfi.getCode(), cacheKey, cfvValue);
                }
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
        customFieldValueCache.remove(cacheKey);

        log.trace("Removed all custom fields from CustomFieldInstance cache for entity uuid {}", cacheKey);
    }

    /**
     * Remove a particular custom field instance from cache for a given entity
     * 
     * @param entity Entity with custom fields
     * @param cfi Custom field instance
     */
    public void removeCustomFieldFromCache(ICustomFieldEntity entity, CustomFieldInstance cfi) {
        String cacheKey = getCacheKey(entity);
        if (customFieldValueCache.containsKey(cacheKey) && customFieldValueCache.get(cacheKey).containsKey(cfi.getCode())) {
            CachedCFPeriodValue cfvValue = convertFromCFI(cfi, null);
            customFieldValueCache.get(cacheKey).get(cfi.getCode()).remove(cfvValue);
            log.trace("Removed custom field {} from CustomFieldInstance cache for entity uuid {}", cfvValue, cacheKey);
        }
    }

    /**
     * Remove a particular custom field instance from cache for a given entity
     * 
     * @param entity Entity with custom fields
     */
    public void removeCustomFieldFromCache(ICustomFieldEntity entity, String code) {

        String cacheKey = getCacheKey(entity);
        if (customFieldValueCache.containsKey(cacheKey)) {
            customFieldValueCache.get(cacheKey).remove(code);
            log.trace("Removed custom field {} from CustomFieldInstance cache for entity uuid {}", code, cacheKey);
        }
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

        if (!customFieldValueCache.containsKey(cacheKey)) {
            return values;
        }

        // Add only values that are not versioned
        for (Entry<String, List<CachedCFPeriodValue>> cfValuesInfo : customFieldValueCache.get(cacheKey).entrySet()) {
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

        if (customFieldValueCache.containsKey(cacheKey) && customFieldValueCache.get(cacheKey).containsKey(code)) {
            // Only value that is not versioned
            for (CachedCFPeriodValue cfValue : customFieldValueCache.get(cacheKey).get(code)) {
                if (!cfValue.isVersioned()) {
                    return cfValue.getValue();
                }
            }

        }

        return null;
    }

    /**
     * Get a map of custom field values mapped by a CF code that apply to an entity for a given date
     * 
     * @param entity Entity to match
     * @param date Date to match
     * @return A map of CachedCFPeriodValue values with CF code as a key
     */
    public Map<String, Object> getValues(ICustomFieldEntity entity, Date date) {
        return getValues(entity.getUuid(), date);
    }

    /**
     * Get a map of custom field values mapped by a CF code that apply to an entity for a given date
     * 
     * @param entityIdentifier Unique entity identifier
     * @param date Date to match
     * @return A map of CachedCFPeriodValue values with CF code as a key
     */
    public Map<String, Object> getValues(String entityIdentifier, Date date) {

        Map<String, Object> values = new HashMap<String, Object>();

        String cacheKey = entityIdentifier;

        if (!customFieldValueCache.containsKey(cacheKey)) {
            return values;
        }
        // Add only values that are versioned, with highest priority
        for (Entry<String, List<CachedCFPeriodValue>> cfValuesInfo : customFieldValueCache.get(cacheKey).entrySet()) {
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

        if (!(customFieldValueCache.containsKey(cacheKey) && customFieldValueCache.get(cacheKey).containsKey(code))) {
            return null;
        }
        // Add only values that are versioned, with highest priority
        CachedCFPeriodValue periodFound = null;
        for (CachedCFPeriodValue period : customFieldValueCache.get(cacheKey).get(code)) {
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
     * @return A list of a map containing cache information with cache name as a key and cache as a value
     */
    @SuppressWarnings("rawtypes")
    public Map<String, BasicCache> getCaches() {
        Map<String, BasicCache> summaryOfCaches = new HashMap<String, BasicCache>();
        summaryOfCaches.put(customFieldValueCache.getName(), customFieldValueCache);

        return summaryOfCaches;
    }

    /**
     * Refresh cache by name
     * 
     * @param cacheName Name of cache to refresh or null to refresh all caches
     */
    @Asynchronous
    public void refreshCache(String cacheName) {

        if (cacheName == null || cacheName.equals(customFieldValueCache.getName())) {

            populateCFValueCache();
        }
    }

    private CachedCFPeriodValue convertFromCFI(CustomFieldInstance cfi, Date cutoffDate) {

        if (cutoffDate == null || cfi.getPeriodEndDate() == null || (cfi.getPeriodEndDate() != null && cutoffDate.before(cfi.getPeriodEndDate()))) {
            cfi.deserializeValue();
            CachedCFPeriodValue value = new CachedCFPeriodValue(cfi.getCfValue().getValue(), cfi.getPriority(), cfi.getPeriodStartDate(), cfi.getPeriodEndDate());
            return value;
        }

        return null;

    }

    /**
     * Calculate until what date the versionable custom field value should be stored in cache
     * 
     * @param code Custom field template cache
     * @param entity Entity (only to find provider)
     * @return A date or null if not applicable
     */
    private Date calculateCutoffDate(String code, ICustomFieldEntity entity) {

        Long providerId = ProviderService.getProvider((IEntity) entity).getId();

        Date cutoffDate = null;
        Integer cutoffPeriod = cfValueCacheTime.get(providerId + "_" + code);
        if (cutoffPeriod != null) {
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DATE, (-1) * cutoffPeriod.intValue());
            cutoffDate = calendar.getTime();
        }
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
        if (!cftsByAppliesTo.containsKey(cacheKeyByAppliesTo)) {
            cftsByAppliesTo.put(cacheKeyByAppliesTo, new TreeMap<String, CustomFieldTemplate>());
        } else {
            cftsByAppliesTo.get(cacheKeyByAppliesTo).remove(cft.getCode());
        }
        // Load calendar for lazy loading
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
            cft.setListValues(PersistenceUtils.initializeAndUnproxy(cft.getListValues()));
        }

        cftsByAppliesTo.get(cacheKeyByAppliesTo).put(cft.getCode(), cft);

        if (cft.isVersionable()) {
            if (cft.getCacheValueTimeperiod() != null) {
                cfValueCacheTime.put(getCFTCacheKeyByCode(cft), cft.getCacheValueTimeperiod());

            } else {
                cfValueCacheTime.remove(getCFTCacheKeyByCode(cft));
            }
        }
    }

    /**
     * Remove mapping between CF code and value storage in cache time period and remove from cache by CFT appliesTo value
     * 
     * @param cft Custom field template definition
     */
    public void removeCustomFieldTemplate(CustomFieldTemplate cft) {

        cftsByAppliesTo.get(getCFTCacheKeyByAppliesTo(cft)).remove(cft.getCode());

        if (cft.isVersionable()) {
            cfValueCacheTime.remove(getCFTCacheKeyByCode(cft));
        }
    }

    /**
     * Store custom entity template to cache
     * 
     * @param cet Custom entity template definition
     */
    public void addUpdateCustomEntityTemplate(CustomEntityTemplate cet) {

        if (!cetsByProvider.containsKey(cet.getProvider().getId().toString())) {
            cetsByProvider.put(cet.getProvider().getId().toString(), new TreeMap<String, CustomEntityTemplate>());
        } else {
            cetsByProvider.get(cet.getProvider().getId().toString()).remove(cet.getCode());
        }
        cetsByProvider.get(cet.getProvider().getId().toString()).put(cet.getCode(), cet);

        // Sort values by cet.name
        // Collections.sort(cetsByProvider.get(cet.getProvider().getId().toString()));

    }

    /**
     * Remove custom entity template from cache
     * 
     * @param cet Custom entity template definition
     */
    public void removeCustomEntityTemplate(CustomEntityTemplate cet) {

        cetsByProvider.get(cet.getProvider().getId().toString()).remove(cet.getCode());
    }

    private String getCFTCacheKeyByCode(CustomFieldTemplate cft) {
        return cft.getProvider().getId() + "_" + cft.getCode();
    }

    private String getCFTCacheKeyByAppliesTo(CustomFieldTemplate cft) {
        return cft.getProvider().getId() + "_" + cft.getAppliesTo();
    }

    /**
     * Get custom field templates for a given entity (appliesTo value) and provider
     * 
     * @param appliesTo entity (appliesTo value)
     * @param provider Provider
     * @return A map of custom field templates with template code as a key
     */
    public Map<String, CustomFieldTemplate> getCustomFieldTemplates(String appliesTo, Provider provider) {
        String key = provider.getId() + "_" + appliesTo;
        if (cftsByAppliesTo.containsKey(key)) {
            return cftsByAppliesTo.get(key);

        } else {
            return new HashMap<String, CustomFieldTemplate>();
        }
    }

    /**
     * Get custom entity templates for a given provider
     * 
     * @param provider Provider
     * @return A list of custom entity templates
     */
    public Collection<CustomEntityTemplate> getCustomEntityTemplates(Provider provider) {

        if (cetsByProvider.containsKey(provider.getId().toString())) {
            return cetsByProvider.get(provider.getId().toString()).values();

        } else {
            return new ArrayList<CustomEntityTemplate>();
        }
    }

    /**
     * Get custom entity template by code for a given provider
     * 
     * @param code Custom entity template code
     * @param provider Provider
     * @return A list of custom entity templates
     */
    public CustomEntityTemplate getCustomEntityTemplate(String code, Provider provider) {

        if (cetsByProvider.containsKey(provider.getId().toString())) {
            return cetsByProvider.get(provider.getId().toString()).get(code);

        } else {
            return null;
        }
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
            return getCustomFieldTemplate(code, CustomFieldTemplateService.calculateAppliesToValue(entity), ProviderService.getProvider((IEntity) entity));

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
     * @param provider Provider
     * @return Custom field template
     */
    public CustomFieldTemplate getCustomFieldTemplate(String code, String appliesTo, Provider provider) {

        String key = provider.getId() + "_" + appliesTo;
        if (cftsByAppliesTo.containsKey(key)) {
            return cftsByAppliesTo.get(key).get(code);

        } else {
            return null;
        }
    }
}