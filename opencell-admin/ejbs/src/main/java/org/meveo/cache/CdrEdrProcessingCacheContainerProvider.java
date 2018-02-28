package org.meveo.cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.crm.Provider;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.crm.impl.ProviderService;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;

/**
 * Provides cache related services (loading, update) for CDR and EDR processing related operations
 * 
 * @author Andrius Karpavicius
 * 
 */
//@Startup
@Singleton
@Lock(LockType.READ)
public class CdrEdrProcessingCacheContainerProvider implements Serializable { // CacheContainerProvider, Serializable {

    private static final long serialVersionUID = 1435137623784514994L;

    @Inject
    protected Logger log;

    @EJB
    private EdrService edrService;

    private ParamBean paramBean = ParamBean.getInstance();

    /**
     * Stores a list of processed EDR's. Key format: &lt;originBatch&gt;_&lt;originRecord&gt;, value: 0 (no meaning, only keys are used)
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-edr-cache")
    private Cache<CacheKeyStr, Boolean> edrCache;
    
    @Inject
    private ProviderService providerService;
    
    @Inject
    private CurrentUserProvider currentUserProvider;

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
     * Populate EDR cache from db.
     */
    private void populateEdrCache() {

        boolean useInMemoryDeduplication = paramBean.getProperty("mediation.deduplicateInMemory", "true").equals("true");
        if (!useInMemoryDeduplication) {
            log.info("EDR cache population will be skipped as cache will not be used");
            return;
        }

        //edrCache.clear();
        // remove current provider data from cache
        clear();
        
        boolean prepopulateMemoryDeduplication = paramBean.getProperty("mediation.deduplicateInMemory.prepopulate", "false").equals("true");
        if (!prepopulateMemoryDeduplication) {
            log.info("EDR cache pre-population will be skipped");
            return;
        }

        log.debug("Start to pre-populate EDR cache");

        int maxRecords = Integer.parseInt(paramBean.getProperty("mediation.deduplicateInMemory.size", "100000"));
        int pageSize = Integer.parseInt(paramBean.getProperty("mediation.deduplicateInMemory.pageSize", "1000"));

        final Integer[] totalEdrs = new Integer[1];
        totalEdrs[0] = 0;
        
        // for each provider we fill the cache with initial equal portion of the cache 
        // This may vary during the run 
        final int nbProviders = providerService.list().size();
        int maxRecordsPerProvider = maxRecords/nbProviders;
    	for (int from = 0; from < maxRecordsPerProvider; from = from + pageSize) {	          	
            List<String> edrCacheKeys = edrService.getUnprocessedEdrsForCache(from, pageSize);
            List<String> distinct = edrCacheKeys.stream().distinct().collect(Collectors.toList());
            Map<CacheKeyStr, Boolean> mappedEdrCacheKeys = distinct.stream().collect(Collectors.toMap(p -> new CacheKeyStr(currentUserProvider.getCurrentUserProviderCode(),p), p -> true));

            edrCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).putAll(mappedEdrCacheKeys);

            int retrievedSize = edrCacheKeys.size();
            totalEdrs[0] = totalEdrs[0] + retrievedSize;

            log.info("EDR cache pre-populated with {} EDRs", retrievedSize);

            if (retrievedSize < pageSize) {
                break;
            }
        } 

        log.info("Finished to pre-populate EDR cache with {} for provider {}", totalEdrs[0], currentUserProvider.getCurrentUserProviderCode());
    }

    /**
     * Check if EDR exists already for a given originBatch and originRecord.
     * 
     * @param originBatch Origin batch
     * @param originRecord Origin record
     * @return True if EDR is cached
     */
    public Boolean getEdrDuplicationStatus(String originBatch, String originRecord) {

        return edrCache.get(new CacheKeyStr(currentUserProvider.getCurrentUserProviderCode(), originBatch + '_' + originRecord));
    }

    /**
     * Set to cache that EDR with a given originBatch and originRecord already exists.
     * 
     * @param originBatch Origin batch
     * @param originRecord Origin record
     */
    public void setEdrDuplicationStatus(String originBatch, String originRecord) {
        edrCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(new CacheKeyStr(currentUserProvider.getCurrentUserProviderCode(), originBatch + '_' + originRecord), true);
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

        if (cacheName == null || cacheName.equals(edrCache.getName()) || cacheName.contains(edrCache.getName())) {
            populateEdrCache();
        }
    }
    
    /**
     * clear the data belonging to the current provider from cache 
     * 
     * @param 
     */
    public void clear() {
    	String currentProvider = currentUserProvider.getCurrentUserProviderCode();
    	edrCache.keySet().removeIf(key -> key.getProvider().equals(currentProvider));
    }
    
    /**
     * clear the data belonging to the current provider from cache 
     * 
     * @param 
     */
    public void clearAll() {
    	edrCache.clear();
    }
}