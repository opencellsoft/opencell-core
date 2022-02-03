package org.meveo.service.billing.invoicing.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.TypedQuery;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingEntityTypeEnum;
import org.meveo.model.billing.BillingProcessTypesEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.ThresholdOptionsEnum;
import org.meveo.model.billing.threshold.ThresholdLevelEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.shared.DateUtils;
import org.meveo.security.MeveoUser;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingRunExtensionService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.RejectedBillingAccountService;

@Stateless
public class BillingService extends PersistenceService<BillingRun> {

	// all may be configured as job CF
	private static final int MAX_READ_TO_PROCESS = 30000;
	private static final int MAX_UPDATE_PER_INTERVAL = 500000;
	private static final int MAX_UPDATE_WITHOUT_INTERVAL = 1000000;

	@Inject
	private BillingAccountService billingAccountService;

	@Inject
	private InvoiceService invoiceService;

	@Inject
	private BillingRunExtensionService billingRunExtensionService;

	@Inject
	private InvoicingService invoicingService;
	
	@Inject
	private BillingRunService billingRunService;
	
    @Inject
	private RejectedBillingAccountService rejectedBillingAccountService;
	
	/**
	 * Invoicing process for the billingRun, launched by invoicingJob.
	 *
	 * @param billingRun    the billing run to process
	 * @param nbRuns        the nb runs
	 * @param waitingMillis the waiting millis
	 * @param expectMassRTsProcessing 
	 * @param recalculateTaxes 
	 * @param jobInstanceId the job instance
	 * @param result        the Job execution result
	 * @throws Exception the exception
	 */
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void validate(BillingRun billingRun, long nbRuns, long waitingMillis, boolean recalculateTaxes, Long jobInstanceId, JobExecutionResultImpl result) throws Exception {
		log.info("========================= START Processing billingRun id={} status={} =========================", billingRun.getId(), billingRun.getStatus());
		Date lastTransactionDate = Boolean.parseBoolean(paramBeanFactory.getInstance().getProperty("invoicing.includeEndDate", "false")) ?
				DateUtils.setDateToEndOfDay(billingRun.getLastTransactionDate()) :DateUtils.setDateToStartOfDay(billingRun.getLastTransactionDate());
		Date[] nextInoiceDateLimits = getNextInvoiceDateStartAndEnd(billingRun);
		BillingCycle billingCycle = billingRun.getBillingCycle();
		if(!BillingEntityTypeEnum.BILLINGACCOUNT.equals(billingCycle.getType())) {
			throw new BusinessException(billingCycle.getType()+" billingCycles are not yet managed, only BILLINGACCOUNT BCs may be processed by this job");
		}
		
		if (BillingRunStatusEnum.NEW.equals(billingRun.getStatus())) {
			BillingRunSummary billingRunSummary = getBRSummury(billingRun, billingCycle, nextInoiceDateLimits);
			log.info( "========== linkBillableEntitiesToBR -- Minimum invoicing amount is skipped ==========");
			linkBillableEntitiesToBR(billingRun, billingRunSummary, nextInoiceDateLimits, lastTransactionDate);
			billingRunExtensionService.updateBRAmounts(billingRun.getId(), billingRunSummary, BillingRunStatusEnum.PREINVOICED, new Date());
		}

		final boolean isFullAutomatic = billingRun.getProcessType() == BillingProcessTypesEnum.FULL_AUTOMATIC;
		boolean proceedToInvoiceGenerating = BillingRunStatusEnum.PREVALIDATED.equals(billingRun.getStatus()) || (BillingRunStatusEnum.NEW.equals(billingRun.getStatus())
						&& ((billingRun.getProcessType() == BillingProcessTypesEnum.AUTOMATIC || isFullAutomatic) || appProvider.isAutomaticInvoicing()));

		if (proceedToInvoiceGenerating) {
			if(recalculateTaxes) {
				log.info("========== recalculateTaxes ==========");
				invoicingService.checkDirtyTaxes(billingRun);
			}
			final boolean checkThresholdByQueryBeforeInvoicing = !ThresholdLevelEnum.INVOICE.equals(billingCycle.getThresholdLevel()) && (ThresholdOptionsEnum.BEFORE_DISCOUNT.equals(billingCycle.getCheckThreshold()) || ThresholdOptionsEnum.POSITIVE_RT.equals(billingCycle.getCheckThreshold()));
			if(checkThresholdByQueryBeforeInvoicing){
				log.info("========== CHECK BA REJECTS BY THRESHOLD {}==========",billingCycle.getCheckThreshold());
				rejectedBillingAccountService.createRejectedBAsForThreshold(billingRun, billingCycle,lastTransactionDate);
				billingAccountService.unlinkRejectedBAs(billingRun.getId());
			}
			createAgregatesAndInvoice(billingRun, nbRuns, waitingMillis, jobInstanceId, isFullAutomatic, billingCycle, lastTransactionDate);
			billingRunExtensionService.updateBillingRun(billingRun.getId(), null, null, BillingRunStatusEnum.INVOICES_GENERATED, null);
			billingRun = billingRunExtensionService.findById(billingRun.getId());
		}
		if (BillingRunStatusEnum.INVOICES_GENERATED.equals(billingRun.getStatus())) {
			billingRunExtensionService.updateBillingRun(billingRun.getId(), null, null, BillingRunStatusEnum.POSTINVOICED, null);
			if (isFullAutomatic) {
				billingRun = billingRunExtensionService.findById(billingRun.getId());
			}
		}
		if (BillingRunStatusEnum.POSTINVOICED.equals(billingRun.getStatus()) && isFullAutomatic) {
			billingRunExtensionService.updateBillingRun(billingRun.getId(), null, null, BillingRunStatusEnum.POSTVALIDATED, null);
			billingRun = billingRunExtensionService.findById(billingRun.getId());
		}

		if (BillingRunStatusEnum.POSTVALIDATED.equals(billingRun.getStatus())) {
			if (!isFullAutomatic) {
				log.info("========== assignInvoiceNumberAndIncrementBAInvoiceDates ==========");
				invoiceService.nullifyInvoiceFileNames(billingRun); // #3600
				billingRunService.assignInvoiceNumberAndIncrementBAInvoiceDates(billingRun, nbRuns, waitingMillis, jobInstanceId, result);
			}
			billingRunExtensionService.updateBillingRun(billingRun.getId(), null, null, BillingRunStatusEnum.VALIDATED, null);
		}
		log.info("========================= END OF Processing billingRun id={} status={} =========================", billingRun.getId(), billingRun.getStatus());
	}

