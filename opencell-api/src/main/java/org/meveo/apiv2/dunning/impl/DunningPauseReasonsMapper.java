package org.meveo.apiv2.dunning.impl;


import org.meveo.apiv2.dunning.ImmutableDunningPauseReason;
import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.model.AuditableEntity;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.dunning.DunningPauseReason;
import org.meveo.model.dunning.DunningSettings;

public class DunningPauseReasonsMapper extends ResourceMapper<org.meveo.apiv2.dunning.DunningPauseReason, DunningPauseReason> {

    @Override
    protected org.meveo.apiv2.dunning.DunningPauseReason toResource(DunningPauseReason entity) {
        return ImmutableDunningPauseReason.builder().id(entity.getId()).language(createResource(entity.getLanguage())).pauseReason(entity.getPauseReason())
                .description(entity.getDescription()).dunningSettings(createResource((AuditableEntity) entity.getDunningSettings())).build();
    }

    @Override
    protected DunningPauseReason toEntity(org.meveo.apiv2.dunning.DunningPauseReason resource) {
        var entity = new DunningPauseReason();
        entity.setId(resource.getId());
        var dunningSettings = new DunningSettings();
        dunningSettings.setId(resource.getDunningSettings().getId());
        dunningSettings.setCode(resource.getDunningSettings().getCode());
        entity.setDunningSettings(dunningSettings);
        resource.getLanguage();
        var tradingLanguage = new TradingLanguage();
        tradingLanguage.setId(resource.getLanguage().getId());
        entity.setLanguage(tradingLanguage);
        entity.setDescription(resource.getDescription());
        entity.setPauseReason(resource.getPauseReason());
        return entity;
    }
}
