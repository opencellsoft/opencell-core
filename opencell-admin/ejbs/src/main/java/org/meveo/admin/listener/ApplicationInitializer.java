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

package org.meveo.admin.listener;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.storage.StorageFactory;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import  org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.cache.CacheContainerProvider;
import org.meveo.cache.CdrEdrProcessingCacheContainerProvider;
import org.meveo.cache.CommercialRulesContainerProvider;
import org.meveo.cache.CustomFieldsCacheContainerProvider;
import org.meveo.cache.JobCacheContainerProvider;
import org.meveo.cache.MetricsConfigurationCacheContainerProvider;
import org.meveo.cache.NotificationCacheContainerProvider;
import org.meveo.cache.TenantCacheContainerProvider;
import org.meveo.cache.WalletCacheContainerProvider;
import org.meveo.jpa.EntityManagerProvider;
import org.meveo.model.crm.Provider;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.base.NativePersistenceService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.job.JobInstanceService;
import org.meveo.service.script.ScriptCompilerService;
import org.slf4j.Logger;

import software.amazon.awssdk.services.s3.model.S3Exception;

/**
 * Takes care of initializing/loading various application services/data
 * 
 * @author Andrius Karpavicius
 */
@Stateless
public class ApplicationInitializer {

    @EJB
    private ApplicationInitializer multitenantAppInitializer;

    @Inject
    private ProviderService providerService;

    @Inject
    private CurrentUserProvider currentUserProvider;

    @Inject
    private JobInstanceService jobInstanceService;

    @Inject
    private ScriptCompilerService scriptCompilerService;

    @Inject
    private EntityManagerProvider entityManagerProvider;

//    @Inject
//    private CfValueAccumulator cfValueAcumulator;

    @Inject
    private Logger log;

    @Inject
    private WalletCacheContainerProvider walletCache;

    @Inject
    private CdrEdrProcessingCacheContainerProvider cdrEdrCache;

    @Inject
    private NotificationCacheContainerProvider notifCache;

    @Inject
    private CustomFieldsCacheContainerProvider cftCache;

    @Inject
    private JobCacheContainerProvider jobCache;

    @Inject
    private TenantCacheContainerProvider tenantCache;

    @Inject
    private MetricsConfigurationCacheContainerProvider metricsConfigurationCacheContainerProvider;

    @Inject
    private StorageFactory storageFactory;
    
    @Inject
    private CommercialRulesContainerProvider commercialRulesContainerProvider;

    @Inject
    @Named
    private NativePersistenceService nativePersistenceService;
    
    public void init() {

        final List<Provider> providers = providerService.list(new PaginationConfiguration("id", SortOrder.ASCENDING));

        int i = 0;

        try {
            // Initialize storage factory
            storageFactory.init();
        } catch (Exception e) {
            if (e instanceof ExecutionException && e.getMessage().contains("S3Exception")) {
                throw S3Exception.builder().message(e.getMessage()).build();
            }
        }

        // Wait for each provider to initialize.
        for (Provider provider : providers) {

            // Register a new EM factory for secondary providers
            if (i > 0) {
                entityManagerProvider.registerEntityManagerFactory(provider.getCode());
            }

            try {
                // Is run as Async, so a different provider can be setup for data loading
                Future<Boolean> initProvider = multitenantAppInitializer.initializeTenant(provider, i == 0);
                initProvider.get();

            } catch (InterruptedException | ExecutionException | BusinessException e) {
                log.error("Failed to initialize a provider {}", provider.getCode(), e);
                }
            i++;
        }

    }

    /**
     * Initialize tenant information: establish EMF for secondary tenants/providers, schedule jobs, compile scripts, preload caches.<br/>
     * Is run as Async, so a different provider can be setup for data loading.
     * 
     * @param provider Tenant/provider to initialize
     * @param isMainProvider Is it a main tenant/provider.
     * @param createESIndex boolean that determines whether to create or not the index
     * @return A future with value of True
     */
    @Asynchronous
    public Future<Boolean> initializeTenant(Provider provider, boolean isMainProvider) {

        log.debug("Will initialize application for provider {}", provider.getCode());

        currentUserProvider.forceAuthentication("applicationInitializer", isMainProvider ? null : provider.getCode());

        // Ensure that provider code in secondary provider schema matches the tenant/provider code as it was listed in main provider's secondary tenant/provider record
        if (!isMainProvider) {
            // providerService.updateSecondaryTenantsCode(provider.getCode());
        }

        // Register jobs
        jobInstanceService.registerJobs();

        // Load Custom table field data type mappings 
        nativePersistenceService.refreshTableFieldMapping(null);
        
        // Initialize scripts
        scriptCompilerService.compileAndInitializeAll();

        // Initialize caches
        cftCache.populateCache(System.getProperty(CacheContainerProvider.SYSTEM_PROPERTY_CACHES_TO_LOAD));
        notifCache.populateCache(System.getProperty(CacheContainerProvider.SYSTEM_PROPERTY_CACHES_TO_LOAD));
        jobCache.populateCache(System.getProperty(CacheContainerProvider.SYSTEM_PROPERTY_CACHES_TO_LOAD));
        walletCache.populateCache(System.getProperty(CacheContainerProvider.SYSTEM_PROPERTY_CACHES_TO_LOAD));
        tenantCache.populateCache(System.getProperty(CacheContainerProvider.SYSTEM_PROPERTY_CACHES_TO_LOAD));
        metricsConfigurationCacheContainerProvider.populateCache(System.getProperty(CacheContainerProvider.SYSTEM_PROPERTY_CACHES_TO_LOAD));
        cdrEdrCache.populateCache(System.getProperty(CacheContainerProvider.SYSTEM_PROPERTY_CACHES_TO_LOAD));
        commercialRulesContainerProvider.populateCache();
        
        // cfValueAcumulator.loadCfAccumulationRules();

        log.info("Initialized application for provider {}", provider.getCode());

        return new AsyncResult<Boolean>(Boolean.TRUE);
    }
}