	private void createAgregatesAndInvoice(BillingRun billingRun, long nbRuns, long waitingMillis, Long jobInstanceId,
			boolean isFullAutomatic, BillingCycle billingCycle, Date lastTransactionDate) throws BusinessException {
		log.info("========== start invoices creation loop ==========");
		// #MEL where to put pageSise param?
		final int pageSize = MAX_READ_TO_PROCESS;
		invoicingService.initCommonData();
		int count = pageSize;
		int page = 0;
		List<BillingAccountDetailsItem> items = null;
		while (count >= pageSize) {
			log.info("===== ITEMS READER LOOP " + pageSize + "-" + page + " =====");
			items = billingAccountService.getInvoicingItems(billingRun, billingCycle, lastTransactionDate, pageSize, page);
			count = items.size();
			log.info("===== {} ITEMS TO BE PROCESSED =====", count);
			processEntities(billingRun, nbRuns, waitingMillis, jobInstanceId, items, isFullAutomatic, billingCycle, lastTransactionDate);
			log.info("===== {} ITEMS PROCESSED =====",count);
			page += 1;
		}
		invoicingService.removeCommonData();
	}

	private void processEntities(BillingRun billingRun, long nbRuns, long waitingMillis, Long jobInstanceId, List<BillingAccountDetailsItem> items, boolean isFullAutomatic, BillingCycle billingCycle, Date lastTransactionDate) {
		List<Future<String>> asyncReturns = new ArrayList<Future<String>>();
		MeveoUser lastCurrentUser = currentUser.unProxy();
		final List<List<BillingAccountDetailsItem>> dispatchedInvoicingItems = dispatchInvoicingItems(items, (int) nbRuns);
		for (List<BillingAccountDetailsItem> dispatchedInvoicingItem : dispatchedInvoicingItems) {
			asyncReturns.add(invoicingService.createAgregatesAndInvoiceForJob(billingRun, billingCycle, dispatchedInvoicingItem, jobInstanceId, lastCurrentUser, isFullAutomatic));
			try {
				Thread.sleep(waitingMillis);
			} catch (InterruptedException e) {
				log.error("Failed to create agregates and invoice waiting for thread", e);
				throw new BusinessException(e);
			}
		}
		for (Future<String> futureItsNow : asyncReturns) {
			try {
				futureItsNow.get();
			} catch (InterruptedException | ExecutionException e) {
				log.error("Failed to create agregates and invoice getting future", e);
				throw new BusinessException(e);
			}
		}
	}

