package org.meveo.admin.job;

import org.meveo.admin.async.OfferPoolRatingAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
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

    private String OFFER_OPENED_WO_COUNT_QUERY = "SELECT count(wo.id)\n" +
            "  FROM billing_wallet_operation wo\n" +
            "   INNER JOIN cat_offer_template ot ON wo.offer_id = ot.id\n" +
            "  WHERE cast(ot.cf_values as json)#>>'{sharingLevel, 0, string}' = 'OF'\n" +
            "   AND wo.status = 'OPEN' AND wo.code LIKE '%_USG_%_IN' AND wo.parameter_1 NOT LIKE '%_NUM_SPE' AND wo.parameter_2 != 'DEDUCTED_FROM_POOL'";

    private String OFFER_WITH_SHARED_POOL = "SELECT DISTINCT ot.id " +
            "  FROM cat_offer_template ot WHERE cast(ot.cf_values as json)#>>'{sharingLevel, 0, string}' = 'OF'";

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
            BigInteger offersWOsCount = (BigInteger) emWrapper.getEntityManager().createNativeQuery(OFFER_OPENED_WO_COUNT_QUERY)
                    .getSingleResult();

            log.info("Nbrs of WOs with offers shared pools to process: {}", offersWOsCount.longValue());
            result.setNbItemsToProcess(offersWOsCount.longValue());

            @SuppressWarnings("unchecked")
            List<BigInteger> offerIds = emWrapper.getEntityManager().createNativeQuery(OFFER_WITH_SHARED_POOL)
                    .getResultList();

            List<Future<String>> futures = new ArrayList<>();
            MeveoUser lastCurrentUser = currentUser.unProxy();
            for (BigInteger offerId : offerIds) {

                futures.add(offerPoolRatingAsync.launchAndForget(offerId.longValue(), result, lastCurrentUser));
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
