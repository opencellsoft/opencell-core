package org.meveo.admin.job;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.slf4j.Logger;

@Stateless
public class InvoicingJobBean {

    @Inject
    protected Logger log;

    @Inject
    private BillingRunService billingRunService;

    @Inject
    private CustomFieldInstanceService customFieldInstanceService;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running for parameter={}", jobInstance.getParametres());

        try {
            List<BillingRun> billingRuns = billingRunService.getbillingRuns(BillingRunStatusEnum.NEW, BillingRunStatusEnum.PREVALIDATED, BillingRunStatusEnum.POSTVALIDATED);
            log.info("BillingRuns to process={}", billingRuns.size());
            result.setNbItemsToProcess(billingRuns.size());
            Long nbRuns = new Long(1);
            Long waitingMillis = new Long(0);
            try {
                nbRuns = (Long) customFieldInstanceService.getCFValue(jobInstance, "nbRuns");
                waitingMillis = (Long) customFieldInstanceService.getCFValue(jobInstance, "waitingMillis");
                if (nbRuns == -1) {
                    nbRuns = (long) Runtime.getRuntime().availableProcessors();
                }
            } catch (Exception e) {
                nbRuns = new Long(1);
                waitingMillis = new Long(0);
                log.warn("Cant get customFields for " + jobInstance.getJobTemplate(), e.getMessage());
            }
            for (BillingRun billingRun : billingRuns) {
                try {

                    billingRunService.detach(billingRun);
                    billingRunService.validate(billingRun, nbRuns.longValue(), waitingMillis.longValue());
                    result.registerSucces();
                } catch (Exception e) {
                    log.error("Failed to run invoicing", e);
                    result.registerError(e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Failed to run invoicing", e);
        }

        log.info("end Execute");
    }
}