package org.meveo.apiv2.dunning.impl;

import org.meveo.apiv2.dunning.DunningPolicyRuleLine;
import org.meveo.apiv2.ordering.ResourceMapper;

public class DunningPolicyRuleLineMapper extends ResourceMapper<DunningPolicyRuleLine, org.meveo.model.dunning.DunningPolicyRuleLine> {

    @Override
    protected DunningPolicyRuleLine toResource(org.meveo.model.dunning.DunningPolicyRuleLine entity) {
        return null;
    }

    @Override
    public org.meveo.model.dunning.DunningPolicyRuleLine toEntity(DunningPolicyRuleLine resource) {
        org.meveo.model.dunning.DunningPolicyRuleLine dunningPolicyRuleLine = new org.meveo.model.dunning.DunningPolicyRuleLine();
        if(resource.getRuleLineJoint() != null) {
            dunningPolicyRuleLine.setRuleLineJoint(resource.getRuleLineJoint().name());
        }
        if(resource.getPolicyConditionTarget() != null) {
            dunningPolicyRuleLine.setPolicyConditionTarget(resource.getPolicyConditionTarget().name());
        }
        dunningPolicyRuleLine.setPolicyConditionTargetValue(resource.getPolicyConditionTargetValue());
        if(resource.getPolicyConditionOperator() != null) {
            dunningPolicyRuleLine.setPolicyConditionOperator(resource.getPolicyConditionOperator().name());
        }
        return dunningPolicyRuleLine;
    }
}
