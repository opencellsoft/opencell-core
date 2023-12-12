package org.meveo.service.payments.impl;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Comparator.comparing;
import static java.util.Optional.*;
import static org.meveo.model.billing.InvoicePaymentStatusEnum.PENDING;
import static org.meveo.model.billing.InvoicePaymentStatusEnum.UNPAID;
import static org.meveo.model.dunning.PolicyConditionTargetEnum.valueOf;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.dunning.*;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.DunningCollectionPlanStatusEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.TradingCurrencyService;
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

    @Inject
    private CurrencyService currencyService;

    @Inject
    private TradingCurrencyService tradingCurrencyService;

    @Inject
    protected ResourceBundle resourceMessages;

    @Inject
    private DunningSettingsService dunningSettingsService;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

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
                String query = "SELECT inv FROM Invoice inv WHERE (inv.paymentStatus = 'UNPAID' OR inv.paymentStatus = 'PPAID') AND inv.dunningCollectionPlanTriggered = false AND "
                        + buildPolicyRulesFilter(policy.getDunningPolicyRules());
                return (List<Invoice>) invoiceService.executeSelectQuery(query, null);
            } catch (Exception exception) {
                throw new BusinessException(exception.getMessage());
            }
        }
        return EMPTY_LIST;
    }
    
    public List<Invoice> findEligibleInvoicesForPolicy(DunningPolicy policy,List<Long> invoiceIds) {
        policy = refreshOrRetrieve(policy);
        if (policy == null) {
            throw new BusinessException("Policy does not exists");
        }
        
        if(policy.getDunningPolicyRules() != null && !policy.getDunningPolicyRules().isEmpty()) {
            try {
                String query = "SELECT inv FROM Invoice inv WHERE inv.id in ("+ invoiceIds.stream().map(String::valueOf).collect(Collectors.joining(",")) + ") and ( " + buildPolicyRulesFilter(policy.getDunningPolicyRules()) +" )";
                return (List<Invoice>) invoiceService.executeSelectQuery(query, null);
            } catch (Exception exception) {
                throw new BusinessException(exception.getMessage());
            }
        }
        return EMPTY_LIST;
    }
    
    public boolean existPolicyRulesCheck(DunningPolicy policy) {
        policy = refreshOrRetrieve(policy);
        if (policy == null) {
            throw new BusinessException("Policy does not exists");
        }
        return (policy.getDunningPolicyRules() != null && !policy.getDunningPolicyRules().isEmpty());
    }
    
    public boolean minBalanceTriggerCurrencyCheck(DunningPolicy policy, Invoice invoice) {
		boolean minBalanceTriggerCurrencyBool;
        policy = refreshOrRetrieve(policy);
        invoice = invoiceService.refreshOrRetrieve(invoice);
        if (policy == null) {
            throw new BusinessException("Policy does not exists");
        }

    	if(policy.getMinBalanceTriggerCurrency() != null && policy.getMinBalanceTriggerCurrency().getCurrencyCode() != null) {
    		TradingCurrency tradingCurrency = tradingCurrencyService.findById(invoice.getTradingCurrency().getId());
    		 if(tradingCurrency != null && policy.getMinBalanceTriggerCurrency().getCurrencyCode().equals(tradingCurrency.getCurrencyCode())) {
    			 minBalanceTriggerCurrencyBool = true;
    		 }else {
    			 minBalanceTriggerCurrencyBool = false;
    		 }
    	}else {
    		minBalanceTriggerCurrencyBool = true;
    	}
        return minBalanceTriggerCurrencyBool;
    }
    
    public boolean minBalanceTriggerCheck(DunningPolicy policy, Invoice invoice) {
		boolean minBalanceTriggerBool;
        policy = refreshOrRetrieve(policy);
        invoice = invoiceService.refreshOrRetrieve(invoice);
        if (policy == null) {
            throw new BusinessException("Policy does not exists");
        }

    	if(policy.getMinBalanceTrigger() != null) {
    		
            BigDecimal minBalance = ofNullable(invoice.getRecordedInvoice())
                    .map(RecordedInvoice::getUnMatchingAmount)
                    .orElse(BigDecimal.ZERO);
            
    		if(minBalance.doubleValue() >= policy.getMinBalanceTrigger()) {
    			minBalanceTriggerBool = true;
    		}else {
    			minBalanceTriggerBool = false;
    		}
    	}else{
    		minBalanceTriggerBool = true;
    	}

    	return minBalanceTriggerBool;
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
            lineFilter.append("(");
            if(ruleLines.get(0).getPolicyConditionTarget().equalsIgnoreCase(PolicyConditionTargetEnum.creditCategory.toString())) {
            	if (ruleLines.get(0).getPolicyConditionOperator().equalsIgnoreCase(PolicyConditionOperatorEnum.NOT_EQUALS.toString())) {
	                lineFilter.append(" (inv.billingAccount.customerAccount.creditCategory IS NULL or ")
	                .append("inv.billingAccount.customerAccount.creditCategory in (select creditCategory from CreditCategory creditCategory where creditCategory.code <> ")
	                .append(toQueryValue(ruleLines.get(0).getPolicyConditionTargetValue(),
	                        ruleLines.get(0).getPolicyConditionTarget()))
	                .append(")) ");
            	}else if(ruleLines.get(0).getPolicyConditionOperator().equalsIgnoreCase(PolicyConditionOperatorEnum.EQUALS.toString())) {
                    lineFilter.append(" ( inv.billingAccount.customerAccount.creditCategory IS NOT NULL and ")
                	.append(valueOf(ruleLines.get(0).getPolicyConditionTarget()).getField())
                    .append(" ")
                    .append(PolicyConditionOperatorEnum
                            .valueOf(ruleLines.get(0).getPolicyConditionOperator().toUpperCase()).getOperator())
                    .append(" ")
                    .append(toQueryValue(ruleLines.get(0).getPolicyConditionTargetValue(),
                            ruleLines.get(0).getPolicyConditionTarget()))
                    .append(") ");
            	}
            }else {
            	lineFilter.append(valueOf(ruleLines.get(0).getPolicyConditionTarget()).getField())
                .append(" ")
                .append(PolicyConditionOperatorEnum
                        .valueOf(ruleLines.get(0).getPolicyConditionOperator().toUpperCase()).getOperator())
                .append(" ")
                .append(toQueryValue(ruleLines.get(0).getPolicyConditionTargetValue(),
                        ruleLines.get(0).getPolicyConditionTarget()))
                .append(" ");
            }
            
            for (int index = 1; index < ruleLines.size(); index++) {
                lineFilter.append(checkRuleLineJoint(ruleLines.get(index).getRuleLineJoint()));
                if(ruleLines.get(index).getPolicyConditionTarget().equalsIgnoreCase(PolicyConditionTargetEnum.creditCategory.toString())) {
                	if (ruleLines.get(index).getPolicyConditionOperator().equalsIgnoreCase(PolicyConditionOperatorEnum.NOT_EQUALS.toString())) {
    	                lineFilter.append(" (inv.billingAccount.customerAccount.creditCategory IS NULL or ")
    	                .append("inv.billingAccount.customerAccount.creditCategory in (select creditCategory from CreditCategory creditCategory where creditCategory.code <> ")
    	                .append(toQueryValue(ruleLines.get(index).getPolicyConditionTargetValue(),
    	                        ruleLines.get(index).getPolicyConditionTarget()))
    	                .append(")) ");
                	}else if(ruleLines.get(index).getPolicyConditionOperator().equalsIgnoreCase(PolicyConditionOperatorEnum.EQUALS.toString())) {
                        lineFilter.append(" ( inv.billingAccount.customerAccount.creditCategory IS NOT NULL and ")
                    	.append(valueOf(ruleLines.get(index).getPolicyConditionTarget()).getField())
                        .append(" ")
                        .append(PolicyConditionOperatorEnum
                                .valueOf(ruleLines.get(index).getPolicyConditionOperator().toUpperCase()).getOperator())
                        .append(" ")
                        .append(toQueryValue(ruleLines.get(index).getPolicyConditionTargetValue(),
                                ruleLines.get(index).getPolicyConditionTarget()))
                        .append(") ");
                	}
                }else {
                	lineFilter.append(" ")
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

    public void processEligibleInvoice(Map<DunningPolicy, List<Invoice>> eligibleInvoice, JobExecutionResultImpl jobExecutionResult) {
        DunningCollectionPlanStatus collectionPlanStatus = collectionPlanStatusService.findByStatus(DunningCollectionPlanStatusEnum.ACTIVE);
        AtomicInteger dunningCollectionPlanNumber = new AtomicInteger(0);
        for (Map.Entry<DunningPolicy, List<Invoice>> entry : eligibleInvoice.entrySet()) {
            DunningPolicy policy = refreshOrRetrieve(entry.getKey());
            boolean policyIsReminderExists = doesPolicyContainReminder(policy.getDunningLevels());
            Optional<DunningPolicyLevel> firstLevel = policy.getDunningLevels()
                    .stream()
                    .filter(policyLevel -> ((policyIsReminderExists && policyLevel.getSequence() == 1) || ( policyLevel.getSequence() == 0)) && !policyLevel.getDunningLevel().isReminder())
                    .findFirst();
            if(!firstLevel.isEmpty()) {
                Integer dayOverDue = firstLevel.map(policyLevel -> policyLevel.getDunningLevel().getDaysOverdue()).get();
                entry.getValue()
                        .stream()
                        .filter(invoice -> !invoice.isDunningCollectionPlanTriggered())
                        .filter(invoice -> invoiceEligibilityCheck(invoice, policy, dayOverDue))
                        .forEach(invoice -> {
                            dunningCollectionPlanNumber.incrementAndGet();
                            collectionPlanService.createCollectionPlanFrom(invoice, policy, dayOverDue, collectionPlanStatus);
                        });
            } else {
                log.error("No level configured do meet the conditions for policy" + policy.getPolicyName());
            }
        }
        if (dunningCollectionPlanNumber.get() != 0) {
            jobExecutionResult.addReport(resourceMessages.getString("jobExecution.dunning.collection.plan.lines.number", dunningCollectionPlanNumber.get()));
        }
    }

    private boolean doesPolicyContainReminder(List<DunningPolicyLevel> dunningLevels) {
    	return dunningLevels.stream().anyMatch(policyLevel -> policyLevel.getDunningLevel().isReminder());
	}

	private boolean invoiceEligibilityCheck(Invoice invoice, DunningPolicy policy, Integer dayOverDue) {
        boolean dayOverDueAndThresholdCondition = false;
        invoice = invoiceService.refreshOrRetrieve(invoice);
        Date today;
        Date dueDate;
        try {
            today = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
            dueDate = simpleDateFormat.parse(simpleDateFormat.format(invoice.getDueDate()));
        } catch (ParseException exception) {
            throw new BusinessException(exception);
        }
        long daysDiff = TimeUnit.DAYS.convert((today.getTime() - dueDate.getTime()), TimeUnit.MILLISECONDS);

        DunningSettings dunningSettings = dunningSettingsService.findLastOne();

        if(dunningSettings != null) {
            if(dunningSettings.getDunningMode().equals(DunningModeEnum.INVOICE_LEVEL)) {
                if (policy.getDetermineLevelBy().equals(DunningDetermineLevelBy.DAYS_OVERDUE)) {
                    dayOverDueAndThresholdCondition = (dayOverDue.longValue() <= daysDiff);
                } else {
                    BigDecimal minBalance = ofNullable(invoice.getRecordedInvoice())
                            .map(RecordedInvoice::getUnMatchingAmount)
                            .orElse(BigDecimal.ZERO);
                    dayOverDueAndThresholdCondition =
                            (dayOverDue.longValue() == daysDiff || minBalance.doubleValue() >= policy.getMinBalanceTrigger());
                }
            } else if(dunningSettings.getDunningMode().equals(DunningModeEnum.CUSTOMER_LEVEL)) {
                if (policy.getDetermineLevelBy().equals(DunningDetermineLevelBy.DAYS_OVERDUE)) {
                    if(invoice != null && invoice.getBillingAccount() != null && invoice.getBillingAccount().getCustomerAccount() != null && invoice.getBillingAccount().getCustomerAccount().getAccountOperations() != null) {
                        for(BillingAccount billingAccount : invoice.getBillingAccount().getCustomerAccount().getBillingAccounts()) {
                            if(!dayOverDueAndThresholdCondition) {
                                for(Invoice inv : billingAccount.getInvoices()) {
                                    try {
                                        daysDiff = TimeUnit.DAYS.convert((today.getTime() - simpleDateFormat.parse(simpleDateFormat.format(inv.getDueDate())).getTime()), TimeUnit.MILLISECONDS);
                                    } catch (ParseException e) {
                                        throw new RuntimeException(e);
                                    }

                                    if(dayOverDue.longValue() <= daysDiff) {
                                        dayOverDueAndThresholdCondition = (dayOverDue.longValue() <= daysDiff);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    BigDecimal minBalance = BigDecimal.valueOf(0);

                    if(invoice != null && invoice.getBillingAccount() != null && invoice.getBillingAccount().getCustomerAccount() != null && invoice.getBillingAccount().getCustomerAccount().getAccountOperations() != null) {
                        for(BillingAccount billingAccount : invoice.getBillingAccount().getCustomerAccount().getBillingAccounts()) {
                            for(Invoice inv : billingAccount.getInvoices()) {
                                minBalance = minBalance.add(ofNullable(inv.getRecordedInvoice())
                                        .map(RecordedInvoice::getUnMatchingAmount)
                                        .orElse(BigDecimal.ZERO));
                            }
                        }
                    }

                    dayOverDueAndThresholdCondition = (dayOverDue.longValue() == daysDiff || minBalance.doubleValue() >= policy.getMinBalanceTrigger());
                }
            }
        }

        return (invoice.getPaymentStatus().equals(UNPAID) || invoice.getPaymentStatus().equals(PENDING))
                && collectionPlanService.findByInvoiceId(invoice.getId()).isEmpty()
                && dayOverDueAndThresholdCondition;
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

    public int deactivatePoliciesByIds(Set<Long> dunningPolicyIds){
        return getEntityManager()
                .createNamedQuery("DunningPolicy.DeactivateDunningPolicies")
                .setParameter("ids", dunningPolicyIds)
                .executeUpdate();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updatePolicyWithLevel(DunningPolicy dunningPolicy, List<DunningPolicyLevel> dunningPolicyLevels) {
        int sequence = 0;
        if(dunningPolicy.getMinBalanceTriggerCurrency() != null && dunningPolicy.getMinBalanceTriggerCurrency().getId() == null
                && dunningPolicy.getMinBalanceTriggerCurrency().getCurrencyCode() != null) {
            Currency currency = currencyService.findByCode(dunningPolicy.getMinBalanceTriggerCurrency().getCurrencyCode());
            if(currency == null) {
                throw new BusinessException("Currency with code " + dunningPolicy.getMinBalanceTriggerCurrency().getCurrencyCode() + " not found");
            }
            dunningPolicy.setMinBalanceTriggerCurrency(currency);
        }
        if(!dunningPolicyLevels.isEmpty()) {
            dunningPolicy.getDunningLevels().sort(comparing(level -> level.getDunningLevel().getDaysOverdue()));
            for (DunningPolicyLevel policyLevel : dunningPolicy.getDunningLevels()) {
                if(policyLevel.getId() == null) {
                    policyLevel.setSequence(sequence++);
                    policyLevel.setDunningPolicy(dunningPolicy);
                    dunningPolicyLevelService.create(policyLevel);
                }
            }
            dunningPolicy.setDunningLevels(dunningPolicyLevels);
        }
        update(dunningPolicy);
    }

    /**
     * Update dunning policies by setting active = true or false according to the selected dunning settings (INVOICE_LEVEL or CUSTOMER_LEVEL)
     * @param pDunningMode {@link DunningModeEnum}
     */
    public void updateDunningPoliciesAfterCreatingOrUpdatingDunningSetting(DunningModeEnum pDunningMode) {
        getEntityManager().createNamedQuery("DunningPolicy.activateByDunningMode").setParameter("dunningMode", pDunningMode).executeUpdate();
        getEntityManager().createNamedQuery("DunningPolicy.deactivateByDunningMode").setParameter("dunningMode", pDunningMode).executeUpdate();
    }
    
    /**
     * Check if priority already taken by an other policy
     * @param priority
     * @return true if priority already taken, false if not
     */
    public boolean checkIfSamePriorityExists(int priority) {
        return !getEntityManager().createNamedQuery("DunningPolicy.checkIfPriorityAlreadyTaken")
                .setParameter("priority", priority)
                .getResultList()
                .isEmpty();
    }

    public List<Invoice> findEligibleInvoicesToTriggerReminder(DunningPolicy policy) {
        policy = refreshOrRetrieve(policy);
        if (policy == null) {
            throw new BusinessException("Policy does not exists");
        }
        if(policy.getDunningPolicyRules() != null && !policy.getDunningPolicyRules().isEmpty()) {
            try {
                String query = "SELECT inv FROM Invoice inv WHERE (inv.paymentStatus = 'UNPAID' OR inv.paymentStatus = 'PPAID') AND inv.isReminderLevelTriggered = false AND "
                        + buildPolicyRulesFilter(policy.getDunningPolicyRules());
                return (List<Invoice>) invoiceService.executeSelectQuery(query, null);
            } catch (Exception exception) {
                throw new BusinessException(exception.getMessage());
            }
        }
        return EMPTY_LIST;
    }
}