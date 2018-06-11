package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.RatedTransactionAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.WalletOperationService;
import org.slf4j.Logger;

@Stateless
public class RatedTransactionsJobBean extends BaseJobBean {

    @Inject
    private Logger log;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private RatedTransactionAsync ratedTransactionAsync;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @SuppressWarnings("unchecked")
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running for with parameter={}", jobInstance.getParametres());

        try {
            List<Long> walletOperationIds = walletOperationService.listToInvoiceIds(new Date());
            log.info("WalletOperations to convert into rateTransactions={}", walletOperationIds.size());
            result.setNbItemsToProcess(walletOperationIds.size());
            Long nbRuns = new Long(1);
            Long waitingMillis = new Long(0);
            try {
                nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns");
                waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis");
                if (nbRuns == -1) {
                    nbRuns = (long) Runtime.getRuntime().availableProcessors();
                }
            } catch (Exception e) {
                nbRuns = new Long(1);
                waitingMillis = new Long(0);
                log.warn("Cant get customFields for " + jobInstance.getJobTemplate(), e.getMessage());
            }

            SubListCreator subListCreator = new SubListCreator(walletOperationIds, nbRuns.intValue());
            List<Future<String>> asyncReturns = new ArrayList<Future<String>>();
            MeveoUser lastCurrentUser = currentUser.unProxy();
            while (subListCreator.isHasNext()) {
                asyncReturns.add(ratedTransactionAsync.launchAndForget((List<Long>) subListCreator.getNextWorkSet(), result, lastCurrentUser));
                try {
                    Thread.sleep(waitingMillis.longValue());
                } catch (InterruptedException e) {
                    log.error("", e);
                }
            }
            for (Future<String> futureItsNow : asyncReturns) {
                futureItsNow.get();
            }
        } catch (Exception e) {
            log.error("Failed to rate transactions", e);
        }
    }

}
