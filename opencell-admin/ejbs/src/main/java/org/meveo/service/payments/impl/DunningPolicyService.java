package org.meveo.service.payments.impl;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.meveo.model.billing.InvoicePaymentStatusEnum.UNPAID;
import static org.meveo.service.payments.impl.PolicyConditionTargetEnum.valueOf;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.Invoice;
import org.meveo.model.dunning.*;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.InvoiceService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Stateless
public class DunningPolicyService extends PersistenceService<DunningPolicy> {

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private DunningCollectionPlanService collectionPlanService;

    @Inject
    private DunningCollectionPlanStatusService collectionPlanStatusService;

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
        policy = refreshOrRetrieve(policy);
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

    public void processEligibleInvoice(Map<DunningPolicy, List<Invoice>> eligibleInvoice) {
        DunningCollectionPlanStatus collectionPlanStatus = collectionPlanStatusService.findByStatus("Actif");
        for (Map.Entry<DunningPolicy, List<Invoice>> entry : eligibleInvoice.entrySet()) {
            DunningPolicy policy = refreshOrRetrieve(entry.getKey());
            Integer dayOverDue = policy.getDunningLevels().stream()
                    .filter(policyLevel -> policyLevel.getSequence() == 1)
                    .map(policyLevel -> policyLevel.getDunningLevel().getDaysOverdue())
                    .findFirst()
                    .orElseThrow(BusinessException::new);
            entry.getValue().stream()
                    .filter(invoice -> invoiceEligibilityCheck(invoice, policy, dayOverDue))
                    .forEach(invoice ->
                            collectionPlanService.createCollectionPlanFrom(invoice, policy, dayOverDue, collectionPlanStatus));
        }
    }

    private boolean invoiceEligibilityCheck(Invoice invoice, DunningPolicy policy, Integer dayOverDue) {
        boolean dayOverDueAndThresholdCondition;
        invoice = invoiceService.refreshOrRetrieve(invoice);
        Date today = new Date();
        if (policy.getDetermineLevelBy().equals(DunningDetermineLevelBy.DAYS_OVERDUE)) {
            dayOverDueAndThresholdCondition =
                    (dayOverDue.longValue() == DAYS.between(invoice.getDueDate().toInstant(), today.toInstant()));
        } else {
            dayOverDueAndThresholdCondition =
                    (dayOverDue.longValue() == DAYS.between(invoice.getDueDate().toInstant(), today.toInstant())
                            || invoice.getRecordedInvoice().getUnMatchingAmount().doubleValue() >= policy.getMinBalanceTrigger());
        }
        return invoice.getPaymentStatus().equals(UNPAID)
                && collectionPlanService.findByInvoiceId(invoice.getId()).isEmpty() && dayOverDueAndThresholdCondition;
    }
}