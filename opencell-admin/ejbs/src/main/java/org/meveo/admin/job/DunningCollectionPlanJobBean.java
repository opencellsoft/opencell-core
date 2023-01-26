package org.meveo.admin.job;

import org.meveo.model.billing.Invoice;
import org.meveo.model.dunning.DunningPolicy;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.payments.impl.DunningPolicyService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class DunningCollectionPlanJobBean extends BaseJobBean {

    @Inject
    private DunningPolicyService policyService;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        List<DunningPolicy> policies = policyService.getPolicies(true);
        jobExecutionResult.setNbItemsToProcess(policies.size());
        if (policies != null && !policies.isEmpty()) {
            Map<DunningPolicy, List<Invoice>> eligibleInvoicesByPolicy = new HashMap<>();
            try {
                for (DunningPolicy policy : policies) {
                    List<Invoice> eligibleInvoice = policyService.findEligibleInvoicesForPolicy(policy);
                    if (eligibleInvoice != null && !eligibleInvoice.isEmpty()) {
                        eligibleInvoicesByPolicy.put(policy, eligibleInvoice);
                    }
                }
                policyService.processEligibleInvoice(eligibleInvoicesByPolicy, jobExecutionResult);
                jobExecutionResult.addNbItemsCorrectlyProcessed(policies.size()
                        - jobExecutionResult.getNbItemsProcessedWithError());
            } catch (Exception exception) {
                jobExecutionResult.addErrorReport(exception.getMessage());
            }
        }
    }
}