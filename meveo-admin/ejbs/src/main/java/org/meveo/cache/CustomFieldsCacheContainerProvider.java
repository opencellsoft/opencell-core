package org.meveo.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldPeriod;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
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

    private Map<String, Integer> cfValueCacheTime = new HashMap<String, Integer>();

    /**
     * Contains association between entity, and custom field value(s). Key format: <entity class>_<entity id>. Value is a map where key is CFI code as value is a list of values
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

        log.debug("Start to populate custom field value caching time");

        // Calculate cache storage cuttoff time
        Map<String, Date> cfValueCacheTimeAsDate = new HashMap<String, Date>();

        cfValueCacheTime.clear();
        List<CustomFieldTemplate> cfts = customFieldTemplateService.getCFTForCache();
        for (CustomFieldTemplate cft : cfts) {
            cfValueCacheTime.put(cft.getProvider().getId() + "_" + cft.getCode(), cft.getCacheValueTimeperiod());

            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DATE, (-1) * cft.getCacheValueTimeperiod().intValue());

            cfValueCacheTimeAsDate.put(cft.getProvider().getId() + "_" + cft.getCode(), calendar.getTime());
        }
        log.info("Custom field value caching time populated with {} values", cfValueCacheTime.size());

        log.debug("Start to populate custom field value cache");

        customFieldValueCache.clear();

        List<CustomFieldInstance> activeCustomFieldInstances = customFieldInstanceService.getCFIForCache();
        for (CustomFieldInstance cfi : activeCustomFieldInstances) {

            IEntity entity = cfi.getRelatedEntity();
            String cacheKey = getCacheKey(entity);

            Long providerId = null;
            if (entity instanceof Provider) {
                providerId = ((Provider) entity).getId();

            } else {
                providerId = ((IProvider) entity).getProvider().getId();
            }

            log.trace("Add CustomFieldInstance {} to CustomFieldInstance cache for entity {}", cfi.getCode(), cacheKey);

            customFieldValueCache.putIfAbsent(cacheKey, new HashMap<String, List<CachedCFPeriodValue>>());
            List<CachedCFPeriodValue> cfvValues = convertFromCFI(cfi, cfValueCacheTimeAsDate.get(providerId + "_" + cfi.getCode()));
            if (!cfvValues.isEmpty()) {
                customFieldValueCache.get(cacheKey).put(cfi.getCode(), cfvValues);
            }
        }

        log.info("Custom field value populated with {} values", activeCustomFieldInstances.size());
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

        for (CustomFieldInstance cfi : entity.getCustomFields().values()) {
            List<CachedCFPeriodValue> cfvValues = convertFromCFI(cfi, calculateCutoffDate(cfi.getCode(), entity));
            if (!cfvValues.isEmpty()) {
                values.put(cfi.getCode(), cfvValues);
            }
            log.trace("Add CustomFieldInstance {} to CustomFieldInstance cache for entity {}", cfi.getCode(), cacheKey);
        }

        customFieldValueCache.put(cacheKey, values);
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
    public Object getValues(ICustomFieldEntity entity, String cfCode, Date date) {
        return getValues(entity.getClass(), ((IEntity) entity).getId(), cfCode, date);
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
    public Object getValues(Class<? extends ICustomFieldEntity> entityClass, Serializable id, String cfCode, Date date) {

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
            CachedCFPeriodValue value = this.new CachedCFPeriodValue(cfi.getCfValue().getValue());
            values.add(value);

        } else {

            for (CustomFieldPeriod period : cfi.getValuePeriods()) {
                if (cutoffDate == null || period.getPeriodEndDate() == null || (period.getPeriodEndDate() != null && cutoffDate.before(period.getPeriodEndDate()))) {
                    CachedCFPeriodValue value = this.new CachedCFPeriodValue(period.getCfValue().getValue(), period.getPriority(), period.getPeriodStartDate(),
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
     * Store mapping between CF code and value storage in cache time period
     * 
     * @param cft Custom field template definition
     */
    public void addUpdateCustomFieldTemplate(CustomFieldTemplate cft) {

        if (cft.isVersionable()) {
            if (cft.getCacheValueTimeperiod() != null) {
                cfValueCacheTime.put(cft.getProvider().getId() + "_" + cft.getCode(), cft.getCacheValueTimeperiod());

            } else {
                cfValueCacheTime.remove(cft.getProvider().getId() + "_" + cft.getCode());
            }
        }
    }

    /**
     * Remove mapping between CF code and value storage in cache time period
     * 
     * @param cft Custom field template definition
     */
    public void removeCustomFieldTemplate(CustomFieldTemplate cft) {

        if (cft.isVersionable()) {
            cfValueCacheTime.remove(cft.getProvider().getId() + "_" + cft.getCode());
        }
    }

    private class CachedCFPeriodValue implements Serializable {

        private static final long serialVersionUID = -6850614096852110306L;

        private Date periodStartDate;

        private Date periodEndDate;

        private Object value;

        private boolean versioned;

        private int priority;

        public CachedCFPeriodValue(Object value) {
            super();
            this.value = value;
        }

        public CachedCFPeriodValue(Object value, int priority, Date periodStartDate, Date periodEndDate) {
            super();
            this.value = value;
            this.priority = priority;
            this.periodStartDate = periodStartDate;
            this.periodEndDate = periodEndDate;
            versioned = true;
        }

        private boolean isCorrespondsToPeriod(Date date) {
            return (periodStartDate == null || date.compareTo(periodStartDate) >= 0) && (periodEndDate == null || date.before(periodEndDate));
        }

        // public Date getPeriodStartDate() {
        // return periodStartDate;
        // }
        //
        // public Date getPeriodEndDate() {
        // return periodEndDate;
        // }

        private Object getValue() {
            return value;
        }

        private boolean isVersioned() {
            return versioned;
        }

        private int getPriority() {
            return priority;
        }

        @Override
        public String toString() {

            if (versioned) {
                return String.format("CachedCFPeriodValue [priority=%s, periodStartDate=%s, periodEndDate=%s, value=%s, versioned=%s]", priority,
                    DateUtils.formatDateWithPattern(periodStartDate, "yyyy-MM-dd"), DateUtils.formatDateWithPattern(periodEndDate, "yyyy-MM-dd"), value, versioned);
            } else {
                return String.format("CachedCFPeriodValue [value=%s]", value);
            }
        }
    }
}