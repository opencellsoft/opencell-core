package org.meveo.admin.listener;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.cache.CacheContainerProvider;
import org.meveo.cache.CdrEdrProcessingCacheContainerProvider;
import org.meveo.cache.CustomFieldsCacheContainerProvider;
import org.meveo.cache.JobCacheContainerProvider;
import org.meveo.cache.NotificationCacheContainerProvider;
import org.meveo.cache.TenantCacheContainerProvider;
import org.meveo.cache.WalletCacheContainerProvider;
import org.meveo.jpa.EntityManagerProvider;
import org.meveo.model.crm.Provider;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.custom.CfValueAccumulator;
import org.meveo.service.index.ElasticClient;
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
    private CfValueAccumulator cfValueAcumulator;

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
    private ElasticClient elasticClient;

    public void init() {

        final List<Provider> providers = providerService.list(new PaginationConfiguration("id", SortOrder.ASCENDING));

        int i = 0;

        // Wait for each provider to initialize.
        for (Provider provider : providers) {

            Future<Boolean> initProvider;

            try {
                initProvider = multitenantAppInitializer.initializeTenant(provider, i == 0, false);

                initProvider.get();

            } catch (InterruptedException | ExecutionException | BusinessException e) {
                log.error("Failed to initialize a provider {}", provider.getCode());
            }
            i++;
        }
    }

    /**
     * Initialize tenant information: establish EMF for secondary tenants/providers, schedule jobs, compile scripts, preload caches
     * 
     * @param provider Tenant/provider to initialize
     * @param isMainProvider Is it a main tenant/provider.
     * @param createESIndex boolean that determines whether to create or not the index
     * @return A future with value of True
     * @throws BusinessException Business exception
     */
    @Asynchronous
    public Future<Boolean> initializeTenant(Provider provider, boolean isMainProvider, boolean createESIndex) throws BusinessException {

        log.debug("Will initialize application for provider {}", provider.getCode());

        if (!isMainProvider) {
            entityManagerProvider.registerEntityManagerFactory(provider.getCode());
        }

        currentUserProvider.forceAuthentication("applicationInitializer", isMainProvider ? null : provider.getCode());

        // Ensure that provider code in secondary provider schema matches the tenant/provider code as it was listed in main provider's secondary tenant/provider record
        if (!isMainProvider) {
//            providerService.updateProviderCode(provider.getCode());
        }

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
        tenantCache.populateCache(System.getProperty(CacheContainerProvider.SYSTEM_PROPERTY_CACHES_TO_LOAD));

        if (createESIndex) {
            elasticClient.cleanAndReindex(MeveoUser.instantiate("applicationInitializer", isMainProvider ? null : provider.getCode()));
        }

        cfValueAcumulator.loadCfAccumulationRules();

        log.info("Initialized application for provider {}", provider.getCode());

        return new AsyncResult<Boolean>(Boolean.TRUE);
    }

}