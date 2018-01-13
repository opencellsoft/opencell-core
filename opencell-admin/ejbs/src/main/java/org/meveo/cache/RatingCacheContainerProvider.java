package org.meveo.cache;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

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
import org.meveo.model.CounterValueChangeInfo;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.CounterPeriod;
import org.meveo.service.billing.impl.CounterInstanceService;
import org.slf4j.Logger;

/**
 * Provides cache related services (loading, update) for rating related operations
 * 
 * @author Andrius Karpavicius
 * 
 */
@Startup
@Singleton
@Lock(LockType.READ)
public class RatingCacheContainerProvider implements Serializable { // CacheContainerProvider, Serializable {

    private static final long serialVersionUID = -8177539254337953136L;

    public static String COUNTER_CACHE = "counterCache";

    @Inject
    protected Logger log;

    @EJB
    private CounterInstanceService counterInstanceService;

    /**
     * Contains association between counter instance ID and cached counter information. Key format: CounterInstance.id, value: &lt;DTOversion of CounterInstance&gt;
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-counter-cache")
    private Cache<Long, CachedCounterInstance> counterCache;

    @PostConstruct
    private void init() {
        try {
            log.debug("RatingCacheContainerProvider initializing...");

            refreshCache(System.getProperty(CacheContainerProvider.SYSTEM_PROPERTY_CACHES_TO_LOAD));

            log.info("RatingCacheContainerProvider initialized");

        } catch (Exception e) {
            log.error("RatingCacheContainerProvider init() error", e);
            throw e;
        }
    }

    /**
     * Populate usage charge related caches
     */
    private void populateCounterCache() {
        log.debug("Loading counter cache");

        counterCache.clear();

        List<CounterInstance> counterInstances = counterInstanceService.getCounterInstancesForCache();

        for (CounterInstance counterInstance : counterInstances) {
            addCounterInstanceToCache(counterInstance);
        }

        log.info("Counter cache populated with {} counter instances", counterInstances.size());
    }

    /**
     * Add (or overwrite) counter instance to cache
     * 
     * @param counterInstance Counter instance to add
     * @return Cached counter instance value
     */
    private CachedCounterInstance addCounterInstanceToCache(CounterInstance counterInstance) {

        log.debug("Adding counter to the counter cache counter: {}", counterInstance);

        CachedCounterInstance counterCacheValue = new CachedCounterInstance(counterInstance);
        counterCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(counterInstance.getId(), counterCacheValue);

        return counterCacheValue;
    }

    /**
     * Retrieve cached counter instance
     * 
     * @param counterId Counter instance ID
     * @return Cached counter instance
     */
    public CachedCounterInstance getCounterInstance(Long counterId) {
        return counterCache.get(counterId);
    }

    /**
     * Get a summary of cached information.
     * 
     * @return A list of a map containing cache information with cache name as a key and cache as a value
     */
    // @Override
    @SuppressWarnings("rawtypes")
    public Map<String, Cache> getCaches() {
        Map<String, Cache> summaryOfCaches = new HashMap<String, Cache>();
        summaryOfCaches.put(counterCache.getName(), counterCache);

        return summaryOfCaches;
    }

    /**
     * Refresh cache by name.
     * 
     * @param cacheName Name of cache to refresh or null to refresh all caches
     */
    // @Override
    @Asynchronous
    public void refreshCache(String cacheName) {

        if (cacheName == null || cacheName.equals(counterCache.getName()) || cacheName.equals(COUNTER_CACHE) || cacheName.contains(counterCache.getName())) {
            populateCounterCache();
        }
    }

