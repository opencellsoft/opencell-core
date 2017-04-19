package org.meveo.cache;

import java.util.Map;

import javax.ejb.Asynchronous;

import org.infinispan.commons.api.BasicCache;

public interface CacheContainerProvider {

    /**
     * Refresh cache identified by a particular name, or all caches if not provider. Should be @Asynchronous implementation
     * 
     * @param cacheName Cache name (optional)
     */
    @Asynchronous
    public void refreshCache(String cacheName);

    /**
     * Get a list of caches implemented in a bean
     * 
     * @return A a map containing cache information with cache name as a key and cache as a value
     */
    @SuppressWarnings("rawtypes")
    public Map<String, BasicCache> getCaches();

}