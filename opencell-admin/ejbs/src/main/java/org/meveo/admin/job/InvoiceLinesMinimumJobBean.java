package org.meveo.admin.job;

import static java.lang.String.format;
import static org.meveo.model.billing.BillingRunStatusEnum.INVOICE_LINES_CREATED;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.apache.commons.collections.map.HashedMap;
import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceLineService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

@Stateless
public class InvoiceLinesMinimumJobBean extends BaseJobBean {

	@Inject
	private Logger log;

	@Inject
	private BillingRunService billingRunService;

	@Inject
	private InvoiceLineService invoiceLinesService;

	@Inject
	private IteratorBasedJobProcessing iteratorBasedJobProcessing;

	@Inject
	@ApplicationProvider
	protected Provider appProvider;

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
		log.debug("Running for with parameter={}", jobInstance.getParametres());
		try {
			Map<String, Object> filters = new HashedMap();
			filters.put("status", INVOICE_LINES_CREATED);
			filters.put("disabled", false);
			List<BillingRun> billingRuns = billingRunService.list(new PaginationConfiguration(filters));
			if (billingRuns != null && !billingRuns.isEmpty()) {
				for (BillingRun billingRun : billingRuns) {
					createMinimumsForBillingRunLevelByLevel(billingRun, result, jobInstance);
				}
			}
		} catch (BusinessException exception) {
			result.registerError(exception.getMessage());
			log.error(format("Failed to run invoice lines job: %s", exception));
		}
	}

	public void createMinimumsForBillingRunLevelByLevel(BillingRun billingRun, JobExecutionResultImpl result, JobInstance jobInstance) {
		AccountingArticle defaultMinAccountingArticle = invoiceLinesService.getDefaultAccountingArticle();
		Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
		if (nbRuns == -1) {
			nbRuns = (long) Runtime.getRuntime().availableProcessors();
		}
		
		createMinInvoicLine(result, jobInstance, billingRun, invoiceLinesService.findMinimumsTocheck(billingRun, " join il.serviceInstance mt "), defaultMinAccountingArticle, nbRuns, nbRuns);
		createMinInvoicLine(result, jobInstance, billingRun, invoiceLinesService.findMinimumsTocheck(billingRun, " join il.subscription mt "), defaultMinAccountingArticle, nbRuns, nbRuns);
		createMinInvoicLine(result, jobInstance, billingRun, invoiceLinesService.findMinimumsTocheck(billingRun, " join il.userAccount mt "), defaultMinAccountingArticle, nbRuns, nbRuns);
		createMinInvoicLine(result, jobInstance, billingRun, invoiceLinesService.findMinimumsTocheck(billingRun, " join il.billingAccount mt "), defaultMinAccountingArticle, nbRuns, nbRuns);
		createMinInvoicLine(result, jobInstance, billingRun, invoiceLinesService.findMinimumsTocheck(billingRun, " join il.billingAccount ba join ba.customerAccount mt "), defaultMinAccountingArticle, nbRuns, nbRuns);
		createMinInvoicLine(result, jobInstance, billingRun, invoiceLinesService.findMinimumsTocheck(billingRun, " join il.billingAccount ba join ba.customerAccount ca join ca.customer mt "), defaultMinAccountingArticle, nbRuns, nbRuns);
	}

	private void createMinInvoicLine(JobExecutionResultImpl result, JobInstance jobInstance, BillingRun billingRun, List<Object[]> minimumForServices, AccountingArticle defaultMinAccountingArticle, Long waitingMillis, Long nbRuns) {
		BiConsumer<Object[], JobExecutionResultImpl> task = (minimumForService, jobResult) -> invoiceLinesService .createMinInvoiceLine(billingRun, defaultMinAccountingArticle, minimumForService);
		iteratorBasedJobProcessing.processItems(result, new SynchronizedIterator((Collection<Object[]>) minimumForServices), task, null, null, nbRuns, waitingMillis, true, jobInstance.getJobSpeed(), true);
	}

}