package org.meveo.admin.job;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.meveo.model.billing.BillingRunReportTypeEnum.OPEN_RATED_TRANSACTIONS;
import static org.meveo.model.billing.BillingRunStatusEnum.NEW;
import static org.meveo.model.billing.BillingRunStatusEnum.OPEN;

import org.meveo.model.billing.BillingRun;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
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

    private List<Long> billingRunIds;

    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        List<EntityReferenceWrapper> billingRunWrappers =
                (List<EntityReferenceWrapper>) this.getParamOrCFValue(jobInstance, "BillingRunReportJob_billingRun");
        billingRunIds = billingRunWrappers != null ? extractBRIds(billingRunWrappers) : emptyList();
        try {
            List<BillingRun> billingRuns = initJobAndGetDataToProcess();
            jobExecutionResult.setNbItemsToProcess(billingRuns.size());
            jobExecutionResult.registerSucces(createBillingRunReport(billingRuns, jobExecutionResult));
        } catch (Exception exception) {
            jobExecutionResult.registerError(exception.getMessage());
            log.error(exception.getMessage());
        }
    }

    private List<Long> extractBRIds(List<EntityReferenceWrapper> billingRunWrappers) {
        return billingRunWrappers.stream()
                .map(br -> Long.valueOf(br.getCode().split("/")[0]))
                .collect(toList());
    }

    private List<BillingRun> initJobAndGetDataToProcess() {
        if(billingRunIds != null && !billingRunIds.isEmpty()) {
            return asList(billingRunService.findById(billingRunIds.get(0)));
        }
        return billingRunService.getBillingRuns(NEW, OPEN);
    }

    private int createBillingRunReport(List<BillingRun> billingRuns, JobExecutionResultImpl jobExecutionResult) {
        int countOfReportCreated = 0;
        for (BillingRun billingRun : billingRuns) {
            billingRunReportService.createBillingRunReport(billingRun, null, OPEN_RATED_TRANSACTIONS);
            billingRunService.updateBillingRunJobExecution(billingRun, jobExecutionResult);
            countOfReportCreated++;
        }
        return countOfReportCreated;
    }
}
