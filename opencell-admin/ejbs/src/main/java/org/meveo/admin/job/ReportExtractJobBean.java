package org.meveo.admin.job;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.ReportExtractAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.finance.ReportExtractService;
import org.slf4j.Logger;

/**
 * List all ReportExtract and dispatched for asynch execution.
 * 
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 5.0
 * @lastModifiedVersion 5.1
 **/
@Stateless
public class ReportExtractJobBean extends BaseJobBean implements Serializable {

    private static final long serialVersionUID = 9159856207913605563L;

    @Inject
    private Logger log;

    @Inject
    private ReportExtractService reportExtractService;

    @Inject
    private ReportExtractAsync reportExtractAsync;
    
    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @SuppressWarnings("unchecked")
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("start in running with parameter={}", jobInstance.getParametres());
        try {
            Long nbRuns = new Long(1);
            Long waitingMillis = new Long(0);
            Date startDate = null, endDate = null;
            try {
                nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns");
                waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis");
                startDate = (Date) this.getParamOrCFValue(jobInstance, "startDate");
                endDate = (Date) this.getParamOrCFValue(jobInstance, "endDate");
                if (nbRuns == -1) {
                    nbRuns = (long) Runtime.getRuntime().availableProcessors();
                }
            } catch (Exception e) {
                nbRuns = new Long(1);
                waitingMillis = new Long(0);
                log.warn("Cant get customFields for " + jobInstance.getJobTemplate(), e.getMessage());
            }

            List<Long> reportExtractIds = reportExtractService.listIds();
            log.debug("Report to execute={}" + (reportExtractIds == null ? null : reportExtractIds.size()));

            List<Future<String>> futures = new ArrayList<Future<String>>();
            SubListCreator subListCreator = new SubListCreator(reportExtractIds, nbRuns.intValue());

            log.debug("block to run:" + subListCreator.getBlocToRun());
            log.debug("nbThreads:" + nbRuns);
            MeveoUser lastCurrentUser = currentUser.unProxy();
            while (subListCreator.isHasNext()) {
                futures.add(reportExtractAsync.launchAndForget((List<Long>) subListCreator.getNextWorkSet(), result, startDate, endDate, lastCurrentUser));
                if (subListCreator.isHasNext()) {
                    try {
                        Thread.sleep(waitingMillis.longValue());
                    } catch (InterruptedException e) {
                        log.error("", e);
                        Thread.currentThread().interrupt();

                    }
                }
            }
            // Wait for all async methods to finish
            for (Future<String> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    // It was cancelled from outside - no interest
                    Thread.currentThread().interrupt();

                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    result.registerError(cause.getMessage());
                    log.error("Failed to execute async method", cause);
                }
            }
        } catch (Exception e) {
            log.error("Failed to run recurring rating job", e);
            result.registerError(e.getMessage());
        }
        log.debug("end running RecurringRatingJobBean!");
    }

}
