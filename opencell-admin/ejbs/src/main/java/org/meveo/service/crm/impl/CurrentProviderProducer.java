package org.meveo.service.crm.impl;

import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.model.crm.Provider;
import org.meveo.util.ApplicationProvider;
import org.meveo.util.PersistenceUtils;

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

        provider = PersistenceUtils.initializeAndUnproxy(provider);

        providerService.detach(provider);
        return provider;
    }
}