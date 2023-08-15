package org.meveo.apiv2.dunning.impl;

import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.meveo.apiv2.dunning.DunningPolicy;
import org.meveo.apiv2.dunning.DunningPolicyInput;
import org.meveo.apiv2.dunning.ImmutableDunningPolicy;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.admin.Currency;

public class DunningPolicyMapper extends ResourceMapper<DunningPolicy, org.meveo.model.dunning.DunningPolicy> {

    private final DunningPolicyLevelMapper policyLevelMapper = new DunningPolicyLevelMapper();

    @Override
    protected DunningPolicy toResource(org.meveo.model.dunning.DunningPolicy entity) {
        ImmutableDunningPolicy.Builder builder = ImmutableDunningPolicy.builder().id(entity.getId()).isDefaultPolicy(entity.getIsDefaultPolicy())
            .isActivePolicy(entity.getIsActivePolicy()).policyName(entity.getPolicyName()).policyDescription(entity.getPolicyDescription())
            .minBalanceTrigger(entity.getMinBalanceTrigger()).policyPriority(entity.getPolicyPriority()).interestForDelaySequence(entity.getInterestForDelaySequence())
            .isIncludeDueInvoicesInThreshold(entity.getIncludeDueInvoicesInThreshold()).isAttachInvoicesToEmails(entity.getAttachInvoicesToEmails())
            .isIncludePayReminder(entity.getIncludePayReminder()).determineLevelBy(entity.getDetermineLevelBy());
        if (entity.getDunningLevels() != null && Hibernate.isInitialized(entity.getDunningLevels())) {
            builder
                .dunningPolicyLevels(entity.getDunningLevels().stream().map(dunningPolicyLevel -> policyLevelMapper.toResource(dunningPolicyLevel)).collect(Collectors.toList()));
        }
        if(entity.getMinBalanceTriggerCurrency() != null) {
            builder.minBalanceTriggerCurrency(ImmutableResource.builder().id(entity.getMinBalanceTriggerCurrency().getId()).build());
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
        entity.setDetermineLevelBy(resource.getDetermineLevelBy());
        entity.setIncludeDueInvoicesInThreshold(resource.isIncludeDueInvoicesInThreshold());
        entity.setIncludePayReminder(resource.isIncludePayReminder());
        ofNullable(resource.isAttachInvoicesToEmails()).ifPresent(attachInvoicesToEmail -> entity.setAttachInvoicesToEmails(attachInvoicesToEmail));
        entity.setPolicyPriority(resource.getPolicyPriority());
        entity.setIsDefaultPolicy(resource.isDefaultPolicy());
        entity.setIsActivePolicy(resource.isActivePolicy());
        if(resource.getMinBalanceTriggerCurrency() != null && resource.getMinBalanceTriggerCurrency().getCode() != null) {
            Currency currency = new Currency();
            currency.setCurrencyCode(resource.getMinBalanceTriggerCurrency().getCode());
            entity.setMinBalanceTriggerCurrency(currency);
        }
        return entity;
    }

    public org.meveo.model.dunning.DunningPolicy toUpdateEntity(DunningPolicyInput resource,
                                                                org.meveo.model.dunning.DunningPolicy toUpdate, List<String> updatedFields) {
        ofNullable(resource.getPolicyName()).ifPresent(policyName -> {
            if (!resource.getPolicyName().equals(toUpdate.getPolicyName())) {
                updatedFields.add("policyName");
            }
            toUpdate.setPolicyName(policyName);
        });
        ofNullable(resource.getPolicyDescription()).ifPresent(description -> {
            if (!resource.getPolicyDescription().equals(toUpdate.getPolicyDescription())) {
                updatedFields.add("policyDescription");
            }
            toUpdate.setPolicyDescription(description);
        });
        ofNullable(resource.isActivePolicy()).ifPresent(activePolicy -> {
            if (!resource.isActivePolicy().equals(toUpdate.getIsActivePolicy())) {
                updatedFields.add("activePolicy");
            }
            toUpdate.setIsActivePolicy(activePolicy);
        });
        ofNullable(resource.isDefaultPolicy()).ifPresent(defaultPolicy -> {
            if (!resource.isDefaultPolicy().equals(toUpdate.getIsDefaultPolicy())) {
                updatedFields.add("defaultPolicy");
            }
            toUpdate.setIsDefaultPolicy(defaultPolicy);
        });
        ofNullable(resource.isAttachInvoicesToEmails()).ifPresent(attachInvoicesToEmails -> {
            if (!resource.isAttachInvoicesToEmails().equals(toUpdate.getAttachInvoicesToEmails())) {
                updatedFields.add("attachInvoicesToEmails");
            }
            toUpdate.setAttachInvoicesToEmails(attachInvoicesToEmails);
        });
        ofNullable(resource.isIncludeDueInvoicesInThreshold()).ifPresent(includeDueInvoices -> {
            if (!resource.isIncludeDueInvoicesInThreshold().equals(toUpdate.getIncludeDueInvoicesInThreshold())) {
                updatedFields.add("includeDueInvoicesInThreshold");
            }
            toUpdate.setIncludeDueInvoicesInThreshold(includeDueInvoices);
        });
        ofNullable(resource.isIncludePayReminder()).ifPresent(includePayReminder -> {
            if (!resource.isIncludePayReminder().equals(toUpdate.getIncludePayReminder())) {
                updatedFields.add("includePayReminder");
            }
            toUpdate.setIncludePayReminder(includePayReminder);
        });
        ofNullable(resource.getInterestForDelaySequence()).ifPresent(interestForDelaySequence -> {
            if (resource.getInterestForDelaySequence().equals(toUpdate.getInterestForDelaySequence())) {
                updatedFields.add("interestForDelaySequence");
            }
            toUpdate.setInterestForDelaySequence(interestForDelaySequence);
        });
        ofNullable(resource.getMinBalanceTrigger()).ifPresent(minBalance -> {
            if (!resource.getMinBalanceTrigger().equals(toUpdate.getMinBalanceTrigger())) {
                updatedFields.add("minBalanceTrigger");
            }
            toUpdate.setMinBalanceTrigger(minBalance);
        });
        ofNullable(resource.getDetermineLevelBy()).ifPresent(determineLevelBy -> {
            if (!resource.getDetermineLevelBy().equals(toUpdate.getDetermineLevelBy())) {
                updatedFields.add("determineLevelBy");
            }
            toUpdate.setDetermineLevelBy(determineLevelBy);
        });
        if(resource.getPolicyPriority() == null && toUpdate.getPolicyPriority() != null) {
            updatedFields.add("policyPriority");
            toUpdate.setPolicyPriority(resource.getPolicyPriority());
        }else {
            ofNullable(resource.getPolicyPriority()).ifPresent(policyPriority -> {
                if (!resource.getPolicyPriority().equals(toUpdate.getPolicyPriority())) {
                    updatedFields.add("policyPriority");
                }
                toUpdate.setPolicyPriority(policyPriority);
            });
        }
        if (resource.getMinBalanceTriggerCurrency() != null) {
            if(resource.getMinBalanceTriggerCurrency().getCode() == null) {                
                if(toUpdate.getMinBalanceTriggerCurrency() != null 
                        && toUpdate.getMinBalanceTriggerCurrency().getCurrencyCode() != null 
                        && toUpdate.getMinBalanceTriggerCurrency().getCurrencyCode() != "") {
                    updatedFields.add("minBalanceTriggerCurrency");
                }
                toUpdate.setMinBalanceTriggerCurrency(null);
            } else {
                if (toUpdate.getMinBalanceTriggerCurrency() == null
                        || !resource.getMinBalanceTriggerCurrency().getCode().equals(toUpdate.getMinBalanceTriggerCurrency().getCurrencyCode())) {
                    Currency currency = new Currency();
                    currency.setCurrencyCode(resource.getMinBalanceTriggerCurrency().getCode());
                    toUpdate.setMinBalanceTriggerCurrency(currency);
                    updatedFields.add("minBalanceTriggerCurrency");
                }
            }
        }
        return toUpdate;
    }
}