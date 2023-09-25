package org.meveo.admin.job;

import static org.meveo.model.billing.BillingRunReportTypeEnum.OPEN_RATED_TRANSACTIONS;
import static org.meveo.model.billing.BillingRunStatusEnum.NEW;
import static org.meveo.model.billing.BillingRunStatusEnum.OPEN;

import org.meveo.model.billing.BillingRun;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.BillingRunReportService;
import org.meveo.service.billing.impl.BillingRunService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

@Stateless
public class BillingRunReportJobBean extends BaseJobBean {

    @Inject
    private BillingRunReportService billingRunReportService;

    @Inject
    private BillingRunService billingRunService;

    public void execute(JobExecutionResultImpl jobExecutionResult) {
        try {
            List<BillingRun> billingRuns = billingRunService.getBillingRuns(NEW, OPEN);
            jobExecutionResult.setNbItemsToProcess(billingRuns.size());
            jobExecutionResult.registerSucces(createBillingRunReport(billingRuns, jobExecutionResult));
        } catch (Exception exception) {
            jobExecutionResult.registerError(exception.getMessage());
            log.error(exception.getMessage());
        }
    }

    private int createBillingRunReport(List<BillingRun> billingRuns, JobExecutionResultImpl jobExecutionResult) {
        int countOfReportCreated = 0;
        for (BillingRun billingRun : billingRuns) {
            billingRunReportService.createBillingRunReport(billingRun, null, OPEN_RATED_TRANSACTIONS);
            billingRun.addJobExecutions(jobExecutionResult);
            countOfReportCreated++;
        }
        return countOfReportCreated;
    }
}
