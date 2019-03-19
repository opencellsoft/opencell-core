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
import org.meveo.service.billing.impl.AggregatedWalletOperation;
import org.meveo.service.billing.impl.RatedTransactionsJobAggregationSetting;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.billing.impl.RatedTransactionsJobAggregationSetting.AggregationLevelEnum;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
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

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
		log.debug("Running for with parameter={}", jobInstance.getParametres());

		try {
			RatedTransactionsJobAggregationSetting aggregationSetting = new RatedTransactionsJobAggregationSetting();
			Long nbRuns = 1L;
			Long waitingMillis = 0L;
			try {
				nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns");
				waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis");
				if (nbRuns == -1) {
					nbRuns = (long) Runtime.getRuntime().availableProcessors();
				}

				aggregationSetting.setEnable((boolean) this.getParamOrCFValue(jobInstance, "activateAggregation"));
				aggregationSetting
						.setAggregateGlobally((boolean) this.getParamOrCFValue(jobInstance, "globalAggregation"));
				aggregationSetting.setAggregateByDay((boolean) this.getParamOrCFValue(jobInstance, "aggregateByDay"));
				aggregationSetting.setAggregationLevel(AggregationLevelEnum
						.valueOf(((String) this.getParamOrCFValue(jobInstance, "aggregationLevel"))));
				try {
					aggregationSetting
							.setAggregateByOrder((boolean) this.getParamOrCFValue(jobInstance, "aggregateByOrder"));

				} catch (NullPointerException e) {
				}
				try {
					aggregationSetting
							.setAggregateByParam1((boolean) this.getParamOrCFValue(jobInstance, "aggregateByParam1"));
				} catch (NullPointerException e) {
				}
				try {
					aggregationSetting
							.setAggregateByParam2((boolean) this.getParamOrCFValue(jobInstance, "aggregateByParam2"));
				} catch (NullPointerException e) {
				}
				try {
					aggregationSetting
							.setAggregateByParam3((boolean) this.getParamOrCFValue(jobInstance, "aggregateByParam3"));
				} catch (NullPointerException e) {
				}
				try {
					aggregationSetting.setAggregateByExtraParam(
							(boolean) this.getParamOrCFValue(jobInstance, "aggregateByExtraParam"));
				} catch (NullPointerException e) {
				}
			} catch (Exception e) {
				nbRuns = 1L;
				waitingMillis = 0L;
				log.warn("Cant get customFields for {} with message {}", jobInstance.getJobTemplate(), e.getMessage());
			}

			if (aggregationSetting.isEnable()) {
				executeWithAggregation(result, nbRuns, waitingMillis, aggregationSetting);

			} else {
				executeWithoutAggregation(result, nbRuns, waitingMillis);
			}

		} catch (Exception e) {
			log.error("Failed to rate transactions", e);
		}
	}

	private void executeWithoutAggregation(JobExecutionResultImpl result, Long nbRuns, Long waitingMillis)
			throws Exception {
		List<Long> walletOperationIds = walletOperationService.listToInvoiceIds(new Date());
		log.info("WalletOperations to convert into rateTransactions={}", walletOperationIds.size());
		result.setNbItemsToProcess(walletOperationIds.size());

		SubListCreator<Long> subListCreator = new SubListCreator<>(walletOperationIds, nbRuns.intValue());
		List<Future<String>> asyncReturns = new ArrayList<>();
		MeveoUser lastCurrentUser = currentUser.unProxy();
		while (subListCreator.isHasNext()) {
			asyncReturns.add(ratedTransactionAsync.launchAndForget((List<Long>) subListCreator.getNextWorkSet(), result,
					lastCurrentUser));
			try {
				Thread.sleep(waitingMillis.longValue());

			} catch (InterruptedException e) {
				log.error("", e);
			}
		}

		for (Future<String> futureItsNow : asyncReturns) {
			futureItsNow.get();
		}
	}

	private void executeWithAggregation(JobExecutionResultImpl result, Long nbRuns, Long waitingMillis,
			RatedTransactionsJobAggregationSetting aggregationSetting) throws Exception {
		Date invoicingDate = new Date();
		List<AggregatedWalletOperation> aggregatedWo = walletOperationService.listToInvoiceIdsWithGrouping(invoicingDate,
				aggregationSetting);

		if (aggregatedWo == null || aggregatedWo.isEmpty()) {
			return;
		}

		log.info("Aggregated walletOperations to convert into rateTransactions={}", aggregatedWo.size());
		result.setNbItemsToProcess(aggregatedWo.size());

		SubListCreator<AggregatedWalletOperation> subListCreator = new SubListCreator<>(aggregatedWo,
				nbRuns.intValue());
		List<Future<String>> asyncReturns = new ArrayList<>();
		MeveoUser lastCurrentUser = currentUser.unProxy();
		while (subListCreator.isHasNext()) {
			asyncReturns.add(
					ratedTransactionAsync.launchAndForget(
							(List<AggregatedWalletOperation>) subListCreator.getNextWorkSet(), result, lastCurrentUser,
							aggregationSetting, invoicingDate));
			try {
				Thread.sleep(waitingMillis.longValue());

			} catch (InterruptedException e) {
				log.error("", e);
			}
		}

		for (Future<String> futureItsNow : asyncReturns) {
			futureItsNow.get();
		}
		
		walletOperationService.updateAggregatedWalletOperations(invoicingDate);
	}

}
