package org.meveo.apiv2.dunning.impl;

import org.meveo.apiv2.dunning.DunningActionInstanceInput;
import org.meveo.apiv2.dunning.ImmutableDunningActionInstanceInput;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.models.Resource;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.BaseEntity;
import org.meveo.model.dunning.DunningActionInstance;

public class DunningActionInstanceMapper extends ResourceMapper<DunningActionInstanceInput, DunningActionInstance> {

    @Override
    protected DunningActionInstanceInput toResource(DunningActionInstance entity) {
        return ImmutableDunningActionInstanceInput.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .description(entity.getDescription())
                .actionType(entity.getActionType())
                .mode(entity.getActionMode())
                .actionOwner(createResource(entity.getActionOwner()))
                .dunningAction(createResource(entity.getDunningAction()))
                .actionRestult(entity.getActionRestult())
                .actionStatus(entity.getActionStatus())
                .collectionPlan(createResource(entity.getCollectionPlan()))
                .dunningLevelInstance(createResource(entity.getCollectionPlan()))
                .build();
        }

    private Resource createResource(BaseEntity baseEntity) {
        return baseEntity != null ? ImmutableResource.builder().id(baseEntity.getId()).build() : null;
    }

    @Override
    protected DunningActionInstance toEntity(DunningActionInstanceInput resource) {
        return null;
    }
}