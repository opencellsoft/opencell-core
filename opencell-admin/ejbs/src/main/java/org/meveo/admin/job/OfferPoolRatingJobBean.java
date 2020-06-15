package org.meveo.admin.job;

import org.meveo.admin.async.OfferPoolRatingAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.WalletOperationService;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Mounir BOUKAYOUA
 */
@Stateless
public class OfferPoolRatingJobBean extends BaseJobBean {

    /**
     * Number of Wallet operations to process in a single job run
     */
    private static int PROCESS_NR_IN_JOB_RUN = 2000000;

    private static final String OFFER_OPENED_WO_QUERY = "SELECT DISTINCT wo.id\n" +
            "  FROM billing_wallet_operation_open wo\n" +
            "   INNER JOIN cat_offer_template ot ON wo.offer_id = ot.id\n" +
            "  WHERE cast(ot.cf_values as json)#>>'{sharingLevel, 0, string}' = 'OF'\n" +
            "   AND wo.status = 'OPEN' AND wo.code LIKE '%_USG_%_IN' AND wo.parameter_1 NOT LIKE '%_NUM_SPE' AND wo.parameter_2 != 'DEDUCTED_FROM_POOL'";

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private Logger log;

    @Inject
    private OfferPoolRatingAsync offerPoolRatingAsync;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running for with parameter={}", jobInstance.getParametres());

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);

        try {

            @SuppressWarnings("unchecked")
            List<BigInteger> woIds = walletOperationService.getEntityManager().createNativeQuery(OFFER_OPENED_WO_QUERY)
                    .getResultList();

            log.info("WalletOperations using offer sharing level which overage usage should be checked ={}", woIds.size());
            result.setNbItemsToProcess(woIds.size());

            SubListCreator<BigInteger> subListCreator = new SubListCreator<>(woIds, nbRuns.intValue());
            List<Future<String>> futures = new ArrayList<>();
            MeveoUser lastCurrentUser = currentUser.unProxy();
            while (subListCreator.isHasNext()) {

                futures.add(offerPoolRatingAsync.launchAndForget((List<BigInteger>) subListCreator.getNextWorkSet(), result, lastCurrentUser));
                try {
                    Thread.sleep(waitingMillis.longValue());

                } catch (InterruptedException e) {
                    log.error("", e);
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

            result.setDone(true);

        } catch (Exception e) {
            log.error("Failed to check overage usage for walletOperations using offer sharing level ", e);
        }
    }
}
