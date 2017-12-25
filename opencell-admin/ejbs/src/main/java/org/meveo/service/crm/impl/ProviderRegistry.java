package org.meveo.service.crm.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.meveo.model.crm.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads Providers from DB, creates EntityManagerFactories for them.
 */
@Singleton
@Startup
public class ProviderRegistry {

    /**
     * Default, container managed EntityManager
     */
	@PersistenceUnit(unitName = "MeveoAdmin")
    private EntityManager entityManager;

    private final Set<Provider> providers = new HashSet<>();
    private final Map<String, EntityManagerFactory> entityManagerFactories = new HashMap<>();// to move to infinispan

    private Logger logger = LoggerFactory.getLogger(this.getClass());    


    @PostConstruct
    protected void init() {
        final List<Provider> providers = loadProvidersFromDB();
        logger.info("Loaded {} providers from DB.", providers.size());
        providers.forEach(provider -> {
            this.providers.add(provider);
            final EntityManagerFactory emf = createEntityManagerFactory(provider);
            entityManagerFactories.put(provider.getCode(), emf);
            logger.info("Provider " + provider.getCode() + " loaded.");
        });
        this.providers.addAll(providers);
    }

    @PreDestroy
    protected void shutdownProviders() {
        entityManagerFactories.forEach((providerName, entityManagerFactory) -> entityManagerFactory.close());
        entityManagerFactories.clear();
        providers.clear();
    }

    private List<Provider> loadProvidersFromDB() {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Provider> q = cb.createQuery(Provider.class);
        final Root<Provider> c = q.from(Provider.class);
        q.select(c);
        final TypedQuery<Provider> query = entityManager.createQuery(q);
        return query.getResultList();
    }

    /**
     * Create new {@link EntityManagerFactory} using this provider's schema.
     * @param provider Provider used to retrieve schema name
     * @return new EntityManagerFactory
     */
    private EntityManagerFactory createEntityManagerFactory(final Provider provider) {
        final Map<String, String> props = new TreeMap<>();
        logger.debug("Creating entity manager factory on schema '" + provider.getCode() + "' for provider '" + provider.getDescription() + "'.");
        props.put("hibernate.default_schema", provider.getCode());
        return Persistence.createEntityManagerFactory("opencell.admin", props);
    }

    public Optional<Provider> getProvider(final String providerName) {
        return providers.stream().filter(provider -> provider.getCode().equals(providerName)).findFirst();
    }

    /**
     * Returns EntityManagerFactory from the cache. EMF is created during provider registration and initialization.
     * @see #startupProviders()
     */
    public EntityManagerFactory getEntityManagerFactory(final String providerCode) {
    	logger.info("loaded em :{}",entityManagerFactories.size());
        return entityManagerFactories.get(providerCode);
    }
}
