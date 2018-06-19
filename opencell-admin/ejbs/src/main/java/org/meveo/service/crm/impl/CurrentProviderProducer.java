package org.meveo.service.crm.impl;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.model.crm.Provider;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.model.persistence.JacksonUtil;
import org.meveo.util.ApplicationProvider;
import org.meveo.util.PersistenceUtils;

import com.fasterxml.jackson.core.type.TypeReference;

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

        CustomFieldValues cfv = provider.getCfValues();

        Map<String, List<CustomFieldValue>> cfValues = null;
        if (cfv != null) {
            String json = cfv.asJson();
            cfValues = JacksonUtil.fromString(json, new TypeReference<Map<String, List<CustomFieldValue>>>() {
            });
        }

        provider = PersistenceUtils.initializeAndUnproxy(provider);

        providerService.detach(provider);

        if (cfValues != null) {
            provider.setCfValues(new CustomFieldValues(cfValues));
            provider.setCfAccumulatedValues(new CustomFieldValues(cfValues));
        }

        return provider;
    }
}