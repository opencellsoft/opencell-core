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