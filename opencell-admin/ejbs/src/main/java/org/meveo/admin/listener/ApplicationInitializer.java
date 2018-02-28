package org.meveo.admin.listener;

import java.util.List;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.model.crm.Provider;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.base.EntityManagerProvider;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.job.JobInstanceService;
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
    private EntityManagerProvider entityManagerProvider;

    @Inject
    private Logger log;

    public void init() {

        // cleanAllTimers();

        final List<Provider> providers = providerService.list(new PaginationConfiguration("id", SortOrder.ASCENDING));

        int i = 0;
        for (Provider provider : providers) {
            entityManagerProvider.registerEntityManagerFactory(provider.getCode());
            multitenantAppInitializer.initializeTenant(provider, i == 0);
            i++;
        }
    }

    @Asynchronous
    public void initializeTenant(Provider provider, boolean isMainProvider) {

        log.debug("Will initialize application for provider {}", provider.getCode());
        currentUserProvider.forceAuthentication(provider.getAuditable().getCreator(), isMainProvider ? null : provider.getCode());

        jobInstanceService.registerJobs();

        log.info("Initialized application for provider {}", provider.getCode());
    }
}