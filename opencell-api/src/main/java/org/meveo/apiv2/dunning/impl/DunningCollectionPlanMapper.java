package org.meveo.apiv2.dunning.impl;

import static java.util.stream.Collectors.toList;

import org.meveo.apiv2.dunning.DunningCollectionPlan;
import org.meveo.apiv2.dunning.ImmutableDunningCollectionPlan;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.models.Resource;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.BaseEntity;

import java.util.List;

public class DunningCollectionPlanMapper
        extends ResourceMapper<DunningCollectionPlan, org.meveo.model.dunning.DunningCollectionPlan> {

    @Override
    protected DunningCollectionPlan toResource(org.meveo.model.dunning.DunningCollectionPlan entity) {
        
        return ImmutableDunningCollectionPlan.builder()
            .collectionPlanRelatedPolicy(createResource(entity.getCollectionPlanRelatedPolicy()))
            .initialCollectionPlan(createResource(entity.getInitialCollectionPlan()))
            .collectionPlanBillingAccount(createResource(entity.getCollectionPlanBillingAccount()))
            .collectionPlanRelatedInvoice(createResource(entity.getCollectionPlanRelatedInvoice()))
            .collectionPlanPauseReason(createResource(entity.getCollectionPlanPauseReason()))
            .collectionPlanStopReason(createResource(entity.getCollectionPlanStopReason()))
            .collectionPlanCurrentDunningLevelSequence(entity.getCollectionPlanCurrentDunningLevelSequence())
            .collectionPlanStartDate(entity.getCollectionPlanStartDate())
            .collectionPlanDaysOpen(entity.getCollectionPlanDaysOpen())
            .collectionPlanCloseDate(entity.getCollectionPlanCloseDate())
            .collectionPlanStatus(createResource(entity.getCollectionPlanStatus()))
            .collectionPlanPausedUntilDate(entity.getCollectionPlanPausedUntilDate())
            .collectionPlanBalance(entity.getCollectionPlanBalance())
            .retryPaymentOnResumeDate(entity.isRetryPaymentOnResumeDate())
            .dunningLevelInstances(entity.getDunningLevelInstances() != null 
                    ? entity.getDunningLevelInstances().stream().map(l -> createResource(l)).collect(toList())
                    : null)
            .collectionPlanNextAction(entity.getCollectionPlanNextAction())
            .collectionPlanNextActionDate(entity.getCollectionPlanNextActionDate())
            .collectionPlanLastAction(entity.getCollectionPlanLastAction())
            .collectionPlanLastActionDate(entity.getCollectionPlanLastActionDate())
            .totalDunningLevels(entity.getTotalDunningLevels())
            .build();
    }

    @Override
    protected org.meveo.model.dunning.DunningCollectionPlan toEntity(DunningCollectionPlan resource) {
        return null;
    }

    private Resource createResource(BaseEntity baseEntity) {
        return baseEntity != null ? ImmutableResource.builder().id(baseEntity.getId()).build() : null;
    }

    public List<org.meveo.model.dunning.DunningCollectionPlan> toEntities(List<Resource> collectionPlans) {
        return collectionPlans
                .stream()
                .map(dunningCollectionPlan ->
                        new org.meveo.model.dunning.DunningCollectionPlan(dunningCollectionPlan.getId()))
                .collect(toList());
    }
}