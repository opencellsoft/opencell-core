package org.meveo.cache;

import java.util.Map;

import org.infinispan.Cache;

public interface CacheContainerProvider {

    /**
     * System property indicating what caches should be loaded on a current cluster node. Dont pass any value for single-server installation.
     */
    public static String SYSTEM_PROPERTY_CACHES_TO_LOAD = "opencell.caches.load";

    /**
     * Refresh cache identified by a particular name, or all caches if not provider. Should be @Asynchronous implementation
     * 
     * @param cacheName Cache name (optional)
     */
    // @Asynchronous
    public void refreshCache(String cacheName);

    /**
     * Get a list of caches implemented in a bean
     * 
     * @return A a map containing cache information with cache name as a key and cache as a value
     */
    // @SuppressWarnings("rawtypes")
    public Map<String, Cache> getCaches();

}