package org.meveo.apiv2.dunning.service;

import org.meveo.api.exception.BusinessApiException;
import org.meveo.model.securityDeposit.FinanceSettings;
import org.meveo.service.securityDeposit.impl.FinanceSettingsService;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class GlobalSettingsVerifier {

    @Inject
    private FinanceSettingsService financeSettingsService;

    /**
     * Check if dunning is enabled
     *
     * @throws BusinessApiException in case dunning is disabled
     */
    public void checkActivateDunning() {
        FinanceSettings settings = financeSettingsService.findLastOne();
        if(settings != null && !settings.isActivateDunning()) {
            throw new BusinessApiException("The action is not possible, FinanceSettings.activateDunning is disabled");
        }
    }
}