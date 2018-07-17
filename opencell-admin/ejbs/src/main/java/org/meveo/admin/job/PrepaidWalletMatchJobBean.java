package org.meveo.admin.job;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.OneShotChargeInstanceService;
import org.meveo.service.billing.impl.WalletService;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;

@Stateless
public class PrepaidWalletMatchJobBean {

    @Inject
    private Logger log;

    @Inject
    private WalletService walletService;

    @Inject
    private OneShotChargeInstanceService oneShotChargeInstanceService;

    @Inject
    private JobExecutionService jobExecutionService;

    @JpaAmpNewTx
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(String matchingChargeCode, JobExecutionResultImpl result) {
        log.debug("Running matchingChargeCode={}", matchingChargeCode);

        try {
            List<WalletInstance> wallets = walletService.getWalletsToMatch(new Date());

            log.debug("wallets to match {}", wallets.size());
            result.setNbItemsToProcess(wallets.size());
            for (WalletInstance wallet : wallets) {
                if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                    break;
                }
                log.debug("match wallet={}", wallet.getId());
                try {
                    oneShotChargeInstanceService.matchPrepaidWallet(wallet, matchingChargeCode);
                    result.registerSucces();
                } catch (Exception e) {
                    log.error("Failed to match prepaid wallet {}", wallet.getId(), e);
                    result.registerError(e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Failed to match prepaid wallet ", e);
        }
    }
}