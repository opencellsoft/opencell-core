package org.meveo.admin.job;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.OfferPoolRatingAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.MeveoUser;
import org.slf4j.Logger;

/**
 * @author Mounir BOUKAYOUA
 */
@Stateless
public class OfferPoolRatingJobBean extends BaseJobBean {

    public static final String WO_FILTER_QUERY = "AND wo.code LIKE 'CH_M2M_USG_%_IN' AND wo.code NOT LIKE '%FREE%' AND wo.parameter_1 NOT LIKE '%_NUM_SPE' \n" +
            "AND (wo.parameter_2 IS NULL OR wo.parameter_2 != 'DEDUCTED_FROM_POOL') \n" +
            "AND wo.status != 'CANCELED'";

    private static final String SUB_QUERY = "FROM billing_wallet_operation wo \n" +
            "INNER JOIN cat_offer_template ot ON wo.offer_id = ot.id \n" +
            "WHERE cast(ot.cf_values as json)#>>'{sharingLevel, 0, string}' = 'OF' \n" + WO_FILTER_QUERY;

    private static final String OFFER_OPENED_WO_COUNT_QUERY = "SELECT count(wo.id) \n" + SUB_QUERY;

    private static final String OFFER_WITH_SHARED_POOL = "SELECT DISTINCT ot.id \n" + SUB_QUERY;
    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

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
            BigInteger offersWOsCount = (BigInteger) emWrapper.getEntityManager()
                    .createNativeQuery(OFFER_OPENED_WO_COUNT_QUERY)
                    .getSingleResult();

            log.info("Total of WOs with offers shared pools to check: {}", offersWOsCount.longValue());
            result.setNbItemsToProcess(offersWOsCount.longValue());

            @SuppressWarnings("unchecked")
            List<BigInteger> offerIds = emWrapper.getEntityManager().createNativeQuery(OFFER_WITH_SHARED_POOL)
                    .getResultList();

            log.info("Total of offers with shared pools to process: {}", offerIds.size());

            SubListCreator<BigInteger> subListCreator = new SubListCreator<>(offerIds, nbRuns.intValue());
            List<Future<String>> futures = new ArrayList<>();
            MeveoUser lastCurrentUser = currentUser.unProxy();

            while (subListCreator.isHasNext()) {
                futures.add(offerPoolRatingAsync.launchAndForget(subListCreator.getNextWorkSet(), result, lastCurrentUser));
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
