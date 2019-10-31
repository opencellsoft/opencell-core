package org.meveo.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.infinispan.Cache;
import org.infinispan.commons.CacheException;
import org.infinispan.context.Flag;
import org.meveo.model.metric.configuration.MetricConfiguration;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.metric.configuration.MetricConfigurationService;
import org.slf4j.Logger;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

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
    private Cache<CacheKeyStr, Map<String, String>> metricsConfigCache;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    private MetricConfigurationService metricConfigurationService;

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
     * Refresh cache by name. Removes current provider's data from cache and populates it again
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
    private void putInCache(CacheKeyStr cacheKey, Map<String, String> config) {
        try {
            // Use flags to not return previous value
            metricsConfigCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cacheKey, config);
        } catch (CacheException e) {
            log.error("PutInCacheWithRetry -> CacheException for [cacheKey = {}]", cacheKey, e);
        }
    }

    /**
     * Initialize cache record for a given metric.
     * According to Infinispan documentation in clustered mode one node is treated as primary node to manage a particular key
     *
     * @param metricKey metric name identifier
     * @param config
     */
    public void addUpdateMetricsConfig(String metricKey, Map<String, String> config) {
        this.putInCache(new CacheKeyStr(currentUser.getProviderCode(), metricKey), config);
    }

    /**
     * Remove metric from cache
     *
     * @param metricName metric name identifier
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
     * Remove metric from cache
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
        List<MetricConfiguration> list = metricConfigurationService.findAllForCache();
        Map<String, Map<String, String>> metricsConfig = list.stream()
                .collect(groupingBy(MetricConfiguration::getFullPath, toMap(MetricConfiguration::getMethod, MetricConfiguration::getMetricType)));
        for (Entry<String, Map<String, String>> metric : metricsConfig.entrySet()) {
            this.addUpdateMetricsConfig(metric.getKey(), metric.getValue());
        }
        log.debug("End populating metrics cache of Provider {} with {} metrics configs.", currentUser.getProviderCode(), metricsConfig.size());
    }

    /**
     * Clear the current provider data from cache
     */
    private void clear() {
        String currentProvider = currentUser.getProviderCode();
        Iterator<Entry<CacheKeyStr, Map<String, String>>> iter = metricsConfigCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).entrySet().iterator();
        ArrayList<CacheKeyStr> itemsToBeRemoved = new ArrayList<>();
        while (iter.hasNext()) {
            Entry<CacheKeyStr, Map<String, String>> entry = iter.next();
            boolean comparison = (entry.getKey().getProvider() == null) ? currentProvider == null : entry.getKey().getProvider().equals(currentProvider);
            if (comparison) {
                itemsToBeRemoved.add(entry.getKey());
            }
        }

        for (CacheKeyStr elem : itemsToBeRemoved) {
            log.debug("Remove element Provider:" + elem.getProvider() + " Key:" + elem.getKey() + ".");
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
    public Map<String, String> getConfiguration(String name) {
        if (containsKey(name)) {
            String currentProvider = currentUser.getProviderCode();
            CacheKeyStr cacheKeyStr = new CacheKeyStr(currentProvider, name);
            return metricsConfigCache.getAdvancedCache().get(cacheKeyStr);
        }
        return Collections.emptyMap();
    }
}