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

import javax.ejb.Stateless;

import org.apache.commons.beanutils.BeanUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;

/**
 * Provider service implementation.
 */
@Stateless
public class ProviderService extends PersistenceService<Provider> {

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

        return provider;
    }

    @Override
    public Provider update(Provider entity) throws BusinessException {
        entity = super.update(entity);

        // Refresh appProvider application scope variable
        try {
            BeanUtils.copyProperties(appProvider, entity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Failed to update alProvider fields");
        }

        appProvider.setCurrency(entity.getCurrency() != null ? entity.getCurrency() : null);
        appProvider.setCountry(entity.getCountry() != null ? entity.getCountry() : null);
        appProvider.setLanguage(entity.getLanguage() != null ? entity.getLanguage() : null);
        appProvider.setInvoiceConfiguration(entity.getInvoiceConfiguration() != null ? entity.getInvoiceConfiguration() : null);

        return entity;
    }
}