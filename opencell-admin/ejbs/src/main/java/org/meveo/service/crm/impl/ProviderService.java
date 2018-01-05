/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.crm.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.beanutils.BeanUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.event.monitoring.ClusterEventDto.CrudActionEnum;
import org.meveo.event.monitoring.ClusterEventPublisher;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;

/**
 * Provider service implementation.
 */
@Stateless
public class ProviderService extends PersistenceService<Provider> {

    @Inject
    private ClusterEventPublisher clusterEventPublisher;
    
    @Inject
    private ProviderRegistry providerRegistry;
    
    

    public Provider getProvider() {

        Provider provider = getEntityManager().createNamedQuery("Provider.first", Provider.class).getResultList().get(0);

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
            provider.getInvoiceConfiguration().getDisplayBillingCycle();
        }
        provider.getPaymentMethods().size();
        return provider;
    }

    @Override
    public void remove(Provider provider) throws BusinessException {
        super.remove(provider);
        providerRegistry.removeEntityManagerFactoryFromCache(provider);
    }
    
    @Override
    public Provider update(Provider provider) throws BusinessException {
        provider = super.update(provider);
        // Refresh appProvider application scope variable
        refreshAppProvider(provider);
        clusterEventPublisher.publishEvent(provider, CrudActionEnum.update);
        return provider;
    }

    /**
     * Refresh appProvider application scope variable
     * 
     * @param provider New provider data to refresh with
     */
    private void refreshAppProvider(Provider provider) {

        try {
            BeanUtils.copyProperties(appProvider, provider);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Failed to update alProvider fields");
        }

        appProvider.setCurrency(provider.getCurrency() != null ? provider.getCurrency() : null);
        appProvider.setCountry(provider.getCountry() != null ? provider.getCountry() : null);
        appProvider.setLanguage(provider.getLanguage() != null ? provider.getLanguage() : null);
        appProvider.setInvoiceConfiguration(provider.getInvoiceConfiguration() != null ? provider.getInvoiceConfiguration() : null);
        appProvider.setPaymentMethods(provider.getPaymentMethods());

    }

    /**
     * Refresh appProvider application scope variable with provider data from DB
     */
    public void refreshAppProvider() {
        refreshAppProvider(getProvider());
    }
    

}