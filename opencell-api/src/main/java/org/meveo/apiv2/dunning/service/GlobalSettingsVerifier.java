package org.meveo.apiv2.dunning.service;

import org.meveo.api.exception.BusinessApiException;
import org.meveo.model.settings.GlobalSettings;
import org.meveo.service.settings.impl.GlobalSettingsService;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class GlobalSettingsVerifier {

    @Inject
    private GlobalSettingsService globalSettingsService;

    public void checkActivateDunning() {
        GlobalSettings lastOne = globalSettingsService.findLastOne();
        if(lastOne != null && !lastOne.getActivateDunning()) {
            throw new BusinessApiException("The action is not possible, GlobalSettings.activateDunning is disabled");
        }
    }
}
