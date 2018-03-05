package org.meveo.service.crm.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.model.crm.Provider;
import org.meveo.service.base.EntityManagerProvider;

/**
 * Manages providers connection to DB
 */
@Stateless
public class ProviderRegistry {

    @Inject
    private EntityManagerProvider entityManagerProvider;

    /**
     * Unregister a provider
     * 
     * @param provider Provider to unregister
     */
    public void removeProvider(Provider provider) {
        entityManagerProvider.unregisterEntityManagerFactory(provider.getCode());
    }
}