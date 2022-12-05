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

package org.meveo.service.crm.impl;

import jakarta.ejb.Stateless;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.meveo.model.crm.Provider;
import org.meveo.util.ApplicationProvider;

@Stateless
public class CurrentProviderProducer {

    @Inject
    private ProviderService providerService;

    /**
     * Expose application provider.
     * 
     * @return provider.
     */
    @Produces
    @RequestScoped
    @Named("appProvider")
    @ApplicationProvider
    public Provider getProvider() {

        // Provider provider = providerService.list().get(0);
        //
        // if (provider.getCurrency() != null) {
        // provider.getCurrency().getCurrencyCode();
        // }
        // if (provider.getCountry() != null) {
        // provider.getCountry().getCountryCode();
        // }
        // if (provider.getLanguage() != null) {
        // provider.getLanguage().getLanguageCode();
        // }
        // if (provider.getInvoiceConfiguration() != null) {
        // provider.getInvoiceConfiguration().getDisplayBillingCycle();
        // }
        Provider provider = providerService.getProvider();

        // provider = PersistenceUtils.initializeAndUnproxy(provider);
        //
        // providerService.detach(provider);

        return provider;
    }
}