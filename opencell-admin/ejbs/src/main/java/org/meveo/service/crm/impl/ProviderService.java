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

import javax.ejb.Stateless;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;
import org.meveo.util.ApplicationProvider;

/**
 * Provider service implementation.
 */
@Stateless
public class ProviderService extends PersistenceService<Provider> {

    /**
     * Expose application provider
     * 
     * @return
     */
    @Produces
    @ApplicationScoped
    @Named("appProvider")
    @ApplicationProvider
    public Provider getProvider() {
        Provider provider = list().get(0);
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

        detach(provider);
        return provider;
    }

    @Override
    public Provider update(Provider entity) throws BusinessException {
        entity = super.update(entity);

        // Refresh appProvider application scope variable
        Provider detachedProvider = getProvider();
        appProvider.setCurrency(detachedProvider.getCurrency() != null ? detachedProvider.getCurrency() : null);
        appProvider.setCountry(detachedProvider.getCountry() != null ? detachedProvider.getCountry() : null);
        appProvider.setLanguage(detachedProvider.getLanguage() != null ? detachedProvider.getLanguage() : null);
        appProvider.setInvoiceConfiguration(detachedProvider.getInvoiceConfiguration() != null ? detachedProvider.getInvoiceConfiguration() : null);

        return entity;
    }
}