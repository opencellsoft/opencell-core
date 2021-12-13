package org.meveo.apiv2.dunning.impl;

import org.meveo.apiv2.dunning.PolicyRule;
import org.meveo.apiv2.ordering.ResourceMapper;

public class DunningPolicyRuleMapper extends ResourceMapper<PolicyRule, org.meveo.model.dunning.DunningPolicyRule> {

    @Override
    protected PolicyRule toResource(org.meveo.model.dunning.DunningPolicyRule entity) {
        return null;
    }

    @Override
    protected org.meveo.model.dunning.DunningPolicyRule toEntity(PolicyRule resource) {
        org.meveo.model.dunning.DunningPolicyRule dunningPolicyRule = new org.meveo.model.dunning.DunningPolicyRule();
        if(resource.getRuleJoint() != null) {
            dunningPolicyRule.setRuleJoint(resource.getRuleJoint().name());
        }
        return dunningPolicyRule;
    }
}