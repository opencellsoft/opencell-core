package org.meveo.service.payments.impl;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Comparator.comparing;
import static java.util.Optional.*;
import static org.meveo.model.billing.InvoicePaymentStatusEnum.UNPAID;
import static org.meveo.model.dunning.PolicyConditionTargetEnum.valueOf;

import java.math.BigDecimal;
import java.util.*;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.Invoice;
import org.meveo.model.dunning.*;
import org.meveo.model.payments.DunningCollectionPlanStatusEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.InvoiceService;

@Stateless
public class DunningPolicyService extends PersistenceService<DunningPolicy> {

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private DunningCollectionPlanService collectionPlanService;

    @Inject
    private DunningCollectionPlanStatusService collectionPlanStatusService;

    @Inject
    private DunningPolicyLevelService dunningPolicyLevelService;

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
        if (policy == null) {
            throw new BusinessException("Policy does not exists");
        }
        if(policy.getDunningPolicyRules() != null && !policy.getDunningPolicyRules().isEmpty()) {
            try {
                String query = "SELECT inv FROM Invoice inv WHERE " + buildPolicyRulesFilter(policy.getDunningPolicyRules());
                return (List<Invoice>) invoiceService.executeSelectQuery(query, null);
            } catch (Exception exception) {
                throw new BusinessException(exception.getMessage());
            }
        }
        return EMPTY_LIST;
    }

    private String buildPolicyRulesFilter(List<DunningPolicyRule> rules) {
        StringBuilder ruleFilter = new StringBuilder();
        if(rules != null && !rules.isEmpty()) {
            rules.sort(Comparator.comparing(DunningPolicyRule::getId));
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
            ruleLines.sort(Comparator.comparing(DunningPolicyRuleLine::getId));            
            lineFilter.append("(")
                    .append(valueOf(ruleLines.get(0).getPolicyConditionTarget()).getField())
                    .append(" ")
                    .append(PolicyConditionOperatorEnum
                            .valueOf(ruleLines.get(0).getPolicyConditionOperator().toUpperCase()).getOperator())
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
        DunningCollectionPlanStatus collectionPlanStatus = collectionPlanStatusService.findByStatus(DunningCollectionPlanStatusEnum.ACTIVE);
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
            BigDecimal minBalance = ofNullable(invoice.getRecordedInvoice())
                                            .map(RecordedInvoice::getUnMatchingAmount)
                                            .orElse(BigDecimal.ZERO);
            dayOverDueAndThresholdCondition =
                    (dayOverDue.longValue() == DAYS.between(invoice.getDueDate().toInstant(), today.toInstant())
                            || minBalance.doubleValue() >= policy.getMinBalanceTrigger());
        }
        return invoice.getPaymentStatus().equals(UNPAID)
                && collectionPlanService.findByInvoiceId(invoice.getId()).isEmpty() && dayOverDueAndThresholdCondition;
    }

    public List<DunningPolicy> availablePoliciesForSwitch(Invoice invoice) {
        List<DunningPolicy> availablePoliciesForSwitch = new ArrayList<>();
        invoice = invoiceService.refreshOrRetrieve(invoice);
        for (DunningPolicy policy : list()) {
            if(checkInvoiceMatch(findEligibleInvoicesForPolicy(policy), invoice)) {
                availablePoliciesForSwitch.add(policy);
            }
        }
        return availablePoliciesForSwitch;
    }

    private boolean checkInvoiceMatch(List<Invoice> invoices, Invoice invoice) {
        return invoices.stream()
                        .anyMatch(inv -> inv.getId() == invoice.getId());
    }

    public List<DunningPolicy> getPolicies(boolean active) {
        try {
            return getEntityManager().createNamedQuery("DunningPolicy.listPoliciesByIsActive", DunningPolicy.class)
                    .setParameter("active", active)
                    .getResultList();
        } catch (Exception exception) {
            throw new BusinessException(exception.getMessage());
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updatePolicyWithLevel(DunningPolicy dunningPolicy, List<DunningPolicyLevel> dunningPolicyLevels) {
        int sequence = 0;
        update(dunningPolicy);
        if(!dunningPolicyLevels.isEmpty()) {
            dunningPolicy.getDunningLevels().sort(comparing(level -> level.getDunningLevel().getDaysOverdue()));
            for (DunningPolicyLevel level : dunningPolicy.getDunningLevels()) {
                level.setSequence(sequence++);
                level.setDunningPolicy(dunningPolicy);
                dunningPolicyLevelService.update(level);
            }
        }
    }
}