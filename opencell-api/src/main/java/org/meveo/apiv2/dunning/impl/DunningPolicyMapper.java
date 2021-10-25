package org.meveo.apiv2.dunning.impl;

import org.meveo.apiv2.dunning.DunningPolicy;
import org.meveo.apiv2.ordering.ResourceMapper;

import java.util.Optional;
import java.util.stream.Collectors;

public class DunningPolicyMapper extends ResourceMapper<DunningPolicy, org.meveo.model.dunning.DunningPolicy> {

    private DunningPolicyLevelMapper dunningPolicyLevelMapper = new DunningPolicyLevelMapper();

    @Override
    protected DunningPolicy toResource(org.meveo.model.dunning.DunningPolicy entity) {
        return null;
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
        Optional.ofNullable(resource.isAttachInvoicesToEmails())
                .ifPresent(attachInvoicesToEmail -> entity.setAttachInvoicesToEmails(attachInvoicesToEmail));
        entity.setPolicyPriority(resource.getPolicyPriority());
        entity.setDefaultPolicy(resource.isDefaultPolicy());
        entity.setActivePolicy(resource.isActivePolicy());
        if(entity.getDunningLevels() != null) {
            entity.setDunningLevels(resource.getDunningLevels()
                    .stream().map(dunningPolicyLevelMapper::toEntity)
                    .collect(Collectors.toList()));
        }
        return entity;
    }
}