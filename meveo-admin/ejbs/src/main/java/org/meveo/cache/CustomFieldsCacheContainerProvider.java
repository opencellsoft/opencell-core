package org.meveo.cache;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.infinispan.api.BasicCache;
import org.infinispan.manager.CacheContainer;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.IProvider;
import org.meveo.model.catalog.CalendarDaily;
import org.meveo.model.catalog.CalendarInterval;
import org.meveo.model.catalog.CalendarYearly;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldPeriod;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.util.PersistenceUtils;
import org.reflections.Reflections;
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
     * Group custom field templates applicable to the same entity type. Key format: <provider id>_<custom field template appliesTo code>. Value is a list of custom field templates
     */
    private Map<String, List<CustomFieldTemplate>> cftsByAppliesTo = new HashMap<String, List<CustomFieldTemplate>>();

    /**
     * Cache custom entity templates by provider. Key format: <provider id>. Value is a list of custom entity templates
     */
    private Map<String, List<CustomEntityTemplate>> cetsByProvider = new HashMap<String, List<CustomEntityTemplate>>();

    /**
     * Contains association between entity, and custom field value(s). Key format: <entity class>_<entity id>. Value is a map where key is CFI code and value is a list of values
     */
    // @Resource(lookup = "java:jboss/infinispan/cache/meveo/meveo-cfv-cache")
    private BasicCache<String, Map<String, List<CachedCFPeriodValue>>> customFieldValueCache;

    @Resource(name = "java:jboss/infinispan/container/meveo")
    private CacheContainer meveoContainer;

    @PostConstruct
    private void init() {
        try {
            log.debug("CustomFieldsCacheContainerProvider initializing...");
            customFieldValueCache = meveoContainer.getCache("meveo-cfv-cache");

            populateCFValueCache();

            log.info("CustomFieldsCacheContainerProvider initialized");

        } catch (Exception e) {
            log.error("CustomFieldsCacheContainerProvider init() error", e);
        }
    }

    /**
     * Populate CustomFieldInstance cache
     */
    private void populateCFValueCache() {

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
            
            customFieldTemplateService.detach(cft);

            String cacheKeyByAppliesTo = getCFTCacheKeyByAppliesTo(cft);
            if (!cftsByAppliesTo.containsKey(cacheKeyByAppliesTo)) {
                cftsByAppliesTo.put(cacheKeyByAppliesTo, new ArrayList<CustomFieldTemplate>());
            } else {
                cftsByAppliesTo.get(cacheKeyByAppliesTo).remove(cft);
            }
            cftsByAppliesTo.get(cacheKeyByAppliesTo).add(cft);

            // Remember custom field versionable value cache time
            if (cft.isVersionable() && cft.getCacheValueTimeperiod() != null) {
                cfValueCacheTime.put(getCFTCacheKeyByCode(cft), cft.getCacheValueTimeperiod());

                Calendar calendar = new GregorianCalendar();
                calendar.add(Calendar.DATE, (-1) * cft.getCacheValueTimeperiod().intValue());

                cfValueCacheTimeAsDate.put(getCFTCacheKeyByCode(cft), calendar.getTime());
            }
        }
        log.info("Custom field value caching time populated with {} values. appliesTo cache populated with {} values", cfValueCacheTime.size(), cfts.size());

        log.debug("Start to populate custom entity template cache");

        cetsByProvider.clear();

        // Cache custom entity templates sorted by a cet.name
        List<CustomEntityTemplate> cets = customEntityTemplateService.getCETForCache();
        Collections.sort(cets);

        for (CustomEntityTemplate cet : cets) {

            if (!cetsByProvider.containsKey(cet.getProvider().getId().toString())) {
                cetsByProvider.put(cet.getProvider().getId().toString(), new ArrayList<CustomEntityTemplate>());
            }

            cetsByProvider.get(cet.getProvider().getId().toString()).add(cet);
        }

        log.info("Custom entity template cache populated with {} values.", cets.size());

        log.debug("Start to populate custom field value cache");

        customFieldValueCache.clear();

        // Find custom field instances for each ICustomFieldEntity entity
        Reflections reflections = new Reflections("org.meveo.model");
        Set<Class<? extends ICustomFieldEntity>> classes = reflections.getSubTypesOf(ICustomFieldEntity.class);

        int cfiCount = 0;
        for (Class<? extends ICustomFieldEntity> cfEntityClass : classes) {
            if (Modifier.isAbstract(cfEntityClass.getModifiers())) {
                continue;
            }

            // Get a list of entity id and CFI instances for a given entity class
            List<Object[]> entityIdAndActiveCFIs = customFieldInstanceService.getCFIForCache(cfEntityClass);

            for (Object[] entityIdAndActiveCFI : entityIdAndActiveCFIs) {

                Long entityId = (Long) entityIdAndActiveCFI[0];
                Long providerId = (Long) entityIdAndActiveCFI[1];
                CustomFieldInstance cfi = (CustomFieldInstance) entityIdAndActiveCFI[2];

                String cacheKey = getCacheKey(cfEntityClass, entityId);

                log.trace("Add CustomFieldInstance {} to CustomFieldInstance cache for entity {}", cfi.getCode(), cacheKey);

                List<CachedCFPeriodValue> cfvValues = convertFromCFI(cfi, cfValueCacheTimeAsDate.get(providerId + "_" + cfi.getCode()));
                if (!cfvValues.isEmpty()) {
                    customFieldValueCache.putIfAbsent(cacheKey, new HashMap<String, List<CachedCFPeriodValue>>());
                    customFieldValueCache.get(cacheKey).put(cfi.getCode(), cfvValues);
                    cfiCount++;
                }
            }
        }
        log.info("Custom field value populated with {} values", cfiCount);
    }

    /**
     * Add or update custom field instances for an entity in cache.
     * 
     * @param entity Entity with custom fields
     */
    public void addUpdateCustomFieldsInCache(ICustomFieldEntity entity) {

        // Also removes previous values
        String cacheKey = getCacheKey((IEntity) entity);

        Map<String, List<CachedCFPeriodValue>> values = new HashMap<String, List<CachedCFPeriodValue>>();

        if (entity.getCfFields() != null) {
            for (CustomFieldInstance cfi : entity.getCfFields().getCustomFields().values()) {
                List<CachedCFPeriodValue> cfvValues = convertFromCFI(cfi, calculateCutoffDate(cfi.getCode(), entity));
                if (!cfvValues.isEmpty()) {
                    values.put(cfi.getCode(), cfvValues);
                }
                log.trace("Add CustomFieldInstance {} to CustomFieldInstance cache for entity {}", cfi.getCode(), cacheKey);
            }
        }

        if (!values.isEmpty()) {
            customFieldValueCache.put(cacheKey, values);
        } else {
            customFieldValueCache.remove(cacheKey);
        }
    }

    /**
     * Remove custom field instance from cache for a given entity
     * 
     * @param entity Entity with custom fields
     */
    public void removeCustomFieldsFromCache(ICustomFieldEntity entity) {

        String cacheKey = getCacheKey((IEntity) entity);
        customFieldValueCache.remove(cacheKey);

        log.trace("Removed custom fields from CustomFieldInstance cache for entity {}", cacheKey);
    }

    /**
     * Get a map of custom field values (not versioned) mapped by a CF code that apply to an entity
     * 
     * @param entity Entity to match
     * @return A map of CachedCFPeriodValue values with CF code as a key
     */
    public Map<String, Object> getValues(ICustomFieldEntity entity) {
        return getValues(entity.getClass(), ((IEntity) entity).getId());
    }

    /**
     * Get a map of custom field values (not versioned) mapped by a CF code that apply to an entity
     * 
     * @param entityClass Entity class to match
     * @param id Entity id
     * @return A map of CachedCFPeriodValue values with CF code as a key
     */
    public Map<String, Object> getValues(Class<? extends ICustomFieldEntity> entityClass, Serializable id) {

        Map<String, Object> values = new HashMap<String, Object>();

        String cacheKey = getCacheKey(entityClass, id);

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
     * @param cfCode Custom field code
     * @return A single custom field value
     */
    public Object getValue(ICustomFieldEntity entity, String cfCode) {
        return getValue(entity.getClass(), ((IEntity) entity).getId(), cfCode);
    }

    /**
     * Get a single custom field value (not versioned) for a given custom field code that applies to an entity
     * 
     * @param entityClass Entity class to match
     * @param id Entity id
     * @param cfCode Custom field code
     * @return A single custom field value
     */
    public Object getValue(Class<? extends ICustomFieldEntity> entityClass, Serializable id, String cfCode) {

        String cacheKey = getCacheKey(entityClass, id);

        if (customFieldValueCache.containsKey(cacheKey) && customFieldValueCache.get(cacheKey).containsKey(cfCode)) {
            // Only value that is not versioned
            for (CachedCFPeriodValue cfValue : customFieldValueCache.get(cacheKey).get(cfCode)) {
                if (!cfValue.isVersioned()) {
                    return cfValue.getValue();
                }
            }

        }

        return null;
    }

    /**
     * Match for a given entity's custom field (non-versionable values) as close as possible map's key to the key provided and return a map value. Match is performed by matching a
     * full string and then reducing one by one symbol until a match is found.
     * 
     * TODO can be an issue with lower/upper case mismatch
     * 
     * @param entity Entity to match
     * @param cfCode Custom field code
     * @param keyToMatch Key to match
     * @return Map value that closely matches map key
     */
    public Object getClosestMatchValue(ICustomFieldEntity entity, String cfCode, String keyToMatch) {
        return getClosestMatchValue(entity.getClass(), ((IEntity) entity).getId(), cfCode, keyToMatch);
    }

    /**
     * Match for a given date (versionable values) for a given entity's custom field as close as possible map's key to the key provided and return a map value. Match is performed
     * by matching a full string and then reducing one by one symbol until a match is found.
     * 
     * TODO can be an issue with lower/upper case mismatch
     * 
     * @param entity Entity to match
     * @param cfCode Custom field code
     * @param date Date
     * @param keyToMatch Key to match
     * @return Map value that closely matches map key
     */
    public Object getClosestMatchValue(ICustomFieldEntity entity, String cfCode, Date date, String keyToMatch) {
        return getClosestMatchValue(entity.getClass(), ((IEntity) entity).getId(), cfCode, date, keyToMatch);
    }

    /**
     * Match for a given entity's custom field (non-versionable values) as close as possible map's key to the key provided and return a map value. Match is performed by matching a
     * full string and then reducing one by one symbol until a match is found.
     * 
     * TODO can be an issue with lower/upper case mismatch
     * 
     * @param entityClass Entity class to match
     * @param id Entity id
     * @param cfCode Custom field code
     * @param keyToMatch Key to match
     * @return Map value that closely matches map key
     */
    public Object getClosestMatchValue(Class<? extends ICustomFieldEntity> entityClass, Serializable id, String cfCode, String keyToMatch) {

        String cacheKey = getCacheKey(entityClass, id);
        CachedCFPeriodValue value = null;

        if (!customFieldValueCache.containsKey(cacheKey) || !customFieldValueCache.get(cacheKey).containsKey(cfCode)) {
            return null;
        }
        // Only value that is not versioned
        for (CachedCFPeriodValue period : customFieldValueCache.get(cacheKey).get(cfCode)) {
            if (!period.isVersioned()) {
                value = period;
            }
        }

        if (value != null) {
            return value.getClosestMatchValue(keyToMatch);
        }

        return null;
    }

    /**
     * Match for a given date (versionable values, but also works with non-versioned values) for a given entity's custom field as close as possible map's key to the key provided
     * and return a map value. Match is performed by matching a full string and then reducing one by one symbol until a match is found.
     * 
     * TODO can be an issue with lower/upper case mismatch
     * 
     * @param entityClass Entity class to match
     * @param id Entity id
     * @param cfCode Custom field code
     * @param date Date
     * @param keyToMatch Key to match
     * @return Map value that closely matches map key
     */
    public Object getClosestMatchValue(Class<? extends ICustomFieldEntity> entityClass, Serializable id, String cfCode, Date date, String keyToMatch) {

        String cacheKey = getCacheKey(entityClass, id);

        if (!customFieldValueCache.containsKey(cacheKey) || !customFieldValueCache.get(cacheKey).containsKey(cfCode)) {
            return null;
        }

        // Add only values that are versioned, with highest priority
        CachedCFPeriodValue periodFound = null;
        for (CachedCFPeriodValue period : customFieldValueCache.get(cacheKey).get(cfCode)) {
            if (period.isVersioned() && period.isCorrespondsToPeriod(date)) {
                if (periodFound == null || periodFound.getPriority() < period.getPriority()) {
                    periodFound = period;
                }
            } else if (!period.isVersioned()) {
                periodFound = period;
                break;
            }
        }
        if (periodFound != null) {
            Object result = periodFound.getClosestMatchValue(keyToMatch);
            log.trace("Found closest match value {} for period {} and keyToMatch={}", result, date, keyToMatch);
            return result;
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
        return getValues(entity.getClass(), ((IEntity) entity).getId(), date);
    }

    /**
     * Get a map of custom field values mapped by a CF code that apply to an entity for a given date
     * 
     * @param entityClass Entity class to match
     * @param id Entity id
     * @param date Date to match
     * @return A map of CachedCFPeriodValue values with CF code as a key
     */
    public Map<String, Object> getValues(Class<? extends ICustomFieldEntity> entityClass, Serializable id, Date date) {

        Map<String, Object> values = new HashMap<String, Object>();

        String cacheKey = getCacheKey(entityClass, id);

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
     * @param cfCode Custom field code
     * @param date Date to match
     * @return A single custom field value
     */
    public Object getValue(ICustomFieldEntity entity, String cfCode, Date date) {
        return getValue(entity.getClass(), ((IEntity) entity).getId(), cfCode, date);
    }

    /**
     * Get a single custom field value (versioned) for a given custom field code that applies to an entity in a given date
     * 
     * @param entityClass Entity class to match
     * @param id Entity id
     * @param cfCode Custom field code
     * @param date Date to match
     * @return A single custom field value
     */
    public Object getValue(Class<? extends ICustomFieldEntity> entityClass, Serializable id, String cfCode, Date date) {

        String cacheKey = getCacheKey(entityClass, id);

        if (!(customFieldValueCache.containsKey(cacheKey) && customFieldValueCache.get(cacheKey).containsKey(cfCode))) {
            return null;
        }
        // Add only values that are versioned, with highest priority
        CachedCFPeriodValue periodFound = null;
        for (CachedCFPeriodValue period : customFieldValueCache.get(cacheKey).get(cfCode)) {
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

    private List<CachedCFPeriodValue> convertFromCFI(CustomFieldInstance cfi, Date cutoffDate) {
        List<CachedCFPeriodValue> values = new ArrayList<CachedCFPeriodValue>();

        if (!cfi.isVersionable()) {
            cfi.deserializeValue();
            CachedCFPeriodValue value = new CachedCFPeriodValue(cfi.getCfValue().getValue());
            values.add(value);

        } else {

            for (CustomFieldPeriod period : cfi.getValuePeriods()) {
                if (cutoffDate == null || period.getPeriodEndDate() == null || (period.getPeriodEndDate() != null && cutoffDate.before(period.getPeriodEndDate()))) {
                    period.deserializeValue();
                    CachedCFPeriodValue value = new CachedCFPeriodValue(period.getCfValue().getValue(), period.getPriority(), period.getPeriodStartDate(),
                        period.getPeriodEndDate());
                    values.add(value);
                }
            }

        }

        return values;

    }

    /**
     * Calculate until what date the versionable custom field value should be stored in cache
     * 
     * @param cfCode Custom field template cache
     * @param entity Entity (only to find provider)
     * @return A date or null if not applicable
     */
    private Date calculateCutoffDate(String cfCode, ICustomFieldEntity entity) {

        Long providerId = null;
        if (entity instanceof Provider) {
            providerId = ((Provider) entity).getId();

        } else {
            providerId = ((IProvider) entity).getProvider().getId();
        }

        Date cutoffDate = null;
        Integer cutoffPeriod = cfValueCacheTime.get(providerId + "_" + cfCode);
        if (cutoffPeriod != null) {
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DATE, (-1) * cutoffPeriod.intValue());
            cutoffDate = calendar.getTime();
        }
        return cutoffDate;
    }

    private String getCacheKey(IEntity entity) {
        return getCacheKey(entity.getClass(), entity.getId());
    }

    @SuppressWarnings("rawtypes")
    private String getCacheKey(Class entityClass, Serializable id) {
        String className = entityClass.getSimpleName();
        int pos = className.indexOf("_$$_");
        if (pos > 0) {
            className = className.substring(0, pos);
        }

        return className + "_" + id;
    }

    /**
     * Store mapping between CF code and value storage in cache time period and cache by CFT appliesTo value
     * 
     * @param cft Custom field template definition
     */
    public void addUpdateCustomFieldTemplate(CustomFieldTemplate cft) {

        String cacheKeyByAppliesTo = getCFTCacheKeyByAppliesTo(cft);
        if (!cftsByAppliesTo.containsKey(cacheKeyByAppliesTo)) {
            cftsByAppliesTo.put(cacheKeyByAppliesTo, new ArrayList<CustomFieldTemplate>());
        } else {
            cftsByAppliesTo.get(cacheKeyByAppliesTo).remove(cft);
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
        cftsByAppliesTo.get(cacheKeyByAppliesTo).add(cft);

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

        cftsByAppliesTo.get(getCFTCacheKeyByAppliesTo(cft)).remove(cft);

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
            cetsByProvider.put(cet.getProvider().getId().toString(), new ArrayList<CustomEntityTemplate>());
        } else {
            cetsByProvider.get(cet.getProvider().getId().toString()).remove(cet);
        }
        cetsByProvider.get(cet.getProvider().getId().toString()).add(cet);

        // Sort values by cet.name
        Collections.sort(cetsByProvider.get(cet.getProvider().getId().toString()));

    }

    /**
     * Remove custom entity template from cache
     * 
     * @param cet Custom entity template definition
     */
    public void removeCustomEntityTemplate(CustomEntityTemplate cet) {

        cetsByProvider.get(cet.getProvider().getId().toString()).remove(cet);
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
     * @return A list of custom field templates
     */
    public List<CustomFieldTemplate> getCustomFieldTemplatesByAppliesTo(String appliesTo, Provider provider) {
        String key = provider.getId() + "_" + appliesTo;
        if (cftsByAppliesTo.containsKey(key)) {
            return cftsByAppliesTo.get(key);

        } else {
            return new ArrayList<CustomFieldTemplate>();
        }
    }

    /**
     * Get custom entity templates for a given provider
     * 
     * @param provider Provider
     * @return A list of custom entity templates
     */
    public List<CustomEntityTemplate> getCustomEntityTemlates(Provider provider) {

        if (cetsByProvider.containsKey(provider.getId().toString())) {
            return cetsByProvider.get(provider.getId().toString());

        } else {
            return new ArrayList<CustomEntityTemplate>();
        }
    }
}