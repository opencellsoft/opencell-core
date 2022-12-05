package org.meveo.model.dunning;

import static jakarta.persistence.FetchType.LAZY;

import java.util.List;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "dunning_policy_rule")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "dunning_policy_rule_seq")})
@NamedQueries({
        @NamedQuery(name = "DunningPolicyRule.findByDunningPolicyId", query = "SELECT dpr from DunningPolicyRule dpr where dpr.dunningPolicy.id = :policyId") })
public class DunningPolicyRule extends AuditableEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "rule_joint")
    private String ruleJoint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dunning_policy_id")
    private DunningPolicy dunningPolicy;

    @OneToMany(mappedBy = "dunningPolicyRule", fetch = LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<DunningPolicyRuleLine> dunningPolicyRuleLines;

    public String getRuleJoint() {
        return ruleJoint;
    }

    public void setRuleJoint(String ruleJoint) {
        this.ruleJoint = ruleJoint;
    }

    public DunningPolicy getDunningPolicy() {
        return dunningPolicy;
    }

    public void setDunningPolicy(DunningPolicy dunningPolicy) {
        this.dunningPolicy = dunningPolicy;
    }

    public List<DunningPolicyRuleLine> getDunningPolicyRuleLines() {
        return dunningPolicyRuleLines;
    }

    public void setDunningPolicyRuleLines(List<DunningPolicyRuleLine> dunningPolicyRuleLines) {
        this.dunningPolicyRuleLines = dunningPolicyRuleLines;
    }
}