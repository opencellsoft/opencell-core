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

import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.async.UsageRatingAsync;
import org.meveo.event.qualifier.Rejected;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.EdrService;
import org.slf4j.Logger;

@Stateless
public class UsageRatingJobBean extends BaseJobBean {

    /**
     * Number of EDRS to process in a single job run
     */
    private static int PROCESS_NR_IN_JOB_RUN = 2000000;

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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);

        try {
            Date rateUntilDate = null;
            String ratingGroup = null;
            try {
                rateUntilDate = (Date) this.getParamOrCFValue(jobInstance, "rateUntilDate");
                ratingGroup = (String) this.getParamOrCFValue(jobInstance, "ratingGroup");
            } catch (Exception e) {
                log.warn("Cant get customFields for {}. {}", jobInstance.getJobTemplate(), e.getMessage());
            }

            List<Long> edrIds = edrService.getEDRsToRate(rateUntilDate, ratingGroup, PROCESS_NR_IN_JOB_RUN);

            result.setNbItemsToProcess(edrIds.size());

            List<Future<String>> futures = new ArrayList<>();
            SubListCreator<Long> subListCreator = new SubListCreator(edrIds, nbRuns.intValue());
            log.info("Will rate {} EDRS", edrIds.size());

            MeveoUser lastCurrentUser = currentUser.unProxy();
            while (subListCreator.isHasNext()) {
                futures.add(usageRatingAsync.launchAndForget(subListCreator.getNextWorkSet(), result, lastCurrentUser));

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

            // Check if there are any more EDRS to process and mark job as completed if there are none
            edrIds = edrService.getEDRsToRate(rateUntilDate, ratingGroup, 1);
            result.setDone(edrIds.isEmpty());

        } catch (Exception e) {
            log.error("Failed to run usage rating job", e);
            result.registerError(e.getMessage());
            result.addReport(e.getMessage());
        }
    }

}
