package org.meveo.service.crm.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.meveo.cache.CacheContainerProvider;
import org.meveo.model.crm.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads Providers from DB, creates EntityManagerFactories for them.
 */
@Singleton
@Startup
public class ProviderRegistry {

    @Inject
    private ProviderService providerService;

    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-multiTenant-cache")
    private Cache<String, EntityManagerFactory> entityManagerFactories;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private static Map<String, EntityManager> entityManagers = new HashMap<>();

    @PostConstruct
    protected void init() {
        final List<Provider> providers = providerService.list();
        log.info("Loaded {} providers from DB.", providers.size());
        providers.forEach(provider -> {
            final EntityManagerFactory emf = createEntityManagerFactory(provider);
            addEntityManagerFactoryToCache(System.getProperty(CacheContainerProvider.SYSTEM_PROPERTY_CACHES_TO_LOAD), emf, provider);
            log.info("Provider " + provider.getCode() + " loaded.");
        });
    }

    @Asynchronous
    public void addEntityManagerFactoryToCache(String cacheName, EntityManagerFactory entityManagerFactory, Provider provider) {
        if (cacheName == null || cacheName.equals(entityManagerFactories.getName()) || cacheName.contains(entityManagerFactories.getName())) {
            log.info("EntityManagerFactory Processing CacheContainerProvider initializing...");
            entityManagerFactories.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(provider.getCode(), entityManagerFactory);
        }
    }

    public void removeEntityManagerFactoryFromCache(Provider provider) {
        log.trace("Removing entityManagerFactory for provider {}", provider.getId());
        String cacheKey = provider.getCode();
        EntityManagerFactory entityManagerFactory = entityManagerFactories.getAdvancedCache().withFlags(Flag.FORCE_WRITE_LOCK).get(cacheKey);

        if (entityManagerFactory != null) {
            entityManagerFactories.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).remove(cacheKey);
        } else {
            entityManagerFactories.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cacheKey, entityManagerFactory);
        }
        log.info("Removed entityMangerFactory for provider {}, entityManagerFactories count={}", provider.getId(), entityManagerFactories.size());
    }

    private EntityManagerFactory createEntityManagerFactory(Provider provider) {
        Map<String, String> props = new TreeMap<>();
        log.info("Creating EMF for provider {}", provider.getCode());

        props.put("hibernate.default_schema", provider.getCode());
        return Persistence.createEntityManagerFactory("MeveoAdminMultiTenant", props);
    }

    public EntityManager createEntityManager(String providerCode) {
        log.error("AKK creating entity manager for code {}", providerCode);
        return entityManagerFactories.get(providerCode).createEntityManager();
    }

    public EntityManager createEntityManagerForJobs(String providerCode) {
        
        log.error("AKK creating entity manager for JOBs for code {}", providerCode);
        EntityManager entityManager = null;
        if (entityManagers.containsKey(providerCode)) {
            entityManager = entityManagers.get(providerCode);
        }
        if ((entityManager == null || !entityManager.isOpen()) && entityManagerFactories.containsKey(providerCode)) {
            entityManager = entityManagerFactories.get(providerCode).createEntityManager();
            
            log.error("AKK putting new entity manager for JOBs for code {}", providerCode);
            entityManagers.put(providerCode, entityManager);
        }
        return entityManager;
    }

}
