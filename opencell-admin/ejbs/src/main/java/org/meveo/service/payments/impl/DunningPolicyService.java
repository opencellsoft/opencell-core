package org.meveo.service.payments.impl;

import static org.meveo.service.payments.impl.PolicyConditionTargetEnum.valueOf;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.Invoice;
import org.meveo.model.dunning.DunningPolicy;
import org.meveo.model.dunning.DunningPolicyRule;
import org.meveo.model.dunning.DunningPolicyRuleLine;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.InvoiceService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.util.List;

@Stateless
public class DunningPolicyService extends PersistenceService<DunningPolicy> {

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private DunningPolicyService policyService;

    public DunningPolicy findByName(String policyName) {
        try {
            return getEntityManager().createNamedQuery("DunningPolicy.findByName", DunningPolicy.class)
                    .setParameter("policyName", policyName)
                    .getSingleResult();
        } catch (NoResultException noResultException) {
            throw new BusinessException("Dunning policy does not exits");
        } catch (Exception exception) {
            throw new BusinessException(exception.getMessage());
        }
    }

    public List<Invoice> findEligibleInvoicesForPolicy(DunningPolicy policy) {
        policy = policyService.refreshOrRetrieve(policy);
        try {
            String query = "SELECT inv FROM Invoice inv WHERE " + buildPolicyRulesFilter(policy.getDunningPolicyRules());
            return  (List<Invoice>) invoiceService.executeSelectQuery(query, null);
        } catch (Exception exception) {
            throw new BusinessException(exception.getMessage());
        }
    }

    private String buildPolicyRulesFilter(List<DunningPolicyRule> rules) {
        StringBuilder ruleFilter = new StringBuilder();
        if(rules != null && !rules.isEmpty()) {
            ruleFilter.append(buildRuleLinesFilter(rules.get(0).getDunningPolicyRuleLines()));
            for (int index = 1; index < rules.size(); index++) {
                ruleFilter.append(" ")
                        .append(checkRuleLineJoint(rules.get(index).getRuleJoint()))
                        .append(" ")
                        .append(buildRuleLinesFilter(rules.get(index).getDunningPolicyRuleLines()));
            }
        }
        return ruleFilter.toString();
    }

    private String checkRuleLineJoint(String joint) {
        if(joint.equalsIgnoreCase("OR")
                || joint.equalsIgnoreCase("AND")) {
            return joint.toLowerCase();
        }
        throw new BusinessException("Invalid rule joint [" + joint + "]");
    }

    private String buildRuleLinesFilter(List<DunningPolicyRuleLine> ruleLines) {
        StringBuilder lineFilter = new StringBuilder();
        if(ruleLines != null && !ruleLines.isEmpty()) {
            lineFilter.append("(")
                    .append(valueOf(ruleLines.get(0).getPolicyConditionTarget()).getField())
                    .append(" ")
                    .append(PolicyConditionOperatorEnum
                            .valueOf(ruleLines.get(0).getPolicyConditionOperator()).getOperator())
                    .append(" ")
                    .append(toQueryValue(ruleLines.get(0).getPolicyConditionTargetValue(),
                            ruleLines.get(0).getPolicyConditionTarget()))
                    .append(" ");
            for (int index = 1; index < ruleLines.size(); index++) {
                lineFilter.append(checkRuleLineJoint(ruleLines.get(index).getRuleLineJoint()))
                        .append(" ")
                        .append(valueOf(ruleLines.get(index).getPolicyConditionTarget()).getField())
                        .append(" ")
                        .append(PolicyConditionOperatorEnum
                                .valueOf(ruleLines.get(index).getPolicyConditionOperator()).getOperator())
                        .append(" ")
                        .append(toQueryValue(ruleLines.get(index).getPolicyConditionTargetValue(),
                                ruleLines.get(index).getPolicyConditionTarget()))
                        .append(" ");
            }
        }
        return lineFilter.append(")").toString();
    }

    private String toQueryValue(String policyConditionTargetValue, String policyTarget) {
        if(policyTarget.equalsIgnoreCase("isCompany")) {
            return policyConditionTargetValue;
        } else {
            return "'" + policyConditionTargetValue + "'";
        }
    }
}
