/**
 * 
 */
package org.meveo.admin.async;

import org.meveo.admin.job.OfferPoolRatingUnitJobBean;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;

import javax.ejb.*;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Mounir BOUKAYOUA
 */
@Stateless
public class OfferPoolRatingAsync {

    private static final String OFFER_OPENED_WO_QUERY = "SELECT wo.id \n" +
            "  FROM billing_wallet_operation wo \n" +
            "  WHERE wo.offer_id = :offerId AND wo.status = 'OPEN' \n" +
            "   AND wo.code LIKE 'CH_M2M_USG_%_IN' AND wo.code NOT LIKE '%FREE%' AND wo.parameter_1 NOT LIKE '%_NUM_SPE' \n" +
            "   AND (wo.parameter_2 IS NULL OR wo.parameter_2 != 'DEDUCTED_FROM_POOL')";

    @Inject
    private Logger log;

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private CurrentUserProvider currentUserProvider;

    @Inject
    private OfferPoolRatingUnitJobBean offerPoolRatingUnitJobBean;

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<BigInteger> offerIds, JobExecutionResultImpl result, MeveoUser lastCurrentUser) {
        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        log.info("Start new offer pool rating thread to process workSet of offerIds={}", offerIds.size());

        int i = 0;
        for (BigInteger offerId : offerIds) {
            i++;
            @SuppressWarnings("unchecked")
            List<BigInteger> walletOperations = emWrapper.getEntityManager().createNativeQuery(OFFER_OPENED_WO_QUERY)
                    .setParameter("offerId", offerId.longValue())
                    .getResultList();

            log.info("Start rating overage usage for offerId={}. nbr of WO={}", offerId, walletOperations.size());

            for (BigInteger walletOperationId : walletOperations) {
                i++;
                if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR_FAST == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                    break;
                }
                offerPoolRatingUnitJobBean.execute(result, walletOperationId.longValue());
            }

        }

        return new AsyncResult<>("OK");
    }

}
