package org.meveo.apiv2.settings.globalSettings.impl;

import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.apiv2.settings.GlobalSettingsInput;
import org.meveo.apiv2.settings.ImmutableDunning;
import org.meveo.apiv2.settings.ImmutableGlobalSettingsInput;
import org.meveo.apiv2.settings.ImmutableQuoteSettings;
import org.meveo.model.settings.GlobalSettings;

public class GlobalSettingsMapper extends ResourceMapper<GlobalSettingsInput, GlobalSettings> {

    @Override
    protected GlobalSettingsInput toResource(GlobalSettings entity) {
        return ImmutableGlobalSettingsInput
                .builder()
                .id(entity.getId())
                .dunning(ImmutableDunning.builder().activateDunning(entity.getActivateDunning()).build())
                .quoteSettings(ImmutableQuoteSettings
                                .builder()
                                .quoteDefaultValidityDelay(entity.getQuoteDefaultValidityDelay())
                                .build())
                .build();
    }

    @Override
    protected GlobalSettings toEntity(GlobalSettingsInput resource) {
        return toEntity(new GlobalSettings(), resource);
    }

    protected GlobalSettings toEntity(GlobalSettings entity, GlobalSettingsInput resource) {
        entity.setQuoteDefaultValidityDelay(resource.getQuoteSettings().getQuoteDefaultValidityDelay());
        if(resource.getDunning() != null) {
            entity.setActivateDunning(resource.getDunning().getActivateDunning());
        }
        return entity;
    }
}
