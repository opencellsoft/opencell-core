package org.meveo.admin.listener;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.cache.CacheContainerProvider;
import org.meveo.cache.CdrEdrProcessingCacheContainerProvider;
import org.meveo.cache.CustomFieldsCacheContainerProvider;
import org.meveo.cache.JobCacheContainerProvider;
import org.meveo.cache.NotificationCacheContainerProvider;
import org.meveo.cache.WalletCacheContainerProvider;
import org.meveo.model.crm.Provider;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.base.EntityManagerProvider;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.job.JobInstanceService;
import org.meveo.service.script.ScriptInstanceService;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;

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
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private EntityManagerProvider entityManagerProvider;

    @Inject
    private Logger log;

    @Inject
    WalletCacheContainerProvider walletCache;

    @Inject
    CdrEdrProcessingCacheContainerProvider cdrEdrCache;

    @Inject
    NotificationCacheContainerProvider notifCache;

    @Inject
    CustomFieldsCacheContainerProvider cftCache;

    @Inject
    JobCacheContainerProvider jobCache;

    public void init() {

        final List<Provider> providers = providerService.list(new PaginationConfiguration("id", SortOrder.ASCENDING));

        int i = 0;
        for (Provider provider : providers) {
            Future<Boolean> initProvider = multitenantAppInitializer.initializeTenant(provider, i == 0);

            // Wait for each provider to initialize
            try {
                initProvider.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Failed to initialize a provider {}", provider.getCode());
            }
            i++;
        }
    }

    @Asynchronous
    public Future<Boolean> initializeTenant(Provider provider, boolean isMainProvider) {

        log.debug("Will initialize application for provider {}", provider.getCode());

        if (!isMainProvider) {
            entityManagerProvider.registerEntityManagerFactory(provider.getCode());
        }

        currentUserProvider.forceAuthentication(provider.getAuditable().getCreator(), isMainProvider ? null : provider.getCode());

        // Register jobs
        jobInstanceService.registerJobs();

        // Initialize scripts
        scriptInstanceService.compileAll();

        // Initialize caches
        walletCache.populateCache(System.getProperty(CacheContainerProvider.SYSTEM_PROPERTY_CACHES_TO_LOAD));
        cdrEdrCache.populateCache(System.getProperty(CacheContainerProvider.SYSTEM_PROPERTY_CACHES_TO_LOAD));
        notifCache.populateCache(System.getProperty(CacheContainerProvider.SYSTEM_PROPERTY_CACHES_TO_LOAD));
        cftCache.populateCache(System.getProperty(CacheContainerProvider.SYSTEM_PROPERTY_CACHES_TO_LOAD));
        jobCache.populateCache(System.getProperty(CacheContainerProvider.SYSTEM_PROPERTY_CACHES_TO_LOAD));

        log.info("Initialized application for provider {}", provider.getCode());

        return new AsyncResult<Boolean>(Boolean.TRUE);
    }

}