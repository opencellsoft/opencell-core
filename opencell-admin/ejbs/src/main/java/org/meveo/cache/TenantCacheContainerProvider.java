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
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.crm.Provider;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.commons.utils.PersistenceUtils;
import org.slf4j.Logger;

/**
 * Provides cache related services (loading, update) for custom field value related operations
 * 
 * @author Andrius Karpavicius
 * @author Wassim Drira
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class TenantCacheContainerProvider implements Serializable { // CacheContainerProvider, Serializable {

    private static final long serialVersionUID = 180156064688145292L;

    @Inject
    protected Logger log;

    private static boolean useTenantCache = true;

    /**
     * Stores provider/tenant information. Key format: &lt;provider code&gt;. Value is a Provider entity
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-tenant-cache")
    private Cache<String, Provider> tenants;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    static {
        ParamBean tmpParamBean = ParamBeanFactory.getAppScopeInstance();
        useTenantCache = Boolean.parseBoolean(tmpParamBean.getProperty("cache.cacheTenant", "true"));
    }

    /**
     * Get a summary of cached information.
     * 
     * @return A map containing cache information with cache name as a key and cache as a value
     */
    // @Override
    @SuppressWarnings("rawtypes")
    public Map<String, Cache> getCaches() {
        Map<String, Cache> summaryOfCaches = new HashMap<String, Cache>();
        summaryOfCaches.put(tenants.getName(), tenants);

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

        if (cacheName == null || cacheName.equals(tenants.getName()) || cacheName.contains(tenants.getName())) {
            tenantsClear();
        }
    }

    /**
     * Populate cache by name
     * 
     * @param cacheName Name of cache to populate or null to populate all caches
     */
    // @Override
    public void populateCache(String cacheName) {

        // if (cacheName == null || cacheName.equals(tenants.getName()) || cacheName.contains(tenants.getName())) {
        // }
    }

    /**
     * Store tenant/provider information
     * 
     * @param provider Provider entity
     */
    public void addUpdateTenant(Provider provider) {

        if (!useTenantCache) {
            return;
        }

        log.trace("Adding/updating tenant {} to Tenant cache of Provider {}", provider.getCode(), currentUser.getProviderCode());

        if (provider.getCurrency() != null) {
            provider.getCurrency().getCurrencyCode();
        }
        if (provider.getCountry() != null) {
            provider.getCountry().getCountryCode();
        }
        if (provider.getLanguage() != null) {
            provider.getLanguage().getLanguageCode();
        }
        if (provider.getInvoiceConfiguration() != null) {
            provider.getInvoiceConfiguration().isDisplayBillingCycle();
        }
        if (provider.getGdprConfiguration() != null) {
            provider.getGdprConfiguration().getAccountingLife();
        }
        provider.getPaymentMethods().size();

        provider = PersistenceUtils.initializeAndUnproxy(provider);

        // detach(provider);

        tenants.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(currentUser.getProviderCode() == null ? "null" : currentUser.getProviderCode(), provider);
    }

    /**
     * Get provider/tenant
     * 
     * @return A Provider entity
     */
    public Provider getTenant() {
        return tenants.get(currentUser.getProviderCode() == null ? "null" : currentUser.getProviderCode());
    }

    /**
     * Clear the data in Tenants cache for the current provider
     */
    public void tenantsClear() {
        tenants.remove(currentUser.getProviderCode());
    }

    /**
     * Clear all the data in Tenants cache
     */
    public void tenantsClearAll() {
        tenants.clear();
    }
}