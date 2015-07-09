package org.meveo.admin.job;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.slf4j.Logger;

@Stateless
public class UnitRatedTransactionsJobBean {

	@Inject
	private Logger log;

	@Inject
	private RatedTransactionService ratedTransactionService;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result, User currentUser,Long walletOperationId ) {
		log.debug("Running for user={}, walletOperationId={}", currentUser, walletOperationId);
		
		try {
			
			ratedTransactionService.createRatedTransaction(walletOperationId,currentUser);
			result.registerSucces();
		} catch (Exception e) {
			log.error("Failed to rate transaction for wallet operation {}", walletOperationId, e);
			result.registerError(e.getMessage());
		}
	}
}
