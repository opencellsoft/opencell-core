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
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.async.UsageRatingAsync;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.event.qualifier.Rejected;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.EdrService;
import org.slf4j.Logger;

@Stateless
public class UsageRatingJobBean extends BaseJobBean {

    @Inject
    private Logger log;

    @Inject
    private EdrService edrService;

    @Inject
    private UsageRatingAsync usageRatingAsync;

    @Inject
    @Rejected
    private Event<Serializable> rejectededEdrProducer;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @SuppressWarnings("unchecked")
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running with parameter={}", jobInstance.getParametres());

        try {
            Long nbRuns = new Long(1);
            Long waitingMillis = new Long(0);
            Date rateUntilDate = null;
            String ratingGroup = null;
            try {
                nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns");
                waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis");
                if (nbRuns == -1) {
                    nbRuns = (long) Runtime.getRuntime().availableProcessors();
                }
                rateUntilDate = (Date) this.getParamOrCFValue(jobInstance, "rateUntilDate");
                ratingGroup = (String) this.getParamOrCFValue(jobInstance, "ratingGroup");
            } catch (Exception e) {
                nbRuns = 1L;
                waitingMillis = 0L;
                log.warn("Cant get customFields for {}. {}", jobInstance.getJobTemplate(), e.getMessage());
            }
            List<Long> ids = edrService.getEDRidsToRate(rateUntilDate, ratingGroup);
            log.debug("edr to rate={}", ids.size());
            result.setNbItemsToProcess(ids.size());
            List<Future<String>> futures = new ArrayList<>();
            SubListCreator subListCreator = new SubListCreator(ids, nbRuns.intValue());
            log.debug("block to run={}", subListCreator.getBlocToRun());
            log.debug("nbThreads={}", nbRuns);

            MeveoUser lastCurrentUser = currentUser.unProxy();
            while (subListCreator.isHasNext()) {
                futures.add(usageRatingAsync.launchAndForget((List<Long>) subListCreator.getNextWorkSet(), result, lastCurrentUser));

                if (subListCreator.isHasNext()) {
                    try {
                        Thread.sleep(waitingMillis.longValue());
                    } catch (InterruptedException e) {
                        log.error("", e);
                    }
                }
            }
            // Wait for all async methods to finish
            for (Future<String> future : futures) {
                try {
                    future.get();

                } catch (InterruptedException e) {
                    // It was cancelled from outside - no interest

                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    result.registerError(cause.getMessage());
                    result.addReport(cause.getMessage());
                    log.error("Failed to execute async method", cause);
                }
            }
        } catch (Exception e) {
            log.error("Failed to run usage rating job", e);
            result.registerError(e.getMessage());
            result.addReport(e.getMessage());
        }
    }

}
