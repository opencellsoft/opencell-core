package org.meveo.cache;

import java.io.Serializable;
import java.util.ArrayList;
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
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.mediation.Access;
import org.meveo.model.rating.EDR;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.medina.impl.AccessService;
import org.slf4j.Logger;

/**
 * Provides cache related services (loading, update) for CDR and EDR processing related operations
 * 
 * @author Andrius Karpavicius
 * 
 */
@Startup
@Singleton
@Lock(LockType.READ)
public class CdrEdrProcessingCacheContainerProvider implements Serializable { // CacheContainerProvider, Serializable {

    private static final long serialVersionUID = 1435137623784514994L;

    @Inject
    protected Logger log;

    @EJB
    private AccessService accessService;

    @EJB
    private EdrService edrService;

    private ParamBean paramBean = ParamBean.getInstance();

    /**
     * Contains association between access code and accesses sharing this code. Key format: &lt;Access.accessUserId&gt;, value: List of &lt;Access entity&gt;
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-access-cache")
    private Cache<String, List<Access>> accessCache;

    /**
     * Stores a list of processed EDR's. Key format: &lt;originBatch&gt;_&lt;originRecord&gt;, value: 0 (no meaning, only keys are used)
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-edr-cache")
    private Cache<String, Integer> edrCache;

    @PostConstruct
    private void init() {
        try {
            log.debug("CdrEdrProcessingCacheContainerProvider initializing...");
            // accessCache = meveoContainer.getCache("meveo-access-cache");
            // edrCache = meveoContainer.getCache("meveo-edr-cache");

            refreshCache(System.getProperty(CacheContainerProvider.SYSTEM_PROPERTY_CACHES_TO_LOAD));

            log.info("CdrEdrProcessingCacheContainerProvider initialized");

        } catch (Exception e) {
            log.error("CdrEdrProcessingCacheContainerProvider init() error", e);
            throw e;
        }
    }

    /**
     * Populate access cache from db.
     */
    private void populateAccessCache() {

        log.debug("Start to populate access cache");

        List<Access> activeAccesses = accessService.getAccessesForCache();
        accessCache.clear();

        for (Access access : activeAccesses) {
            addAccessToCache(access);
        }

        log.info("Access cache populated with {} accesses", activeAccesses.size());
    }

    /**
     * Add access to a cache.
     * 
     * @param access Access to add
     */
    // @Lock(LockType.WRITE)
    public void addAccessToCache(Access access) {

        log.trace("Adding access {} to access cache", access.getId());

        // because accessed later, to avoid lazy init
        access.getSubscription().getId();

        String cacheKey = access.getAccessUserId();

        List<Access> accessesOld = accessCache.getAdvancedCache().withFlags(Flag.FORCE_WRITE_LOCK).get(cacheKey);

        List<Access> accesses = new ArrayList<Access>();
        if (accessesOld != null) {
            accesses.addAll(accessesOld);
        }
        accesses.add(access);

        accessCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cacheKey, accesses);

    }

    /**
     * Remove access from cache.
     * 
     * @param access Access to remove
     */
    // @Lock(LockType.WRITE)
    public void removeAccessFromCache(Access access) {

        log.trace("Removing access {} from access cache", access.getId());

        String cacheKey = access.getAccessUserId();
        List<Access> accessesOld = accessCache.getAdvancedCache().withFlags(Flag.FORCE_WRITE_LOCK).get(cacheKey);

        if (accessesOld != null && !accessesOld.isEmpty()) {
            List<Access> accesses = new ArrayList<>(accessesOld);
            boolean removed = accesses.remove(access);
            if (removed) {
                // Remove cached value altogether if no value are left in the list
                if (accesses.isEmpty()) {
                    accessCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).remove(cacheKey);
                } else {
                    accessCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cacheKey, accesses);
                }

                log.trace("Removed access {} from access cache", access.getId());
            }
        }
    }

    /**
     * Update access in cache.
     * 
     * @param access Access to update
     */
    public void updateAccessInCache(Access access) {
        removeAccessFromCache(access);
        addAccessToCache(access);
    }

    /**
     * Get a list of accesses for a given access user id.
     * 
     * @param accessUserId Access user id
     * @return A list of accesses
     */
    public List<Access> getAccessesByAccessUserId(String accessUserId) {

        String cacheKey = accessUserId;
        return accessCache.get(cacheKey);
    }

    /**
     * Populate EDR cache from db.
     */
    private void populateEdrCache() {

        boolean useInMemoryDeduplication = paramBean.getProperty("mediation.deduplicateInMemory", "true").equals("true");
        if (!useInMemoryDeduplication) {
            log.info("EDR cache population will be skipped");
            return;
        }

        log.debug("Start to populate EDR cache");

        edrCache.clear();

        List<String> edrCacheKeys = edrService.getUnprocessedEdrsForCache();

        for (String edrCacheKey : edrCacheKeys) {
            edrCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(edrCacheKey, 0);
        }

        log.info("EDR cache populated with {} EDRs", edrCacheKeys.size());
    }

    /**
     * Check if EDR is cached for a given originBatch and originRecord.
     * 
     * @param originBatch Origin batch
     * @param originRecord Origin record
     * @return True if EDR is cached
     */
    public boolean isEDRCached(String originBatch, String originRecord) {

        return edrCache.containsKey(originBatch + '_' + originRecord);
    }

    /**
     * Add EDR to cache.
     * 
     * @param edr EDR to add to cache
     */
    public void addEdrToCache(EDR edr) {
        edrCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(edr.getOriginBatch() + "_" + edr.getOriginRecord(), 0);
    }

    /**
     * Get a summary of cached information.
     * 
     * @return A a map containing cache information with cache name as a key and cache as a value
     */
    // @Override
    @SuppressWarnings("rawtypes")
    public Map<String, Cache> getCaches() {
        Map<String, Cache> summaryOfCaches = new HashMap<String, Cache>();
        summaryOfCaches.put(accessCache.getName(), accessCache);
        summaryOfCaches.put(edrCache.getName(), edrCache);

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

        if (cacheName == null || cacheName.equals(accessCache.getName()) || cacheName.contains(accessCache.getName())) {
            populateAccessCache();

        }
        if (cacheName == null || cacheName.equals(edrCache.getName()) || cacheName.contains(edrCache.getName())) {
            populateEdrCache();
        }
    }
}