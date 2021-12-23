package org.meveo.apiv2.dunning.impl;

import static java.util.stream.Collectors.toList;
import static org.meveo.apiv2.models.ImmutableResource.builder;

import org.meveo.apiv2.dunning.*;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.models.Resource;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.BaseEntity;
import org.meveo.model.dunning.DunningPolicy;

import java.util.List;

public class DunningCollectionPlanMapper
        extends ResourceMapper<DunningCollectionPlan, org.meveo.model.dunning.DunningCollectionPlan> {

    @Override
    protected DunningCollectionPlan toResource(org.meveo.model.dunning.DunningCollectionPlan entity) {
        
        return ImmutableDunningCollectionPlan.builder()
            .relatedPolicy(createResource(entity.getRelatedPolicy()))
            .initialCollectionPlan(createResource(entity.getInitialCollectionPlan()))
            .billingAccount(createResource(entity.getBillingAccount()))
            .relatedInvoice(createResource(entity.getRelatedInvoice()))
            .pauseReason(createResource(entity.getPauseReason()))
            .stopReason(createResource(entity.getStopReason()))
            .currentDunningLevelSequence(entity.getCurrentDunningLevelSequence())
            .startDate(entity.getStartDate())
            .daysOpen(entity.getDaysOpen())
            .closeDate(entity.getCloseDate())
            .status(createResource(entity.getStatus()))
            .pausedUntilDate(entity.getPausedUntilDate())
            .balance(entity.getBalance())
            .retryPaymentOnResumeDate(entity.isRetryPaymentOnResumeDate())
            .dunningLevelInstances(entity.getDunningLevelInstances() != null 
                    ? entity.getDunningLevelInstances().stream().map(l -> createResource(l)).collect(toList())
                    : null)
            .nextAction(entity.getNextAction())
            .nextActionDate(entity.getNextActionDate())
            .lastAction(entity.getLastAction())
            .lastActionDate(entity.getLastActionDate())
            .totalDunningLevels(entity.getTotalDunningLevels())
            .collectionPlanNumber(entity.getCollectionPlanNumber())
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

    public AvailablePoliciesList toAvailablePolicies(List<DunningPolicy> availablePolicies) {
        List<org.meveo.apiv2.dunning.DunningPolicy> dunningPolicies = availablePolicies.stream()
                .map(this::toPolicy)
                .collect(toList());
        AvailablePoliciesList availablePoliciesResponse = ImmutableAvailablePoliciesList.builder()
                .total(availablePolicies.size())
                .availablePolicies(dunningPolicies)
                .build();
        return availablePoliciesResponse;
    }

    private org.meveo.apiv2.dunning.DunningPolicy toPolicy(DunningPolicy policy) {
        return ImmutableDunningPolicy.builder()
                    .id(policy.getId())
                    .isDefaultPolicy(policy.getDefaultPolicy())
                    .isActivePolicy(policy.getActivePolicy())
                    .policyName(policy.getPolicyName())
                    .minBalanceTriggerCurrency(builder().id(policy.getMinBalanceTriggerCurrency().getId()).build())
                    .policyDescription(policy.getPolicyDescription())
                    .minBalanceTrigger(policy.getMinBalanceTrigger())
                    .policyPriority(policy.getPolicyPriority())
                    .interestForDelaySequence(policy.getInterestForDelaySequence())
                    .isIncludeDueInvoicesInThreshold(policy.getIncludeDueInvoicesInThreshold())
                    .isAttachInvoicesToEmails(policy.getAttachInvoicesToEmails())
                    .isIncludePayReminder(policy.getIncludePayReminder())
                    .determineLevelBy(policy.getDetermineLevelBy())
                .build();
    }
}