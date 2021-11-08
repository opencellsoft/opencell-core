package org.meveo.apiv2.dunning.impl;

import static java.util.Optional.ofNullable;
import static org.meveo.apiv2.models.ImmutableResource.builder;

import org.meveo.apiv2.dunning.DunningCollectionPlan;
import org.meveo.apiv2.dunning.ImmutableDunningCollectionPlan;
import org.meveo.apiv2.models.Resource;
import org.meveo.apiv2.ordering.ResourceMapper;

public class DunningCollectionPlanMapper
        extends ResourceMapper<DunningCollectionPlan, org.meveo.model.dunning.DunningCollectionPlan> {

    @Override
    protected DunningCollectionPlan toResource(org.meveo.model.dunning.DunningCollectionPlan entity) {
        Resource dunningLevel = ofNullable(entity.getCollectionPlanCurrentDunningLevel())
                .map(dl -> builder().id(dl.getId()).build())
                .orElse(null);
        Resource collectionPlanStatus = ofNullable(entity.getCollectionPlanStatus())
                .map(cps -> builder().id(cps.getId()).build())
                .orElse(null);
        Resource billingAccount = ofNullable(entity.getCollectionPlanBillingAccount())
                .map(ba -> builder().id(ba.getId()).build())
                .orElse(null);
        Resource paymentMethod = ofNullable(entity.getCollectionPlanPaymentMethod())
                .map(pm -> builder().id(pm.getId()).build())
                .orElse(null);
        return ImmutableDunningCollectionPlan.builder()
                .collectionPlanBillingAccount(billingAccount)
                .collectionPlanPaymentMethod(paymentMethod)
                .startDate(entity.getCollectionPlanStartDate())
                .collectionPlanSequence(entity.getCollectionPlanCurrentDunningLevelSequence())
                .policyLevel(dunningLevel)
                .collectionPlanStatus(collectionPlanStatus)
                .collectionPlanDaysOpen(entity.getCollectionPlanDaysOpen())
                .collectionPlanPausedUntilDate(entity.getCollectionPlanPausedUntilDate())
                .collectionPlanLastUpdate(entity.getCollectionPlanLastUpdate())
                .collectionPlanTotalBalance(entity.getCollectionPlanTotalBalance())
                .collectionPlanAgedBalance(entity.getCollectionPlanAgedBalance())
                .collectionPlanDueBalance(entity.getCollectionPlanDueBalance())
                .collectionPlanDisputedBalance(entity.getCollectionPlanDisputedBalance())
                .retryPaymentOnResumeDate(entity.isRetryPaymentOnResumeDate())
                .collectionPlanSequence(entity.getCollectionPlanCurrentDunningLevelSequence())
                .build();
    }

    @Override
    protected org.meveo.model.dunning.DunningCollectionPlan toEntity(DunningCollectionPlan resource) {
        return null;
    }
}