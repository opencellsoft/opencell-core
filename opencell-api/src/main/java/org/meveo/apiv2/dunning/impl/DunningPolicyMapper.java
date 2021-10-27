package org.meveo.apiv2.dunning.impl;

import static java.util.Optional.ofNullable;

import org.meveo.apiv2.dunning.DunningPolicy;
import org.meveo.apiv2.dunning.ImmutableDunningPolicy;
import org.meveo.apiv2.ordering.ResourceMapper;

import java.util.stream.Collectors;

public class DunningPolicyMapper extends ResourceMapper<DunningPolicy, org.meveo.model.dunning.DunningPolicy> {

    private final DunningPolicyLevelMapper policyLevelMapper = new DunningPolicyLevelMapper();
    @Override
    protected DunningPolicy toResource(org.meveo.model.dunning.DunningPolicy entity) {
        return ImmutableDunningPolicy.builder()
                .id(entity.getId())
                .isDefaultPolicy(entity.getDefaultPolicy())
                .isActivePolicy(entity.getActivePolicy())
                .policyName(entity.getPolicyName())
                .policyDescription(entity.getPolicyDescription())
                .minBalanceTrigger(entity.getMinBalanceTrigger())
                .policyPriority(entity.getPolicyPriority())
                .interestForDelaySequence(entity.getInterestForDelaySequence())
                .isIncludeDueInvoicesInThreshold(entity.getIncludeDueInvoicesInThreshold())
                .isAttachInvoicesToEmails(entity.getAttachInvoicesToEmails())
                .isIncludePayReminder(entity.getIncludePayReminder())
                .determineLevelBy(entity.getDetermineLevelBy())
                .minBalanceTriggerCurrency(entity.getDetermineLevelBy())
                .dunningLevels(entity.getDunningLevels().stream()
                        .map(dunningPolicyLevel ->  policyLevelMapper.toResource(dunningPolicyLevel))
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    protected org.meveo.model.dunning.DunningPolicy toEntity(DunningPolicy resource) {
        org.meveo.model.dunning.DunningPolicy entity = new org.meveo.model.dunning.DunningPolicy();
        entity.setPolicyName(resource.getPolicyName());
        entity.setPolicyDescription(resource.getPolicyDescription());
        entity.setInterestForDelaySequence(resource.getInterestForDelaySequence());
        entity.setMinBalanceTrigger(resource.getMinBalanceTrigger());
        entity.setMinBalanceTriggerCurrency(resource.getMinBalanceTriggerCurrency());
        entity.setDetermineLevelBy(resource.getDetermineLevelBy());
        entity.setIncludeDueInvoicesInThreshold(resource.isIncludeDueInvoicesInThreshold());
        entity.setIncludePayReminder(resource.isIncludePayReminder());
        ofNullable(resource.isAttachInvoicesToEmails())
                .ifPresent(attachInvoicesToEmail -> entity.setAttachInvoicesToEmails(attachInvoicesToEmail));
        entity.setPolicyPriority(resource.getPolicyPriority());
        entity.setDefaultPolicy(resource.isDefaultPolicy());
        entity.setActivePolicy(resource.isActivePolicy());
        return entity;
    }

    public org.meveo.model.dunning.DunningPolicy toUpdateEntity(DunningPolicy resource,
                                                                org.meveo.model.dunning.DunningPolicy toUpdate) {
        ofNullable(resource.getPolicyName()).ifPresent(policyName -> toUpdate.setPolicyName(policyName));
        ofNullable(resource.getPolicyDescription()).ifPresent(description -> toUpdate.setPolicyDescription(description));
        ofNullable(resource.isActivePolicy()).ifPresent(activePolicy -> toUpdate.setActivePolicy(activePolicy));
        ofNullable(resource.isDefaultPolicy()).ifPresent(defaultPolicy -> toUpdate.setDefaultPolicy(defaultPolicy));
        ofNullable(resource.isAttachInvoicesToEmails())
                .ifPresent(attachInvoicesToEmails -> toUpdate.setAttachInvoicesToEmails(attachInvoicesToEmails));
        ofNullable(resource.isIncludeDueInvoicesInThreshold())
                .ifPresent(includeDueInvoices -> toUpdate.setIncludeDueInvoicesInThreshold(includeDueInvoices));
        ofNullable(resource.isIncludePayReminder())
                .ifPresent(includePayReminder -> toUpdate.setIncludePayReminder(includePayReminder));
        ofNullable(resource.getInterestForDelaySequence())
                .ifPresent(interestForDelaySequence -> toUpdate.setInterestForDelaySequence(interestForDelaySequence));
        ofNullable(resource.getMinBalanceTrigger()).ifPresent(minBalance -> toUpdate.setMinBalanceTrigger(minBalance));
        ofNullable(resource.getDetermineLevelBy()).ifPresent(determineLevelBy -> toUpdate.setDetermineLevelBy(determineLevelBy));
        ofNullable(resource.getMinBalanceTriggerCurrency())
                .ifPresent(minBalanceTrigger -> toUpdate.setMinBalanceTriggerCurrency(minBalanceTrigger));
        return toUpdate;
    }
}