package org.meveo.apiv2.dunning.impl;

import org.meveo.apiv2.dunning.DunningPolicyLevel;
import org.meveo.apiv2.dunning.ImmutableDunningPolicyLevel;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.dunning.DunningCollectionPlanStatus;
import org.meveo.model.dunning.DunningLevel;

public class DunningPolicyLevelMapper extends ResourceMapper<DunningPolicyLevel, org.meveo.model.dunning.DunningPolicyLevel> {

    @Override
    protected DunningPolicyLevel toResource(org.meveo.model.dunning.DunningPolicyLevel entity) {
        return ImmutableDunningPolicyLevel.builder()
                .dunningLevelId(entity.getDunningLevel().getId())
                .collectionPlanStatusId(entity.getCollectionPlanStatus().getId())
                .sequence(entity.getSequence())
                .build();
    }

    @Override
    protected org.meveo.model.dunning.DunningPolicyLevel toEntity(DunningPolicyLevel resource) {
        org.meveo.model.dunning.DunningPolicyLevel entity = new org.meveo.model.dunning.DunningPolicyLevel();
        entity.setSequence(resource.getSequence());
        DunningLevel dunningLevel = new DunningLevel();
        dunningLevel.setId(resource.getDunningLevelId());
        DunningCollectionPlanStatus collectionPlanStatus = new DunningCollectionPlanStatus();
        collectionPlanStatus.setId(resource.getCollectionPlanStatusId());
        entity.setCollectionPlanStatus(collectionPlanStatus);
        entity.setDunningLevel(dunningLevel);
        return entity;
    }
}