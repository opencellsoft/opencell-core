package org.meveo.apiv2.dunning.impl;

import org.meveo.apiv2.dunning.ImmutableDunningCollectionPlanStatus;
import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.model.AuditableEntity;
import org.meveo.model.dunning.DunningCollectionPlanStatus;
import org.meveo.model.dunning.DunningSettings;

public class DunningCollectionPlanStatusMapper extends ResourceMapper<org.meveo.apiv2.dunning.DunningCollectionPlanStatus, DunningCollectionPlanStatus> {

    @Override
    protected org.meveo.apiv2.dunning.DunningCollectionPlanStatus toResource(DunningCollectionPlanStatus entity) {
        return ImmutableDunningCollectionPlanStatus
                .builder()
                .id(entity.getId())
                .dunningSettings(createResource((AuditableEntity) entity.getDunningSettings()))
                .description(entity.getDescription())
                .status(entity.getStatus())
                .colorCode(entity.getColorCode())
                .build();

    }

    @Override
    protected DunningCollectionPlanStatus toEntity(org.meveo.apiv2.dunning.DunningCollectionPlanStatus resource) {
        var dunningSetting = new DunningSettings();
        dunningSetting.setId(resource.getDunningSettings().getId());
        dunningSetting.setCode(resource.getDunningSettings().getCode());
         
        var entity = new DunningCollectionPlanStatus();
        entity.setId(resource.getId());
        entity.setStatus(resource.getStatus());
        entity.setDescription(resource.getDescription());
        entity.setDunningSettings(dunningSetting);
        entity.setColorCode(resource.getColorCode());
        return entity;
    }

}
