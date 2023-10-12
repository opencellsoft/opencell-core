package org.meveo.admin.job;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.meveo.model.billing.BillingRunReportTypeEnum.BILLED_RATED_TRANSACTIONS;
import static org.meveo.model.billing.BillingRunReportTypeEnum.OPEN_RATED_TRANSACTIONS;
import static org.meveo.model.billing.BillingRunStatusEnum.NEW;
import static org.meveo.model.billing.BillingRunStatusEnum.OPEN;

import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunReport;
import org.meveo.model.billing.BillingRunReportTypeEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.BillingRunReportService;
import org.meveo.service.billing.impl.BillingRunService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Stateless
public class BillingRunReportJobBean extends BaseJobBean {

    @Inject
    private BillingRunReportService billingRunReportService;

    @Inject
    private BillingRunService billingRunService;

    private List<Long> billingRunIds;

    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        billingRunIds = jobInstance.getRunTimeValues() != null
                && jobInstance.getRunTimeValues().get("billingRun") != null
                ? extractBRIds((String) jobInstance.getRunTimeValues().get("billingRun")) : emptyList();
        try {
            List<BillingRun> billingRuns = initJobAndGetDataToProcess();
            jobExecutionResult.setNbItemsToProcess(billingRuns.size());
            Map<String, Object> filters = jobInstance.getRunTimeValues() != null
                    ? (Map<String, Object>) jobInstance.getRunTimeValues().get("filters") : null;
            BillingRunReportTypeEnum reportType = filters == null ? OPEN_RATED_TRANSACTIONS : BILLED_RATED_TRANSACTIONS;
            jobExecutionResult.registerSucces(createBillingRunReport(billingRuns, jobExecutionResult, filters, reportType));
        } catch (Exception exception) {
            jobExecutionResult.registerError(exception.getMessage());
            log.error(exception.getMessage());
        }
    }

    private List<Long> extractBRIds(String billingRunIds) {
        if(billingRunIds == null || billingRunIds.isBlank()) {
            return emptyList();
        }
        return stream(billingRunIds.split("/"))
                .map(Long::valueOf)
                .collect(toList());
    }

    private List<BillingRun> initJobAndGetDataToProcess() {
        if(billingRunIds != null && !billingRunIds.isEmpty()) {
            return billingRunIds.stream()
                    .map(id -> billingRunService.findById(id))
                    .collect(toList());
        }
        return billingRunService.getBillingRuns(NEW, OPEN);
    }

    private int createBillingRunReport(List<BillingRun> billingRuns, JobExecutionResultImpl jobExecutionResult,
                                       Map<String, Object> filters, BillingRunReportTypeEnum reportType) {
        int countOfReportCreated = 0;
        for (BillingRun billingRun : billingRuns) {
            billingRunService.updateBillingRunJobExecution(billingRun.getId(), jobExecutionResult);
            if (filters != null && !filters.isEmpty()) {
                filters.put("billingRun", billingRun);
            }
            BillingRunReport billingRunReport =
                    billingRunReportService.createBillingRunReport(billingRun, filters, reportType);
            billingRun = billingRunService.refreshOrRetrieve(billingRun);
            if(OPEN_RATED_TRANSACTIONS.equals(reportType)) {
                billingRun.setPreInvoicingReport(billingRunReport);
            } else {
                billingRun.setBilledRatedTransactionsReport(billingRunReport);
            }
            billingRun.addJobExecutions(jobExecutionResult);
            billingRunService.update(billingRun);
            countOfReportCreated++;
        }
        return countOfReportCreated;
    }
}
