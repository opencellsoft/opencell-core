package org.meveo.apiv2.dunning.impl;


import org.meveo.apiv2.dunning.ImmutableDunningStopReason;
import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.model.AuditableEntity;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.dunning.DunningSettings;
import org.meveo.model.dunning.DunningStopReason;

public class DunningStopReasonsMapper extends ResourceMapper<org.meveo.apiv2.dunning.DunningStopReason, DunningStopReason> {

    @Override
    protected org.meveo.apiv2.dunning.DunningStopReason toResource(DunningStopReason entity) {
        return ImmutableDunningStopReason.builder().id(entity.getId()).stopReason(entity.getStopReason()).description(entity.getDescription())
                .dunningSettings(createResource((AuditableEntity) entity.getDunningSettings())).build();
    }

    @Override
    protected DunningStopReason toEntity(org.meveo.apiv2.dunning.DunningStopReason resource) {
        var entity = new DunningStopReason();
        entity.setId(resource.getId());
        resource.getDunningSettings();
        var dunningSettings = new DunningSettings();
        dunningSettings.setId(resource.getDunningSettings().getId());
        dunningSettings.setCode(resource.getDunningSettings().getCode());
        entity.setDunningSettings(dunningSettings);
        entity.setDescription(resource.getDescription());
        entity.setStopReason(resource.getStopReason());
        return entity;
    }
}
