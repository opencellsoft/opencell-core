package org.meveo.admin.job;

import org.meveo.admin.async.AmendDuplicateConsumptionAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.MeveoUser;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Mounir BOUKAYOUA
 */
@Stateless
public class AmendDuplicateConsumptionJobBean extends BaseJobBean {

    private static final String OFFERS_WITH_DUPLICATED_WO_QUERY = "select distinct wo.offer_id\n" +
            "from billing_wallet_operation wo\n" +
            "where \n" +
            " (wo.code like 'CH_M2M_USG_%_IN' and wo.code not like '%FREE%' and wo.parameter_1 not like '%_NUM_SPE') \n" +
            " and (wo.status='CANCELED' and wo.parameter_extra='DUPLICATE WO') \n" +
            " and (wo.parameter_2 = 'DEDUCTED_FROM_POOL' or wo.counter_id is not null) \n" +
            " and wo.parameter_3 != 'AMENDED_FROM_POOL' \n" +
            "order by wo.offer_id";

    private static final String COUNT_DUPLICATED_WO_QUERY = "select count(wo.id) \n" +
            "from billing_wallet_operation wo\n" +
            "where \n" +
            " (wo.code like 'CH_M2M_USG_%_IN' and wo.code not like '%FREE%' and wo.parameter_1 not like '%_NUM_SPE') \n" +
            " and (wo.status='CANCELED' and wo.parameter_extra='DUPLICATE WO') \n" +
            " and (wo.parameter_2 = 'DEDUCTED_FROM_POOL' or wo.counter_id is not null) \n" +
            " and wo.parameter_3 != 'AMENDED_FROM_POOL'";

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    @Inject
    private Logger log;

    @Inject
    private AmendDuplicateConsumptionAsync amendDuplicateConsumptionAsync;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    @SuppressWarnings("unchecked")
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running for with parameter={}", jobInstance.getParametres());

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);

        try {
            BigInteger WOsCount = (BigInteger) emWrapper.getEntityManager()
                    .createNativeQuery(COUNT_DUPLICATED_WO_QUERY)
                    .getSingleResult();

            log.info("Total of WOs with offers shared pools to check: {}", WOsCount.longValue());
            result.setNbItemsToProcess(WOsCount.longValue());

            @SuppressWarnings("unchecked")
            List<BigInteger> offerIds = emWrapper.getEntityManager().createNativeQuery(OFFERS_WITH_DUPLICATED_WO_QUERY)
                    .getResultList();

            log.info("Total of offers with shared pools to process: {}", offerIds.size());

            SubListCreator<BigInteger> subListCreator = new SubListCreator<>(offerIds, nbRuns.intValue());
            List<Future<String>> futures = new ArrayList<>();
            MeveoUser lastCurrentUser = currentUser.unProxy();

            while (subListCreator.isHasNext()) {
                futures.add(amendDuplicateConsumptionAsync.launchAndForget(subListCreator.getNextWorkSet(), result, lastCurrentUser));
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
            log.error("Failed to amend canceled duplicated WOs from Pools", e);
        }
    }
}
