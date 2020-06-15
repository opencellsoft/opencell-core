/**
 * 
 */
package org.meveo.admin.async;

import org.meveo.admin.job.OfferPoolRatingUnitJobBean;
import org.meveo.admin.job.UnitRatedTransactionsJobBean;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.billing.impl.AggregatedWalletOperation;
import org.meveo.service.billing.impl.RatedTransactionsJobAggregationSetting;
import org.meveo.service.job.JobExecutionService;

import javax.ejb.*;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Mounir BOUKAYOUA
 */
@Stateless
public class OfferPoolRatingAsync {

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private CurrentUserProvider currentUserProvider;

    private OfferPoolRatingUnitJobBean offerPoolRatingUnitJobBean;

    /**
     * Rate wallet operations, one operation at a time in a separate transaction.
     * 
     * @param walletOperations A list of wallet operation ids to rate
     * @param result Job execution result
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     * @return Future String
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<BigInteger> walletOperations, JobExecutionResultImpl result, MeveoUser lastCurrentUser) {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);
        int i = 0;
        for (BigInteger walletOperationId : walletOperations) {
            i++;
            if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR_FAST == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                break;
            }
            offerPoolRatingUnitJobBean.execute(result, walletOperationId.longValue());
        }
        return new AsyncResult<>("OK");
    }

}
