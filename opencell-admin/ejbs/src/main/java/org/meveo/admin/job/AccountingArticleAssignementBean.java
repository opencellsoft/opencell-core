package org.meveo.admin.job;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.model.IBillableEntity;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.RatedTransactionService;

@Stateless
public class AccountingArticleAssignementBean {

	@Inject
	private RatedTransactionService ratedTransactionService;

	@Inject
	private IteratorBasedJobProcessing iteratorBasedJobProcessing;

	/**
	 * @param waitingMillis
	 * @param waitingMillis
	 * @param jobInstance
	 * @param nbRuns
	 * @param jobInstance
	 * @param nbRuns
	 */
	public void assignAccountingArticleIfMissingInRTs(JobExecutionResultImpl result,
			List<? extends IBillableEntity> billableEntities, Long maxInvoiceLinesPerTransaction, Long waitingMillis,
			JobInstance jobInstance, Long nbRuns) {
		BiConsumer<IBillableEntity, JobExecutionResultImpl> task = (billableEntity,
				jobResult) -> updateRTAccountingArticle(result, billableEntity, maxInvoiceLinesPerTransaction);
		iteratorBasedJobProcessing.processItems(result,
				new SynchronizedIterator<>((Collection<IBillableEntity>) billableEntities), task, null, null, nbRuns,
				waitingMillis, true, jobInstance.getJobSpeed(), true);
	}

	/**
	 * @param result
	 * @param billableEntity
	 * @param maxInvoiceLinesPerTransaction
	 * @return
	 */
	private void updateRTAccountingArticle(JobExecutionResultImpl result, IBillableEntity billableEntity,
			Long maxInvoiceLinesPerTransaction) {
		if (maxInvoiceLinesPerTransaction == null || maxInvoiceLinesPerTransaction < 1) {
			ratedTransactionService.calculateAccountingArticle(result, billableEntity, null, null);
		} else {
			int index = 0;
			int count = maxInvoiceLinesPerTransaction.intValue();
			while (count >= maxInvoiceLinesPerTransaction) {
				count = ratedTransactionService.calculateAccountingArticle(result, billableEntity,
						maxInvoiceLinesPerTransaction.intValue(), index++);
			}
		}
	}

}