	private void linkBillableEntitiesToBR(BillingRun billingRun, BillingRunSummary billingRunSummary, Date[] nextInoiceDateLimits, Date lastTransactionDate) {
		final Long count = billingRunSummary.getBillingAccountsCount();
		if(count!=null&& count>0) {
			Long min = billingRunSummary.getFirstBillingAccountId();
			Long max = billingRunSummary.getLastBillingAccountId();
			min = linkBAToBRByInterval(billingRun, lastTransactionDate,nextInoiceDateLimits, min, (count > MAX_UPDATE_WITHOUT_INTERVAL ? min + MAX_UPDATE_PER_INTERVAL : max + 1));
			while (min < max) {
				min = linkBAToBRByInterval(billingRun, lastTransactionDate, nextInoiceDateLimits, min, (max > min + MAX_UPDATE_PER_INTERVAL ? min + MAX_UPDATE_PER_INTERVAL : max + 1));
			}
		}
	}

	private Long linkBAToBRByInterval(BillingRun billingRun, Date lastTransactionDate, Date[] nextInoiceDateLimits, Long min, Long maxRT) {
		billingAccountService.linkBillableEntitiesToBR(billingRun, lastTransactionDate, nextInoiceDateLimits, min, maxRT);
		min = maxRT;
		return min;
	}

	private BillingRunSummary getBRSummury(BillingRun billingRun, BillingCycle billingCycle, Date[] nextInoiceDateLimits) {
		String sqlName = nextInoiceDateLimits == null ? "RatedTransaction.sumBRByBA" : "RatedTransaction.sumBRByBALimitByNextInvoiceDate";
		TypedQuery<BillingRunSummary> query = getEntityManager().createNamedQuery(sqlName, BillingRunSummary.class)
				.setParameter("firstTransactionDate", new Date(0)).setParameter("lastTransactionDate", billingRun.getLastTransactionDate()).setParameter("billingCycle", billingCycle);
		if (nextInoiceDateLimits != null) {
			query.setParameter("startDate", nextInoiceDateLimits[0]).setParameter("endDate", nextInoiceDateLimits[1]);
		}
		return query.getSingleResult();
	}

	private Date[] getNextInvoiceDateStartAndEnd(BillingRun billingRun) {
		Date startDate = billingRun.getStartDate();
		Date endDate = billingRun.getEndDate();
		if (endDate == null && startDate == null) {
			return null;
		}
		if (startDate == null) {
			startDate = new Date(0);
		}
		if(endDate == null) {
			endDate = new Date();
		}
		startDate = DateUtils.setDateToEndOfDay(startDate);
		if (Boolean.parseBoolean(paramBeanFactory.getInstance().getProperty("invoicing.includeEndDate", "false"))) {
			endDate = DateUtils.setDateToEndOfDay(endDate);
		} else {
			endDate = DateUtils.setDateToStartOfDay(endDate);
		}
		Date[] nextInoiceDateLimits = {startDate, endDate};
		return nextInoiceDateLimits;
	}

	private List<List<BillingAccountDetailsItem>> dispatchInvoicingItems(List<BillingAccountDetailsItem> items, int threads) {
		final List<List<BillingAccountDetailsItem>> result = IntStream.range(0, threads).mapToObj(ArrayList<BillingAccountDetailsItem>::new).collect(Collectors.toList());
		int[] counters = new int[threads];
		items.stream().sorted((i1, i2) -> Integer.compare(i2.getTotalRTs(), i1.getTotalRTs())).forEach(item -> dispatch(item, result, counters));
		log.info("===== DISPATCHED: {}=====", result.stream().map(x -> "" + x.size()).collect(Collectors.joining(",")));
		return result;
	}

	private void dispatch(BillingAccountDetailsItem item, List<List<BillingAccountDetailsItem>> result, int[] counters) {
		int minIndex = 0;
		int min = counters[0];
		for (int i = 1; i < counters.length; i++) {
			if (counters[i] <= min) {
				min = counters[i];
				minIndex = i;
			}
		}
		result.get(minIndex).add(item);
		counters[minIndex] = min + item.getTotalRTs() + 100;// 100 added to make some equilibre with other invoicing operations
	}
}