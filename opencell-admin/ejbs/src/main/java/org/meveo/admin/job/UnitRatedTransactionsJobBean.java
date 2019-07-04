package org.meveo.admin.job;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.AggregatedWalletOperation;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.RatedTransactionsJobAggregationSetting;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@Stateless
public class UnitRatedTransactionsJobBean {

	@Inject
	private Logger log;

	@Inject
	private RatedTransactionService ratedTransactionService;

    @JpaAmpNewTx
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result,Long walletOperationId ) {
		log.debug("Running with walletOperationId={}", walletOperationId);
		try {
			ratedTransactionService.createRatedTransaction(walletOperationId);
			result.registerSucces();
		
		} catch (Exception e) {
			log.error("Failed to rate transaction for wallet operation {}", walletOperationId, e);
			result.registerError(e.getMessage());
		}
	}

    @JpaAmpNewTx
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result, AggregatedWalletOperation aggregatedWo, RatedTransactionsJobAggregationSetting aggregationSettings, Date invoicingDate) {
		log.debug("Running with aggregatedWo={}", aggregatedWo);
		try {
			ratedTransactionService.createRatedTransaction(aggregatedWo, aggregationSettings, invoicingDate);
			result.registerSucces();
		
		} catch (Exception e) {
			log.error("Failed to rate transaction for aggregatedWo {}", aggregatedWo, e);
			result.registerError(e.getMessage());
		}
	}
}
