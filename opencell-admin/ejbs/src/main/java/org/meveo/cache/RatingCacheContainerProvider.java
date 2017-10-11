package org.meveo.cache;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
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
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.service.billing.impl.UsageChargeInstanceService;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;
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
    private PricePlanMatrixService pricePlanMatrixService;

    @EJB
    private UsageChargeInstanceService usageChargeInstanceService;

    @EJB
    private UsageChargeTemplateService usageChargeTemplateService;

    @Inject
    private CalendarService calendarService;

    /**
     * Contains association between charge code and price plans. Key format: <charge template code, which is pricePlanMatrix.eventCode>, value: List of <PricePlanMatrix entity>
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-price-plan")
    private Cache<String, List<PricePlanMatrix>> pricePlanCache;

    /**
     * Contains association between subscription and usage charge instances. Key format: Subscription.id, value: List of <DTO version of UsageChargeInstance>
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-charge-instance-cache")
    private Cache<Long, List<CachedUsageChargeInstance>> usageChargeInstanceCache;

    /**
     * Contains association between counter instance ID and cached counter information. Key format: CounterInstance.id, value: <DTO version of CounterInstance>
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-counter-cache")
    private Cache<Long, CachedCounterInstance> counterCache;

    // @Resource(name = "java:jboss/infinispan/container/meveo")
    // private CacheContainer meveoContainer;

    @PostConstruct
    private void init() {
        try {
            log.debug("RatingCacheContainerProvider initializing...");
            // pricePlanCache = meveoContainer.getCache("meveo-price-plan");
            // usageChargeTemplateCache = meveoContainer.getCache("meveo-usage-charge-template-cache-cache");
            // usageChargeInstanceCache = meveoContainer.getCache("meveo-charge-instance-cache");
            // counterCache = meveoContainer.getCache("meveo-counter-cache");

            refreshCache(System.getProperty(CacheContainerProvider.SYSTEM_PROPERTY_CACHES_TO_LOAD));

            log.info("RatingCacheContainerProvider initialized");

        } catch (Exception e) {
            log.error("RatingCacheContainerProvider init() error", e);
            throw e;
        }
    }

    /**
     * Populate price plan cache from db
     */
    private void populatePricePlanCache() {

        log.debug("Start to populate price plan cache");

        pricePlanCache.clear();

        String lastEventCode = null;
        List<PricePlanMatrix> chargePriceListSameEventCode = null;

        // Retrieve price plans ordered by event code
        List<PricePlanMatrix> activePricePlans = pricePlanMatrixService.getPricePlansForCache();

        for (PricePlanMatrix pricePlan : activePricePlans) {
            if (lastEventCode == null) {
                chargePriceListSameEventCode = new ArrayList<PricePlanMatrix>();
                lastEventCode = pricePlan.getEventCode();

            } else if (!lastEventCode.equals(pricePlan.getEventCode())) {
                Collections.sort(chargePriceListSameEventCode);
                pricePlanCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(lastEventCode, chargePriceListSameEventCode);
                chargePriceListSameEventCode = new ArrayList<PricePlanMatrix>();
                lastEventCode = pricePlan.getEventCode();
            }

            if (pricePlan.getCriteria1Value() != null && pricePlan.getCriteria1Value().length() == 0) {
                pricePlan.setCriteria1Value(null);
            }
            if (pricePlan.getCriteria2Value() != null && pricePlan.getCriteria2Value().length() == 0) {
                pricePlan.setCriteria2Value(null);
            }
            if (pricePlan.getCriteria3Value() != null && pricePlan.getCriteria3Value().length() == 0) {
                pricePlan.setCriteria3Value(null);
            }
            if (pricePlan.getCriteriaEL() != null && pricePlan.getCriteriaEL().length() == 0) {
                pricePlan.setCriteriaEL(null);
            }

            // Lazy loading workaround
            if (pricePlan.getOfferTemplate() != null) {
                pricePlan.getOfferTemplate().getCode();
            }
            if (pricePlan.getScriptInstance() != null) {
                pricePlan.getScriptInstance().getCode();
            }
            if (pricePlan.getValidityCalendar() != null) {
                preloadCache(pricePlan.getValidityCalendar());
            }

            chargePriceListSameEventCode.add(pricePlan);

            // log.trace("Added pricePlan to cache chargeCode {} ; priceplan {}", pricePlan.getEventCode(), pricePlan);
        }

        if (chargePriceListSameEventCode != null && !chargePriceListSameEventCode.isEmpty()) {
            Collections.sort(chargePriceListSameEventCode);
            pricePlanCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(lastEventCode, chargePriceListSameEventCode);
        }

        log.info("Price plan cache populated with {} price plans", activePricePlans.size());
    }

    private void preloadCache(Calendar calendar) {
        if (calendar != null) {
            calendar = calendarService.refreshOrRetrieve(calendar);
            calendar.nextCalendarDate(new Date());
        }
    }

    /**
     * Add price plan to a cache
     * 
     * @param pricePlan Price plan to add
     */
    // @Lock(LockType.WRITE)
    public void addPricePlanToCache(PricePlanMatrix pricePlan) {

        String cacheKey = pricePlan.getEventCode();

        log.trace("Adding pricePlan {} to pricePlan cache under key {}", pricePlan.getId(), cacheKey);

        if (pricePlan.getCriteria1Value() != null && pricePlan.getCriteria1Value().length() == 0) {
            pricePlan.setCriteria1Value(null);
        }
        if (pricePlan.getCriteria2Value() != null && pricePlan.getCriteria2Value().length() == 0) {
            pricePlan.setCriteria2Value(null);
        }
        if (pricePlan.getCriteria3Value() != null && pricePlan.getCriteria3Value().length() == 0) {
            pricePlan.setCriteria3Value(null);
        }
        if (pricePlan.getCriteriaEL() != null && pricePlan.getCriteriaEL().length() == 0) {
            pricePlan.setCriteriaEL(null);
        }
        if (pricePlan.getAmountWithoutTaxEL() != null && pricePlan.getAmountWithoutTaxEL().length() == 0) {
            pricePlan.setAmountWithoutTaxEL(null);
        }
        if (pricePlan.getAmountWithTaxEL() != null && pricePlan.getAmountWithTaxEL().length() == 0) {
            pricePlan.setAmountWithTaxEL(null);
        }

        // Lazy loading workaround
        if (pricePlan.getOfferTemplate() != null) {
            pricePlan.getOfferTemplate().getCode();
        }

        if (pricePlan.getScriptInstance() != null) {
            pricePlan.getScriptInstance().getCode();
        }

        if (pricePlan.getValidityCalendar() != null) {
            preloadCache(pricePlan.getValidityCalendar());
        }

        List<PricePlanMatrix> chargePriceListOld = pricePlanCache.getAdvancedCache().withFlags(Flag.FORCE_WRITE_LOCK).get(cacheKey);

        List<PricePlanMatrix> chargePriceList = new ArrayList<PricePlanMatrix>();
        if (chargePriceListOld != null) {
            chargePriceList.addAll(chargePriceListOld);
        }

        chargePriceList.add(pricePlan);
        Collections.sort(chargePriceList);

        pricePlanCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cacheKey, chargePriceList);
    }

    /**
     * Remove price plan from a cache
     * 
     * @param pricePlan Price plan to remove
     */
    public void removePricePlanFromCache(PricePlanMatrix pricePlan) {

        String cacheKey = pricePlan.getEventCode();

        log.trace("Removing pricePlan {} from priceplan cache under key {}", pricePlan.getId(), cacheKey);

        List<PricePlanMatrix> chargePriceListOld = pricePlanCache.getAdvancedCache().withFlags(Flag.FORCE_WRITE_LOCK).get(cacheKey);

        if (chargePriceListOld != null && !chargePriceListOld.isEmpty()) {
            List<PricePlanMatrix> chargePriceList = new ArrayList<>(chargePriceListOld);
            boolean removed = chargePriceList.remove(pricePlan);
            if (removed) {
                // Remove cached value altogether if no value are left in the list
                if (chargePriceList.isEmpty()) {
                    pricePlanCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).remove(cacheKey);
                } else {
                    pricePlanCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cacheKey, chargePriceList);
                }
                log.trace("Removed pricePlan {} from priceplan cache under key {}", pricePlan.getId(), cacheKey);
            }
        }
    }

    public void updatePricePlanInCache(PricePlanMatrix pricePlan) {
        removePricePlanFromCache(pricePlan);
        addPricePlanToCache(pricePlan);
    }

    /**
     * Get applicable price plans for a given charge code
     * 
     * @param chargeCode Charge code
     * @return A list of applicable price plans
     */
    public List<PricePlanMatrix> getPricePlansByChargeCode(String chargeCode) {
        return pricePlanCache.get(chargeCode);
    }

    /**
     * Populate usage charge related caches
     */
    private void populateUsageChargeCache() {
        log.debug("Loading usage charge cache");

        usageChargeInstanceCache.clear();
        counterCache.clear();

        List<UsageChargeInstance> usageChargeInstances = usageChargeInstanceService.getAllUsageChargeInstancesForCache();
        if (usageChargeInstances != null) {
            log.debug("Loading cache for {} usage charges", usageChargeInstances.size());
            for (UsageChargeInstance usageChargeInstance : usageChargeInstances) {
                addOrUpdateUsageChargeInstanceInCache(usageChargeInstance);
            }
        }

        log.info("Usage charge cache populated with {} usage charge instances", usageChargeInstances.size());
    }

    /**
     * Updated cached usageChangeInstance and related info
     * 
     * @param usageChargeInstance Usage charge instance
     */
    public void addOrUpdateUsageChargeInstanceInCache(UsageChargeInstance usageChargeInstance) {

        if (usageChargeInstance == null) {
            return;
        }

        Long subscriptionId = usageChargeInstance.getServiceInstance().getSubscription().getId();

        log.debug("Updating usageChargeInstance cache with usageChargeInstance: subscription Id: {}, charge id={}, usageChargeTemplate id: {}", subscriptionId,
            usageChargeInstance.getId(), usageChargeInstance.getChargeTemplate().getId());

        // For some reason cast does not work - (exception ChargeTemplate can not be cast to UsageChargeTemplate), so need to look it up by id
        // UsageChargeTemplate usageChargeTemplate = (UsageChargeTemplate) usageChargeInstance.getChargeTemplate();
        UsageChargeTemplate usageChargeTemplate = usageChargeTemplateService.findById(usageChargeInstance.getChargeTemplate().getId());

        boolean cachedSubscriptionContainsCharge = false;

        CachedUsageChargeInstance cachedCharge = null;
        List<CachedUsageChargeInstance> cachedSubscriptionChargesOld = usageChargeInstanceCache.getAdvancedCache().withFlags(Flag.FORCE_WRITE_LOCK).get(subscriptionId);

        List<CachedUsageChargeInstance> cachedSubscriptionCharges = null;
        if (cachedSubscriptionChargesOld != null) {
            cachedSubscriptionCharges = new ArrayList<>(cachedSubscriptionChargesOld);

            for (CachedUsageChargeInstance charge : cachedSubscriptionCharges) {
                if (charge.getId().equals(usageChargeInstance.getId())) {
                    cachedCharge = charge;
                    cachedSubscriptionContainsCharge = true;
                }
            }
        } else {
            cachedSubscriptionCharges = new ArrayList<CachedUsageChargeInstance>();
        }

        if (cachedCharge == null) {
            cachedCharge = new CachedUsageChargeInstance();
        }

        CachedCounterInstance cachedCounterInstance = null;
        if (usageChargeInstance.getCounter() != null) {
            cachedCounterInstance = getOrAddCounterInstanceToCache(usageChargeInstance.getCounter());
        }

        cachedCharge.populateFromUsageChargeInstance(usageChargeInstance, usageChargeTemplate, cachedCounterInstance);

        if (!cachedSubscriptionContainsCharge) {
            cachedSubscriptionCharges.add(cachedCharge);
            Collections.sort(cachedSubscriptionCharges);
        }
        // ANA : There no reasons to keep terminated charge instance in cache
        if (!usageChargeInstance.getStatus().name().equals(InstanceStatusEnum.ACTIVE.name())) {
            cachedSubscriptionCharges.remove(cachedCharge);
        }
        usageChargeInstanceCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(subscriptionId, cachedSubscriptionCharges);

        log.debug("UsageChargeInstance " + (!cachedSubscriptionContainsCharge ? "added" : "updated") + " in usageChargeInstanceCache: subscription id {}, charge id {}",
            subscriptionId, usageChargeInstance.getId());
    }

    /**
     * Update usage charge template in cache. When charge template code and priority value changed, all related charge instances need to be updated and/or reordered
     * 
     * @param usageChargeTemplate Usage charge template to update
     * @return Cached usage charge template
     */
    public void updateUsageChargeTemplateInCache(UsageChargeTemplate usageChargeTemplate) {

        if (usageChargeTemplate == null) {
            return;
        }

        boolean priorityChanged = usageChargeTemplate.isPriorityChanged();

        // Update priority and chargeTemplateCode fields in CachedUsageChargeInstance class if code or priority has changed
        // If priority has changed then reorder all cacheInstance associated to this template. Priority can change only when chargeTemplate was cached earlier, as for new
        // chargeTemplates, no subscriptions are linked yet, thus nothing to reorder.
        if (usageChargeTemplate.isCodeChanged() || priorityChanged) {

            List<Long> subscriptionIds = usageChargeTemplateService.getSubscriptionsAssociated(usageChargeTemplate);
            for (Long subscId : subscriptionIds) {
                updateAndReorderUserChargeInstancesInCache(subscId, usageChargeTemplate, priorityChanged);
            }
        }
        log.debug("UsageChargeTemplate updated in usageChargeTemplateCache: template {}", usageChargeTemplate.getCode());
    }

    /**
     * Update cached usage charge instances with info from charge template and reorder cached usage charge instances associated to a given subscription if priority has changed
     * 
     * @param subscriptionId Subscription ID
     * @param usageChargeTemplate Charge template
     * @param priorityChanged Priority has changed?
     */
    private void updateAndReorderUserChargeInstancesInCache(Long subscriptionId, UsageChargeTemplate usageChargeTemplate, boolean priorityChanged) {

        log.debug("Updating and sorted cached subscription {} usage charges", subscriptionId);

        BiFunction<? super Long, ? super List<CachedUsageChargeInstance>, ? extends List<CachedUsageChargeInstance>> remappingFunction = (subId, charges) -> {
            if (charges == null) {
                return null;
            }

            boolean anyUpdated = false;
            for (CachedUsageChargeInstance cachedUsageChargeInstance : charges) {
                anyUpdated = anyUpdated || cachedUsageChargeInstance.updateChargeTemplateInfo(usageChargeTemplate);
            }

            if (anyUpdated && priorityChanged) {
                Collections.sort(charges);
            }

            return charges;
        };

        usageChargeInstanceCache.compute(subscriptionId, remappingFunction);

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
     * Add counter instance to cache if not cached yet and return cached counter instance
     * 
     * @param counterInstance Counter instance to add
     * @return Cached counter instance
     */
    private CachedCounterInstance getOrAddCounterInstanceToCache(CounterInstance counterInstance) {
        if (counterCache.containsKey(counterInstance.getId())) {
            return counterCache.get(counterInstance.getId());
        } else {
            return addCounterInstanceToCache(counterInstance);
        }
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
     * Are usage charge instances cached for a given subscription
     * 
     * @param subscriptionId Subscription id
     * @return True if usage charge instances cached
     */
    public boolean isUsageChargeInstancesCached(Long subscriptionId) {
        return usageChargeInstanceCache.containsKey(subscriptionId);
    }

    /**
     * Get a list of usage charge instances associated to subscription
     * 
     * @param subscriptionId Subsription id
     * @return A list of usage charge instances
     */
    public List<CachedUsageChargeInstance> getUsageChargeInstances(Long subscriptionId) {
        return usageChargeInstanceCache.get(subscriptionId);
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
        summaryOfCaches.put(pricePlanCache.getName(), pricePlanCache);
        summaryOfCaches.put(usageChargeInstanceCache.getName(), usageChargeInstanceCache);
        summaryOfCaches.put(counterCache.getName(), counterCache);

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

        if (cacheName == null || cacheName.equals(pricePlanCache.getName()) || cacheName.contains(pricePlanCache.getName())) {
            populatePricePlanCache();
        }
        if (cacheName == null || cacheName.equals(usageChargeInstanceCache.getName()) || cacheName.equals(counterCache.getName()) || cacheName.equals(COUNTER_CACHE)
                || cacheName.contains(usageChargeInstanceCache.getName()) || cacheName.contains(counterCache.getName())) {
            populateUsageChargeCache();
        }
    }

    /**
     * Add counterPeriodToCache
     * 
     * @param counterPeriod Counter period
     * @return
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
