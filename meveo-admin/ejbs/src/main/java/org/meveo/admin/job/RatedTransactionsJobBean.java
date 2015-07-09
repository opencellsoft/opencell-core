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
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.slf4j.Logger;

@Stateless
public class RatedTransactionsJobBean {

	@Inject
	private Logger log;

	@Inject
	private WalletOperationService walletOperationService;

	@Inject
	private RatedTransactionService ratedTransactionService;

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public JobExecutionResult execute(JobExecutionResultImpl result, User currentUser) {
		Provider provider = currentUser.getProvider();

		try {
			List<WalletOperation> walletOperations = walletOperationService.listToInvoice(
					new Date(), provider);

			log.info("WalletOperations to convert into rateTransactions={}", walletOperations.size());

			for (WalletOperation walletOperation : walletOperations) {
				try {
					ratedTransactionService.createRatedTransaction(walletOperation.getId(), currentUser);
			
				} catch (Exception e) {
					log.error("Failed to rate transaction for wallet operation {}", walletOperation.getId(), e);
					result.registerError(e.getMessage());
				}
			}
		} catch (Exception e) {
		    log.error("Failed to rate transactions", e);
		}

		result.close("");

		return result;
	}

}
