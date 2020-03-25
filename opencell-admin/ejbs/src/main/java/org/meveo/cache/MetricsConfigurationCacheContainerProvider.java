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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.infinispan.Cache;
import org.infinispan.commons.CacheException;
import org.infinispan.context.Flag;
import org.meveo.model.metrics.configuration.MetricsConfiguration;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.metrics.configuration.MetricsConfigurationService;
import org.slf4j.Logger;

/**
 * Provides cache related services (tracking running jobs) for job running related operations
 *
 * @author Andrius Karpavicius
 */
@Stateless
public class MetricsConfigurationCacheContainerProvider implements Serializable {

    @Inject
    protected Logger log;

    /**
     * Contains association between metrics and cluster nodes it runs in.
     * Key format: &lt;metricsName&gt;, value: List of &lt;cluster node name&gt;
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-metrics-config")
    private Cache<CacheKeyStr, Map<String, Map<String, String>>> metricsConfigCache;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    private MetricsConfigurationService metricsConfigurationService;

    /**
     * Get a summary of cached information.
     *
     * @return A list of a map containing cache information with cache name as a key and cache as a value
     */
    @SuppressWarnings("rawtypes")
    public Map<String, Cache> getCaches() {
        Map<String, Cache> summaryOfCaches = new HashMap<>();
        summaryOfCaches.put(metricsConfigCache.getName(), metricsConfigCache);

        return summaryOfCaches;
    }

    /**
     * Refresh cache by name asynchronously. Removes current provider's data from cache and populates it again
     *
     * @param cacheName Name of cache to refresh or null to refresh all caches
     */
    @Asynchronous
    public void refreshCache(String cacheName) {

        if (cacheName == null || cacheName.equals(metricsConfigCache.getName())) {
            clear();
            populateJobCache();
        }
    }

    /**
     * Removes current provider's data from cache and populates it again
     */
    public void clearAndUpdateCache() {
        clear();
        populateJobCache();
    }

    /**
     * Populate cache by name
     *
     * @param cacheName Name of cache to populate or null to populate all caches
     */
    public void populateCache(String cacheName) {

        if (cacheName == null || cacheName.equals(metricsConfigCache.getName())) {
            populateJobCache();
        }
    }

    /**
     * Put item in the cache , and in case of CacheException
     *
     * @param cacheKey
     * @param config
     */
    private void putInCache(CacheKeyStr cacheKey, Map<String, Map<String, String>> config) {
        try {
            // Use flags to not return previous value
            metricsConfigCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cacheKey, config);
        } catch (CacheException e) {
            log.error("PutInCacheWithRetry -> CacheException for [cacheKey = {}]", cacheKey, e);
        }
    }

    /**
     * Initialize cache record for a given metrics.
     *
     * @param metricsKey metrics name identifier
     * @param config
     */
    public void addUpdateMetricsConfig(String metricsKey, Map<String, Map<String, String>> config) {
        this.putInCache(new CacheKeyStr(currentUser.getProviderCode(), metricsKey), config);
    }

    /**
     * Remove metrics from cache
     *
     * @param metricName metrics name identifier
     */
    public void removeMetricCache(String metricName) {
        String currentProvider = currentUser.getProviderCode();

        CacheKeyStr cacheKey = new CacheKeyStr(currentProvider, metricName);
        try { // adding Flag.IGNORE_RETURN_VALUES to enhance the update perfs since we dont need a return value
            metricsConfigCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).remove(cacheKey);
        } catch (CacheException e) {
            log.error("RemoveFromCacheWithRetry -> CacheException for [cacheKey = {}]", cacheKey, e);
        }
    }

    /**
     * Remove metrics from cache
     *
     * @param cacheKey cache key identifier
     */
    public void removeMetric(CacheKeyStr cacheKey) {
        try { // adding Flag.IGNORE_RETURN_VALUES to enhance the update perfs since we dont need a return value
            metricsConfigCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).remove(cacheKey);
        } catch (CacheException e) {
            log.error("RemoveFromCacheWithRetry -> CacheException for [cacheKey = {}]", cacheKey, e);
        }
    }

    /**
     * Initialize cache for all metrics
     */
    private void populateJobCache() {
        log.debug("Start to pre-populate metrics cache of provider {}.", currentUser.getProviderCode());
        List<MetricsConfiguration> list = metricsConfigurationService.findAllForCache();
        Map<String, Map<String, Map<String, String>>> entries = new HashMap<>();
        for (MetricsConfiguration mc : list) {
            Map<String, String> metrics = new HashMap<>();
            metrics.put("metrics_type", mc.getMetricsType());
            metrics.put("metrics_unit", Objects.requireNonNullElse(mc.getMetricsUnit(), ""));
            Map<String, Map<String, String>> values = new HashMap<>();
            values.put(mc.getMethod(), metrics);
            entries.computeIfAbsent(mc.getFullPath(), v -> new HashMap<>()).putAll(values);
        }
        entries.forEach(this::addUpdateMetricsConfig);

        log.debug("End populating metrics cache of Provider {} with {} metrics configs.", currentUser.getProviderCode(), list.size());
    }

    /**
     * Clear the current provider data from cache
     */
    private void clear() {
        String currentProvider = currentUser.getProviderCode();
        Iterator<Entry<CacheKeyStr, Map<String, Map<String, String>>>> iter = metricsConfigCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).entrySet().iterator();
        ArrayList<CacheKeyStr> itemsToBeRemoved = new ArrayList<>();
        while (iter.hasNext()) {
            Entry<CacheKeyStr, Map<String, Map<String, String>>> entry = iter.next();
            boolean comparison = (entry.getKey().getProvider() == null) ? currentProvider == null : entry.getKey().getProvider().equals(currentProvider);
            if (comparison) {
                itemsToBeRemoved.add(entry.getKey());
            }
        }

        for (CacheKeyStr elem : itemsToBeRemoved) {
            log.debug("Remove element Provider:{} Key:{} .", elem.getProvider(), elem.getKey());
            this.removeMetric(elem);
        }
    }

    /**
     * check if the name is configured
     *
     * @param name a request name
     * @return true/false
     */
    public boolean containsKey(String name) {
        String currentProvider = currentUser.getProviderCode();
        CacheKeyStr cacheKeyStr = new CacheKeyStr(currentProvider, name);
        return metricsConfigCache.containsKey(cacheKeyStr);
    }

    /**
     * Return configuration for a request
     *
     * @param name a request name
     * @return a map
     */
    public Map<String, Map<String, String>> getConfiguration(String name) {
        if (containsKey(name)) {
            String currentProvider = currentUser.getProviderCode();
            CacheKeyStr cacheKeyStr = new CacheKeyStr(currentProvider, name);
            return metricsConfigCache.getAdvancedCache().get(cacheKeyStr);
        }
        return Collections.emptyMap();
    }
}