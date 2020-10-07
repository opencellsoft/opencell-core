/**
 * 
 */
package org.meveo.admin.async;

import org.meveo.admin.job.AmendDuplicateConsumptionUnitJobBean;
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
public class AmendDuplicateConsumptionAsync {

    private static final String DUPLICATED_WO_TO_AMEND_QUERY = "select wo.id \n" +
            "from billing_wallet_operation wo\n" +
            "where wo.offer_id = :offerId\n" +
            " and (wo.code like 'CH_M2M_USG_%_IN' and wo.code not like '%FREE%' and wo.parameter_1 not like '%_NUM_SPE') \n" +
            " and (wo.status='CANCELED' and wo.parameter_extra='DUPLICATE WO') \n" +
            " and (wo.parameter_2 = 'DEDUCTED_FROM_POOL' or wo.counter_id is not null) \n" +
            " and wo.parameter_3 != 'AMENDED_FROM_POOL' \n" +
            "order by wo.counter_id, wo.id ";

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
    private AmendDuplicateConsumptionUnitJobBean amendDuplicateConsumptionUnitJobBean;

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<BigInteger> offerIds, JobExecutionResultImpl result, MeveoUser lastCurrentUser) {
        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        log.info("Start amend new group of canceled duplicated WO thread to process. WorkSet of offers={}", offerIds.size());

        int i = 0;
        for (BigInteger offerId : offerIds) {
            i++;
            @SuppressWarnings("unchecked")
            List<BigInteger> walletOperations = emWrapper.getEntityManager().createNativeQuery(DUPLICATED_WO_TO_AMEND_QUERY)
                    .setParameter("offerId", offerId.longValue())
                    .getResultList();

            log.info("Start amend canceled duplicated WO for offerId={}. nbr of WO={}", offerId, walletOperations.size());

            for (BigInteger walletOperationId : walletOperations) {
                i++;
                if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR_FAST == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                    break;
                }
                amendDuplicateConsumptionUnitJobBean.execute(result, walletOperationId.longValue());
            }

        }

        return new AsyncResult<>("OK");
    }

}