    /**
     * Add counterPeriodToCache.
     * 
     * @param counterPeriod Counter period
     * @return cached counter period.
     */
    public CachedCounterPeriod addCounterPeriodToCache(CounterPeriod counterPeriod) {

        log.debug("Adding counter period to the counter cache counter: {}", counterPeriod);

        BiFunction<? super Long, ? super CachedCounterInstance, ? extends CachedCounterInstance> remappingFunction = (counterId, cachedCounterInstance) -> {

            // Add a new counter instance
            if (cachedCounterInstance == null) {
                return new CachedCounterInstance(counterPeriod.getCounterInstance());

                // Add period to existing cached counter
            } else {
                CachedCounterInstance newCachedCounterInstance = SerializationUtils.clone(cachedCounterInstance);
                newCachedCounterInstance.addCounterPeriod(counterPeriod);
                return newCachedCounterInstance;
            }
        };

        CachedCounterInstance cachedCounterInstance = counterCache.compute(counterPeriod.getCounterInstance().getId(), remappingFunction);

        return cachedCounterInstance.getCounterPeriod(counterPeriod.getId());

    }

    /**
     * Retrieve cached counter period by counter instance and counter period ids
     * 
     * @param counterInstanceId Counter instance id
     * @param counterPeriodId Counter period id
     * @return Cached counter period
     */
    public CachedCounterPeriod getCounterPeriod(Long counterInstanceId, Long counterPeriodId) {

        CachedCounterInstance cachedCounterInstance = counterCache.get(counterInstanceId);
        if (cachedCounterInstance == null) {
            return null;
        }

        return cachedCounterInstance.getCounterPeriod(counterPeriodId);
    }

    /**
     * Retrieve cached counter period by counter instance and date
     * 
     * @param counterInstanceId Counter instance id
     * @param date Date to match
     * @return Cached counter period
     */
    public CachedCounterPeriod getCounterPeriod(Long counterInstanceId, Date date) {

        CachedCounterInstance cachedCounterInstance = counterCache.get(counterInstanceId);
        if (cachedCounterInstance == null) {
            return null;
        }

        return cachedCounterInstance.getCounterPeriod(date);
    }

    /**
     * Deduce current counterPeriod's value by a given amount. If given amount exceeds current value, only partial amount will be deduced. If ammount is negative, counter value
     * will be incremented
     * 
     * @param counterInstanceId Counter instance id to update
     * @param counterPeriodId Counter period id to update
     * @param deduceBy Amount to deduce by or to increment by if negative value
     * @return Previous, the actual deduced or incremented (negative) value and new counter value. or NULL if value is not tracked (initial counter value is not set)
     */
    public CounterValueChangeInfo deduceCounterValue(Long counterInstanceId, Long counterPeriodId, BigDecimal deduceBy) {

        CachedCounterInstance cachedCounterInstanceOld = counterCache.getAdvancedCache().withFlags(Flag.FORCE_WRITE_LOCK).get(counterInstanceId);
        CachedCounterPeriod counterPeriod = cachedCounterInstanceOld.getCounterPeriod(counterPeriodId);

        // No initial value, so no need to track present value (will always be able to deduce by any amount) and thus no need to update cache
        if (counterPeriod.getLevel() == null) {
            return null;

            // If previous value is not Zero and deduction is not negative (really its an addition)
        } else if (counterPeriod.getValue().compareTo(BigDecimal.ZERO) == 0 && deduceBy.compareTo(BigDecimal.ZERO) > 0) {
            return new CounterValueChangeInfo(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

        } else {

            BigDecimal previousValue;
            BigDecimal deducedQuantity;
            BigDecimal newValue;

            if (counterPeriod.getValue().compareTo(deduceBy) < 0) {

                previousValue = counterPeriod.getValue();
                deducedQuantity = counterPeriod.getValue();
                newValue = BigDecimal.ZERO;

            } else {
                previousValue = counterPeriod.getValue();
                deducedQuantity = deduceBy;
                newValue = previousValue.subtract(deduceBy);
            }

            CachedCounterInstance newCachedCounterInstance = SerializationUtils.clone(cachedCounterInstanceOld);
            CachedCounterPeriod newCounterPeriod = newCachedCounterInstance.getCounterPeriod(counterPeriodId);

            newCounterPeriod.setValue(newValue);

            counterCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(counterInstanceId, newCachedCounterInstance);

            log.debug("Deduced cached {}/{} counter period from {} by {} to a new value {}", counterInstanceId, counterPeriodId, previousValue, deducedQuantity, newValue);

            return new CounterValueChangeInfo(previousValue, deducedQuantity, newValue);
        }
    }
}
