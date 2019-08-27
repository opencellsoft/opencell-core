/**
 * 
 */
package org.meveo.admin.async;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.job.UnitRatedTransactionsJobBean;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.billing.impl.AggregatedWalletOperation;
import org.meveo.service.billing.impl.RatedTransactionsJobAggregationSetting;
import org.meveo.service.job.JobExecutionService;

/**
 * @author Edward P. Legaspi
 * @author anasseh
 * @lastModifiedVersion 7.0
 */
@Stateless
public class RatedTransactionAsync {

    @Inject
    private UnitRatedTransactionsJobBean unitRatedTransactionsJobBean;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private CurrentUserProvider currentUserProvider;

    /**
     * Rate wallet operations, one operation at a time in a separate transaction.
     * 
     * @param walletOperations A list of wallet operations to rate
     * @param result Job execution result
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     * @return Future String
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<WalletOperation> walletOperations, JobExecutionResultImpl result, MeveoUser lastCurrentUser) {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);
        int i = 0;
        for (WalletOperation walletOperation : walletOperations) {
            i++;
            if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR_FAST == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                break;
            }
            unitRatedTransactionsJobBean.execute(result, walletOperation);
        }
        return new AsyncResult<>("OK");
    }

    /**
     * Rate aggregated wallet operations, one operation at a time in a separate transaction.
     * 
     * @param nextWorkSet A list of aggregated wallet operation.
     * @param result Job execution result
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     * @param aggregationSettings the settings to aggregate the wallet operations
     * @return Future String
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<AggregatedWalletOperation> nextWorkSet, JobExecutionResultImpl result, MeveoUser lastCurrentUser,
            RatedTransactionsJobAggregationSetting aggregationSettings, Date invoicingDate) {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        int i = 0;
        for (AggregatedWalletOperation aggregatedWo : nextWorkSet) {
            i++;
            if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR_FAST == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                break;
            }
            unitRatedTransactionsJobBean.execute(result, aggregatedWo, aggregationSettings, invoicingDate);
        }
        return new AsyncResult<>("OK");
    }

}
