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
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.inject.Inject;

import org.infinispan.Cache;
import org.slf4j.Logger;

/**
 * Provides cache related services (storing, loading, update) for authorization related operations
 * 
 * @author Andrius Karpavicius
 */
public class AuthorizationCacheContainerProvider implements Serializable { // CacheContainerProvider, Serializable {

    private static final long serialVersionUID = 180156064688145292L;

    @Inject
    protected Logger log;

    /**
     * Stores authorization information. Key format: &lt;Keycloak token id&gt;. Value is a Map of Url=true/false values
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-authorization-cache")
    private Cache<String, Map<String, Boolean>> authorizations;

    /**
     * Get a summary of cached information.
     * 
     * @return A map containing cache information with cache name as a key and cache as a value
     */
    // @Override
    @SuppressWarnings("rawtypes")
    public Map<String, Cache> getCaches() {
        Map<String, Cache> summaryOfCaches = new HashMap<String, Cache>();
        summaryOfCaches.put(authorizations.getName(), authorizations);

        return summaryOfCaches;
    }

    /**
     * Refresh cache by name. Removes current data from cache and populates it again
     * 
     * @param cacheName Name of cache to refresh or null to refresh all caches
     */
    // @Override
    @Asynchronous
    public void refreshCache(String cacheName) {

        if (cacheName == null || cacheName.equals(authorizations.getName()) || cacheName.contains(authorizations.getName())) {
            authoriationCacheClear();
        }
    }

    /**
     * Populate cache by name
     * 
     * @param cacheName Name of cache to populate or null to populate all caches
     */
    // @Override
    public void populateCache(String cacheName) {

        // Nothing to do here
    }

    /**
     * Add authorization information to cache
     * 
     * @param token Keycloak token id
     * @param url URL to store
     * @param isAuthorized Is Url authorized
     */
    public void addAuthorization(String token, String url, boolean isAuthorized) {

        Map<String, Boolean> authMap = authorizations.get(token);
        if (authMap == null) {
            authMap = new HashMap<String, Boolean>();
        }
        authMap.put(url, isAuthorized);

        authorizations.putForExternalRead(token, authMap);

    }

    /**
     * Add authorization information to cache
     * 
     * @param token Keycloak token id
     * @param authorizationChanges Authorization info to add - a map of Url and true/false if Url is authorized
     */
    public void addAuthorization(String token, Map<String, Boolean> authorizationInfo) {

        Map<String, Boolean> authMap = authorizations.get(token);
        if (authMap == null) {
            authMap = authorizationInfo;
        } else {
            authMap.putAll(authorizationInfo);
        }

        authorizations.putForExternalRead(token, authMap);

    }

    /**
     * Is url authorized for a given token
     * 
     * @param token Keycloak token id
     * @param url URL to store
     * @return True/false if url is (not)authorized or NULL if no information is available yet
     */
    public Boolean isAuthorized(String token, String url) {

        Map<String, Boolean> authMap = authorizations.get(token);
        if (authMap != null) {
            return authMap.get(url);
        } else {
            return null;
        }
    }

    /**
     * Clear the data in Tenants cache for the current provider
     */
    public void authoriationCacheClear() {
        authorizations.clear();
    }
}