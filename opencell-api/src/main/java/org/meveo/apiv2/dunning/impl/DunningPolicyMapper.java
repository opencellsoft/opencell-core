package org.meveo.apiv2.dunning.impl;

import static java.util.Optional.ofNullable;

import org.meveo.apiv2.dunning.DunningPolicy;
import org.meveo.apiv2.dunning.DunningPolicyInput;
import org.meveo.apiv2.dunning.ImmutableDunningPolicy;
import org.meveo.apiv2.ordering.ResourceMapper;

import java.util.stream.Collectors;

public class DunningPolicyMapper extends ResourceMapper<DunningPolicy, org.meveo.model.dunning.DunningPolicy> {

    private final DunningPolicyLevelMapper policyLevelMapper = new DunningPolicyLevelMapper();
    @Override
    protected DunningPolicy toResource(org.meveo.model.dunning.DunningPolicy entity) {
        ImmutableDunningPolicy.Builder builder = ImmutableDunningPolicy.builder()
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
                .minBalanceTriggerCurrency(entity.getDetermineLevelBy());
        if (entity.getDunningLevels() != null) {
            builder.dunningPolicyLevels(entity.getDunningLevels().stream()
                    .map(dunningPolicyLevel ->  policyLevelMapper.toResource(dunningPolicyLevel))
                    .collect(Collectors.toList()));
        }
        return builder.build();
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

    public org.meveo.model.dunning.DunningPolicy toUpdateEntity(DunningPolicyInput resource,
                                                                org.meveo.model.dunning.DunningPolicy toUpdate, StringBuilder fieldToUpdate) {
        ofNullable(resource.getPolicyName()).ifPresent(policyName -> {
            toUpdate.setPolicyName(policyName);
            fieldToUpdate.append("policyName;"); });
        ofNullable(resource.getPolicyDescription()).ifPresent(description
                -> { toUpdate.setPolicyDescription(description); fieldToUpdate.append("policyDescription;"); });
        ofNullable(resource.isActivePolicy()).ifPresent(activePolicy
                -> { toUpdate.setActivePolicy(activePolicy); fieldToUpdate.append("activePolicy;"); });
        ofNullable(resource.isDefaultPolicy()).ifPresent(defaultPolicy -> {
            toUpdate.setDefaultPolicy(defaultPolicy); fieldToUpdate.append("defaultPolicy;");});
        ofNullable(resource.isAttachInvoicesToEmails())
                .ifPresent(attachInvoicesToEmails -> {
                    toUpdate.setAttachInvoicesToEmails(attachInvoicesToEmails); fieldToUpdate.append("attachInvoicesToEmails;");});
        ofNullable(resource.isIncludeDueInvoicesInThreshold())
                .ifPresent(includeDueInvoices -> {
                    toUpdate.setIncludeDueInvoicesInThreshold(includeDueInvoices); fieldToUpdate.append("includeDueInvoicesInThreshold;"); });
        ofNullable(resource.isIncludePayReminder())
                .ifPresent(includePayReminder -> {
                    toUpdate.setIncludePayReminder(includePayReminder); fieldToUpdate.append("includePayReminder;"); });
        ofNullable(resource.getInterestForDelaySequence())
                .ifPresent(interestForDelaySequence -> {
                    toUpdate.setInterestForDelaySequence(interestForDelaySequence); fieldToUpdate.append("interestForDelaySequence;"); });
        ofNullable(resource.getMinBalanceTrigger()).ifPresent(minBalance -> {
            toUpdate.setMinBalanceTrigger(minBalance); fieldToUpdate.append("minBalanceTrigger;"); });
        ofNullable(resource.getDetermineLevelBy()).ifPresent(determineLevelBy -> {
            toUpdate.setDetermineLevelBy(determineLevelBy); fieldToUpdate.append("determineLevelBy;"); });
        ofNullable(resource.getMinBalanceTriggerCurrency())
                .ifPresent(minBalanceTrigger -> {
                    toUpdate.setMinBalanceTriggerCurrency(minBalanceTrigger); fieldToUpdate.append("minBalanceTriggerCurrency;"); });
        return toUpdate;
    }
}