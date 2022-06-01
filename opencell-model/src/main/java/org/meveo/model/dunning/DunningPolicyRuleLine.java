package org.meveo.model.dunning;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "dunning_policy_rule_line")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "dunning_policy_rule_line_seq")})
@NamedQueries({
    @NamedQuery(name = "DunningPolicyRuleLine.findByDunningPolicyId", query = "SELECT dprl from DunningPolicyRuleLine dprl where dprl.dunningPolicyRule.dunningPolicy.id = :policyId"),
    @NamedQuery(name = "DunningPolicyRuleLine.findByDunningPolicyRuleId", query = "SELECT dprl from DunningPolicyRuleLine dprl where dprl.dunningPolicyRule.id = :dunningPolicyRuleId")})
public class DunningPolicyRuleLine extends AuditableEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "policy_condition_operator")
    @NotNull
    private String policyConditionOperator;

    @Column(name = "policy_condition_target")
    @NotNull
    private String policyConditionTarget;

    @Column(name = "policy_condition_target_value")
    @NotNull
    private String policyConditionTargetValue;

    @Column(name = "rule_line_joint")
    private String ruleLineJoint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dunning_policy_rule_id")
    private DunningPolicyRule dunningPolicyRule;

    public String getPolicyConditionOperator() {
        return policyConditionOperator;
    }

    public void setPolicyConditionOperator(String policyConditionOperator) {
        this.policyConditionOperator = policyConditionOperator;
    }

    public DunningPolicyRule getDunningPolicyRule() {
        return dunningPolicyRule;
    }

    public void setDunningPolicyRule(DunningPolicyRule dunningPolicyRule) {
        this.dunningPolicyRule = dunningPolicyRule;
    }

    public String getRuleLineJoint() {
        return ruleLineJoint;
    }

    public void setRuleLineJoint(String ruleLineJoint) {
        this.ruleLineJoint = ruleLineJoint;
    }

    public String getPolicyConditionTarget() {
        return policyConditionTarget;
    }

    public void setPolicyConditionTarget(String policyConditionTarget) {
        this.policyConditionTarget = policyConditionTarget;
    }

    public String getPolicyConditionTargetValue() {
        return policyConditionTargetValue;
    }

    public void setPolicyConditionTargetValue(String policyConditionTargetValue) {
        this.policyConditionTargetValue = policyConditionTargetValue;
    }
}