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

import javax.ejb.AsyncResult;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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
            "  WHERE wo.offer_id = :offerId \n" +
            "   AND wo.status = 'OPEN' AND wo.code LIKE '%_USG_%_IN' AND wo.parameter_1 NOT LIKE '%_NUM_SPE' \n" +
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


    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<BigInteger> offerIds, JobExecutionResultImpl result, MeveoUser lastCurrentUser) {
        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        log.info("Start new thread to process offers pools overage usage. Nbr offerIds={}", offerIds.size());

        int i = 0;
        for (BigInteger offerId : offerIds) {
            i++;
            if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR_FAST == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                break;
            }

            @SuppressWarnings("unchecked")
            List<BigInteger> walletOperations = emWrapper.getEntityManager().createNativeQuery(OFFER_OPENED_WO_QUERY)
                    .setParameter("offerId", offerId.longValue())
                    .getResultList();

            log.info("Checking overage usage on offerId={}. nbr of WO={}", offerId, walletOperations.size());
            int j = 0;
            for (BigInteger walletOperationId : walletOperations) {
                j++;
                if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR_FAST == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                    break;
                }
                offerPoolRatingUnitJobBean.execute(result, walletOperationId.longValue());
            }

        }

        return new AsyncResult<>("OK");
    }

}
