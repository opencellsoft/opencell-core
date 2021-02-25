/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.crm.impl.ProviderService;
import org.slf4j.Logger;

/**
 * Provides cache related services (loading, update) for CDR and EDR processing related operations
 * 
 * @author Andrius Karpavicius
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 * 
 */
@Stateless
public class CdrEdrProcessingCacheContainerProvider implements Serializable { // CacheContainerProvider, Serializable {

    private static final long serialVersionUID = 1435137623784514994L;

    @Inject
    protected Logger log;

    @EJB
    private EdrService edrService;

    private ParamBean paramBean = ParamBeanFactory.getAppScopeInstance();

    /**
     * Stores a list of processed EDR's. Key format: &lt;originBatch&gt;_&lt;originRecord&gt;, value: 0 (no meaning, only keys are used)
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-edr-cache")
    private Cache<CacheKeyStr, Boolean> edrCache;

    @Inject
    private ProviderService providerService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    /**
     * Populate EDR cache from db.
     */
    private void populateEdrCache() {

        boolean useInMemoryDeduplication = EdrService.DeduplicateEDRTypeEnum.MEMORY.name().equalsIgnoreCase(paramBean.getProperty("mediation.deduplicate", EdrService.DeduplicateEDRTypeEnum.MEMORY.name()));
        if (!useInMemoryDeduplication) {
            log.info("EDR cache population will be skipped as cache will not be used");
            return;
        }

        boolean prepopulateMemoryDeduplication = paramBean.getProperty("mediation.deduplicateInMemory.prepopulate", "true").equals("true");
        if (!prepopulateMemoryDeduplication) {
            log.info("EDR cache is used, but pre-population will be skipped");
            return;
        }

        String currentProvider = currentUser.getProviderCode();

        log.debug("Start to pre-populate EDR cache for provider {}", currentProvider);

        int maxRecords = Integer.parseInt(paramBean.getProperty("mediation.deduplicateInMemory.size", "100000"));
        int pageSize = Integer.parseInt(paramBean.getProperty("mediation.deduplicateInMemory.pageSize", "1000"));

        final Integer[] totalEdrs = new Integer[1];
        totalEdrs[0] = 0;

        // for each provider we fill the cache with initial equal portion of the cache
        // This may vary during the run
        final int nbProviders = providerService.list().size();
        int maxRecordsPerProvider = maxRecords / nbProviders;
        for (int from = 0; from < maxRecordsPerProvider; from = from + pageSize) {
            List<String> edrCacheKeys = edrService.getUnprocessedEdrsForCache(from, pageSize);
            Map<CacheKeyStr, Boolean> mappedEdrCacheKeys = edrCacheKeys.stream().distinct().collect(Collectors.toMap(p -> new CacheKeyStr(currentProvider, p), p -> true));

            edrCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).putAll(mappedEdrCacheKeys);

            int retrievedSize = edrCacheKeys.size();
            totalEdrs[0] = totalEdrs[0] + retrievedSize;

            log.info("EDR cache pre-populated with {} EDRs", retrievedSize);

            if (retrievedSize < pageSize) {
                break;
            }
        }

        log.info("Finished to pre-populate EDR cache with {} for provider {}", totalEdrs[0], currentProvider);
    }

    /**
     * Check if EDR exists already for a given originBatch and originRecord.
     * 
     * @param originBatch Origin batch
     * @param originRecord Origin record
     * @return True if EDR is cached
     */
    public Boolean getEdrDuplicationStatus(String originBatch, String originRecord) {

        return edrCache.get(new CacheKeyStr(currentUser.getProviderCode(), originBatch + '_' + originRecord));
    }

    /**
     * Set to cache that EDR with a given originBatch and originRecord already exists.
     * 
     * @param originBatch Origin batch
     * @param originRecord Origin record
     */
    public void setEdrDuplicationStatus(String originBatch, String originRecord) {
        edrCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(new CacheKeyStr(currentUser.getProviderCode(), originBatch + '_' + originRecord), true);
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
     * Refresh cache by name. Removes current provider's data from cache and populates it again
     * 
     * @param cacheName Name of cache to refresh or null to refresh all caches
     */
    // @Override
    @Asynchronous
    public void refreshCache(String cacheName) {

        if (cacheName == null || cacheName.equals(edrCache.getName()) || cacheName.contains(edrCache.getName())) {
            clear();
            populateEdrCache();
        }
    }

    /**
     * Refresh cache by name. Same as populateCache(), but cleans it up cache before.
     * 
     * @param cacheName Name of cache to populate or null to populate all caches
     */
    // @Override
    public void populateCache(String cacheName) {

        if (cacheName == null || cacheName.equals(edrCache.getName()) || cacheName.contains(edrCache.getName())) {
            populateEdrCache();
        }
    }

    /**
     * Clear the data belonging to the current provider from cache
     */
    public void clear() {
        String currentProvider = currentUser.getProviderCode();
        // edrCache.keySet().removeIf(key -> (key.getProvider() == null) ? currentProvider == null : key.getProvider().equals(currentProvider));
        Iterator<Entry<CacheKeyStr, Boolean>> iter = edrCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).entrySet().iterator();
        ArrayList<CacheKeyStr> itemsToBeRemoved = new ArrayList<>();
        while (iter.hasNext()) {
            Entry<CacheKeyStr, Boolean> entry = iter.next();
            boolean comparison = (entry.getKey().getProvider() == null) ? currentProvider == null : entry.getKey().getProvider().equals(currentProvider);
            if (comparison) {
                itemsToBeRemoved.add(entry.getKey());
            }
        }

        for (CacheKeyStr elem : itemsToBeRemoved) {
            log.debug("Remove element Provider:" + elem.getProvider() + " Key:" + elem.getKey() + ".");
            edrCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).remove(elem);
        }
    }

    /**
     * Clear the data belonging to the current provider from cache
     * 
     */
    public void clearAll() {
        edrCache.clear();
    }
}