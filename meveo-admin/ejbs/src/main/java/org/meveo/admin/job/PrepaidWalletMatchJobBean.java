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
import org.meveo.model.admin.User;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.OneShotChargeInstanceService;
import org.meveo.service.billing.impl.WalletService;
import org.slf4j.Logger;

@Stateless
public class PrepaidWalletMatchJobBean {

	@Inject
	private Logger log;

	@Inject
	private WalletService walletService;

	@Inject
	OneShotChargeInstanceService oneShotChargeInstanceService;

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(String matchingChargeCode, JobExecutionResultImpl result, User currentUser) {
		try {
			List<WalletInstance> wallets = walletService.getWalletsToMatch(new Date());

			log.debug("wallets to match {}", wallets.size());
			result.setNbItemsToProcess(wallets.size());
			for (WalletInstance wallet : wallets) {
				log.debug("match wallet={}", wallet.getId());

				try {
					oneShotChargeInstanceService.matchPrepaidWallet(wallet, matchingChargeCode, currentUser);
					result.registerSucces();
				} catch (Exception e) {
					log.error(e.getMessage());
					result.registerError(e.getMessage());
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

}
