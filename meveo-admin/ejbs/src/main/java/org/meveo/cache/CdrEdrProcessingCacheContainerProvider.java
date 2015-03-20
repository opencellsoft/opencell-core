package org.meveo.cache;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.infinispan.api.BasicCache;
import org.infinispan.manager.CacheContainer;
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
public class CdrEdrProcessingCacheContainerProvider {

    @Inject
    protected Logger log;

    @EJB
    private AccessService accessService;

    @EJB
    private EdrService edrService;

    private ParamBean paramBean = ParamBean.getInstance();

    /**
     * Contains association between access code and accesses sharing this code. Key format: <provider id>_<Access.accessUserId>
     */
    //@Resource(lookup = "java:jboss/infinispan/cache/meveo/meveo-access-cache")
    private BasicCache<String, List<Access>> accessCache;


    @Resource(name = "java:jboss/infinispan/container/meveo")
    private CacheContainer meveoContainer;
    
    /**
     * Stores a list of processed EDR's. Key format: <provider id>_<originBatch>_<originRecord>
     */
    //@Resource(lookup = "java:jboss/infinispan/cache/meveo/meveo-edr-cache")
    private BasicCache<String, Integer> edrCache;

    @PostConstruct
    private void init() {
        try {
            log.debug("CdrEdrProcessingCacheContainerProvider initializing...");
            accessCache = meveoContainer.getCache("meveo-access-cache");
            edrCache = meveoContainer.getCache("meveo-edr-cache");
            
            populateAccessCache();
            populateEdrCache();

            log.debug("CdrEdrProcessingCacheContainerProvider initialized");

        } catch (Exception e) {
            log.error("CdrEdrProcessingCacheContainerProvider init() error", e);
        }
    }

    /**
     * Populate access cache from db
     */
    private void populateAccessCache() {

        log.info("Start to populate access cache");
        List<Access> activeAccesses = accessService.getAccessesForCache();

        for (Access access : activeAccesses) {
            addAccessToCache(access);
        }

        log.debug("Access cache populated with {} accesses", activeAccesses.size());
    }

    /**
     * Add access to a cache
     * 
     * @param access Access to add
     */
    public void addAccessToCache(Access access) {
        String cacheKey = access.getProvider().getId() + "_" + access.getAccessUserId();
        accessCache.putIfAbsent(cacheKey, new ArrayList<Access>());
        //because acccessed later, to avoid lazy init
        access.getSubscription().getId();
        accessCache.get(cacheKey).add(access);
        log.info("Added access {} to access cache", access);
    }

    /**
     * Remove access from cache
     * 
     * @param access Access to remove
     */
    public void removeAccessFromCache(Access access) {
        accessCache.get(access.getProvider().getId() + "_" + access.getAccessUserId()).remove(access);
        log.info("Removed access {} from access cache", access);
    }

    /**
     * Update access in cache
     * 
     * @param access Access to update
     */
    public void updateAccessInCache(Access access) {
        removeAccessFromCache(access);
        addAccessToCache(access);
    }

    /**
     * Get a list of accesses for a given provider and access user id
     * 
     * @param providerId Provider id
     * @param accessUserId Access user id
     * @return A list of accesses
     */
    public List<Access> getAccessesByAccessUserId(Long providerId, String accessUserId) {
        return accessCache.get(providerId + "_" + accessUserId);
    }

    /**
     * Populate EDR cache from db
     */
    private void populateEdrCache() {

        boolean useInMemoryDeduplication = paramBean.getProperty("mediation.deduplicateInMemory", "true").equals("true");
        if (!useInMemoryDeduplication) {
            log.info("EDR cache population will be skipped");
            return;
        }

        log.info("Start to populate EDR cache");

        int maxDuplicateRecords = Integer.parseInt(paramBean.getProperty("mediation.deduplicateCacheSize", "100000"));
        List<String> edrs = edrService.getUnprocessedEdrsForCache(maxDuplicateRecords);
        for (String edrHash : edrs) {
            edrCache.put(edrHash, 0);
        }

        log.debug("EDR cache populated with {} EDRs", edrs.size());
    }

    /**
     * Check if EDR is cached for a given provider, originBatch and originRecord
     * 
     * @param providerId Provider id
     * @param originBatch Origin batch
     * @param originRecord Origin record
     * @return True if EDR is cached
     */
    public boolean isEDRCached(Long providerId, String originBatch, String originRecord) {

        return edrCache.containsKey(providerId + "_" + originBatch + '_' + originRecord);
    }

    /**
     * Add EDR to cache
     * 
     * @param edr EDR to add to cache
     */
    public void addEdrToCache(EDR edr) {
        edrCache.putIfAbsent(edr.getProvider().getId() + "_" + edr.getOriginBatch() + "_" + edr.getOriginRecord(), 0);
    }
}