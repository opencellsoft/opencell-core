package org.meveo.apiv2.dunning.impl;

import org.meveo.apiv2.dunning.DunningCollectionPlan;
import org.meveo.apiv2.dunning.ImmutableDunningCollectionPlan;
import org.meveo.apiv2.models.Resource;
import org.meveo.apiv2.ordering.ResourceMapper;

import static java.util.Optional.ofNullable;
import static org.meveo.apiv2.models.ImmutableResource.builder;

public class DunningCollectionPlanMapper
        extends ResourceMapper<DunningCollectionPlan, org.meveo.model.dunning.DunningCollectionPlan> {

    @Override
    protected DunningCollectionPlan toResource(org.meveo.model.dunning.DunningCollectionPlan entity) {
        Resource collectionPlanStatus = ofNullable(entity.getCollectionPlanStatus())
                .map(cps -> builder().id(cps.getId()).build())
                .orElse(null);
        Resource billingAccount = ofNullable(entity.getCollectionPlanBillingAccount())
                .map(ba -> builder().id(ba.getId()).build())
                .orElse(null);
        return ImmutableDunningCollectionPlan.builder()
                .collectionPlanBillingAccount(billingAccount)
                .startDate(entity.getCollectionPlanStartDate())
                .collectionPlanSequence(entity.getCollectionPlanCurrentDunningLevelSequence())
                .collectionPlanStatus(collectionPlanStatus)
                .collectionPlanDaysOpen(entity.getCollectionPlanDaysOpen())
                .collectionPlanPausedUntilDate(entity.getCollectionPlanPausedUntilDate())
                .collectionPlanLastUpdate(entity.getCollectionPlanLastActionDate())
                .collectionPlanTotalBalance(entity.getCollectionPlanBalance())
                .retryPaymentOnResumeDate(entity.isRetryPaymentOnResumeDate())
                .collectionPlanSequence(entity.getCollectionPlanCurrentDunningLevelSequence())
                .build();
    }

    @Override
    protected org.meveo.model.dunning.DunningCollectionPlan toEntity(DunningCollectionPlan resource) {
        return null;
    }
}