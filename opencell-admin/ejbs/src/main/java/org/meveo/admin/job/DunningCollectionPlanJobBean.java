package org.meveo.admin.job;

import org.meveo.model.billing.Invoice;
import org.meveo.model.dunning.*;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.payments.impl.*;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.*;

import static java.time.temporal.ChronoUnit.*;
import static org.meveo.model.billing.InvoicePaymentStatusEnum.UNPAID;

@Stateless
public class DunningCollectionPlanJobBean extends BaseJobBean {

    @Inject
    private DunningPolicyService policyService;

    @Inject
    private DunningCollectionPlanService collectionPlanService;
    
    @Inject
    private DunningCollectionPlanStatusService collectionPlanStatusService;

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        List<DunningPolicy> policies = policyService.list();
        jobExecutionResult.setNbItemsToProcess(policies.size());
        if (policies != null && !policies.isEmpty()) {
            Map<DunningPolicy, List<Invoice>> eligibleInvoicesByPolicy = new HashMap<>();
            for (DunningPolicy policy : policies) {
                try {
                    List<Invoice> eligibleInvoice = policyService.findEligibleInvoicesForPolicy(policy);
                    if (eligibleInvoice != null && !eligibleInvoice.isEmpty()) {
                        eligibleInvoicesByPolicy.put(policy, eligibleInvoice);
                    }
                    processEligibleInvoice(eligibleInvoicesByPolicy);
                    jobExecutionResult.addNbItemsCorrectlyProcessed(1);
                } catch (Exception exception) {
                    jobExecutionResult.addErrorReport("Error during processing policy id "
                            + policy.getId() + " " + exception.getMessage());
                }
            }
        }
    }

    private void processEligibleInvoice(Map<DunningPolicy, List<Invoice>> eligibleInvoice) {
        DunningCollectionPlanStatus collectionPlanStatus = collectionPlanStatusService.findByStatus("Actif");
        for (Map.Entry<DunningPolicy, List<Invoice>> entry : eligibleInvoice.entrySet()) {
            Integer dayOverDue =  entry.getKey().getDunningLevels().stream()
                    .filter(policyLevel -> policyLevel.getSequence() == 1)
                    .map(policyLevel -> policyLevel.getDunningLevel().getDaysOverdue())
                    .findFirst()
                    .orElseThrow();
            entry.getValue().stream()
                    .filter(invoice -> invoiceEligibilityCheck(invoice, entry.getKey(), dayOverDue))
                    .forEach(invoice ->
                            collectionPlanService.createCollectionPlanFrom(invoice, entry.getKey(), dayOverDue, collectionPlanStatus));
        }
    }

    private boolean invoiceEligibilityCheck(Invoice invoice, DunningPolicy policy, Integer dayOverDue) {
        boolean dayOverDueAndThresholdCondition;
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
