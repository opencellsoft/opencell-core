package org.meveo.apiv2.dunning.impl;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.meveo.apiv2.dunning.DunningActionInstanceInput;
import org.meveo.apiv2.dunning.DunningLevelInstanceInput;
import org.meveo.apiv2.dunning.ImmutableDunningActionInstanceInput;
import org.meveo.apiv2.dunning.ImmutableDunningLevelInstanceInput;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.models.Resource;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.BaseEntity;
import org.meveo.model.dunning.DunningActionInstance;
import org.meveo.model.dunning.DunningLevelInstance;

public class DunningLevelInstanceMapper extends ResourceMapper<DunningLevelInstanceInput, DunningLevelInstance> {

    @Override
    protected DunningLevelInstanceInput toResource(DunningLevelInstance entity) {
        return ImmutableDunningLevelInstanceInput
                .builder()
                .id(entity.getId())
                .sequence(entity.getSequence())
                .daysOverdue(entity.getDaysOverdue())
                .levelStatus(entity.getLevelStatus())
                .collectionPlan(createResource(entity.getCollectionPlan()))
                .dunningLevel(createResource(entity.getDunningLevel()))
                .policyLevel(createResource(entity.getPolicyLevel()))
                .collectionPlanStatus(createResource(entity.getCollectionPlanStatus()))
                .actions(toActions(entity.getActions()))
                .build();
    }

    private Resource createResource(BaseEntity baseEntity) {
        return baseEntity != null ? ImmutableResource.builder().id(baseEntity.getId()).build() : null;
    }

    private List<DunningActionInstanceInput> toActions(List<DunningActionInstance> actions) {
        if (actions == null) {
            return null;
        }
        return actions.stream()
        .map(this::toAction).collect(toList());
    }

    private DunningActionInstanceInput toAction(DunningActionInstance action) {
        return ImmutableDunningActionInstanceInput.builder()
            .id(action.getId())
            .code(action.getCode())
            .description(action.getDescription())
            .actionType(action.getActionType())
            .mode(action.getActionMode())
            .actionOwner(createResource(action.getActionOwner()))
            .dunningAction(createResource(action.getDunningAction()))
            .actionRestult(action.getActionRestult())
            .actionStatus(action.getActionStatus())
            .collectionPlan(createResource(action.getCollectionPlan()))
            .dunningLevelInstance(createResource(action.getCollectionPlan()))
            .build();
    }

    @Override
    protected DunningLevelInstance toEntity(DunningLevelInstanceInput resource) {
        // TODO Auto-generated method stub
        return null;
    }
}