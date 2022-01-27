package org.meveo.service.billing.invoicing.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.TypedQuery;

import org.apache.commons.collections4.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingProcessTypesEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.ThresholdSummary;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.shared.DateUtils;
import org.meveo.security.MeveoUser;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.billing.impl.BillingRunExtensionService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoicingService;

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
    BillingCycleService billingCycleService;
	
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
	public void validate(BillingRun billingRun, long nbRuns, long waitingMillis, boolean recalculateTaxes, Long jobInstanceId,
			JobExecutionResultImpl result) throws Exception {
		log.info("==================== START Processing billingRun id={} status={} ====================", billingRun.getId(), billingRun.getStatus());
		BillingCycle billingCycle = billingRun.getBillingCycle();
		if (BillingRunStatusEnum.NEW.equals(billingRun.getStatus())) {
			BillingRunSummary billingRunSummary = getBRSummury(billingRun, billingCycle);
			log.info( "==================== linkBillableEntitiesToBR -- Minimum invoicing amount is skipped ====================");
			linkBillableEntitiesToBR(billingRun, nbRuns, billingRunSummary, waitingMillis);
			billingRunExtensionService.updateBRAmounts(billingRun.getId(), billingRunSummary, BillingRunStatusEnum.PREINVOICED, new Date());
		}

		final boolean isFullAutomatic = billingRun.getProcessType() == BillingProcessTypesEnum.FULL_AUTOMATIC;
		boolean proceedToInvoiceGenerating = BillingRunStatusEnum.PREVALIDATED.equals(billingRun.getStatus()) || (BillingRunStatusEnum.NEW.equals(billingRun.getStatus())
						&& ((billingRun.getProcessType() == BillingProcessTypesEnum.AUTOMATIC || isFullAutomatic) || appProvider.isAutomaticInvoicing()));

		if (proceedToInvoiceGenerating) {
			if(recalculateTaxes) {
				log.info("==================== recalculateTaxes ====================");
			}
			invoicingService.checkDirtyTaxes(billingRun);
			log.info("==================== start invoices creation loop ====================");
			createAgregatesAndInvoice(billingRun, nbRuns, waitingMillis, jobInstanceId, isFullAutomatic, billingCycle);
			billingRunExtensionService.updateBillingRun(billingRun.getId(), null, null, BillingRunStatusEnum.INVOICES_GENERATED, null);
			billingRun = billingRunExtensionService.findById(billingRun.getId());
		}
		if (BillingRunStatusEnum.INVOICES_GENERATED.equals(billingRun.getStatus())) {
			log.info("apply threshold rules for all invoices generated with {}", billingRun);
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
				log.info("==================== assignInvoiceNumberAndIncrementBAInvoiceDates ====================");
				invoiceService.nullifyInvoiceFileNames(billingRun); // #3600
				billingRunService.assignInvoiceNumberAndIncrementBAInvoiceDates(billingRun, nbRuns, waitingMillis, jobInstanceId, result);
			}
			billingRunExtensionService.updateBillingRun(billingRun.getId(), null, null, BillingRunStatusEnum.VALIDATED, null);
		}
		log.info("==================== END OF Processing billingRun id={} status={} ====================", billingRun.getId(), billingRun.getStatus());
	}

	private void createAgregatesAndInvoice(BillingRun billingRun, long nbRuns, long waitingMillis, Long jobInstanceId,
			boolean isFullAutomatic, BillingCycle billingCycle) throws BusinessException {
		// #MEL must be used in all RT  queries!
		Date lastTransactionDate = Boolean.parseBoolean(paramBeanFactory.getInstance().getProperty("invoicing.includeEndDate", "false")) ?
			DateUtils.setDateToEndOfDay(billingRun.getLastTransactionDate()) :DateUtils.setDateToStartOfDay(billingRun.getLastTransactionDate());
		// #MEL where to put pageSise param?
		final int pageSise = MAX_READ_TO_PROCESS;
		// #MEL STEP 1: get entities to invoice having threshold per entity
		boolean thresholdPerEntityFound = false;
		final List<ThresholdSummary> parentEntitiesWithThresholdPerEntity = getEntitiesToInvoiceHavingThresholdPerEntity(billingRun);
		List<List<ThresholdSummary>> threshouldAccounts = new ArrayList<List<ThresholdSummary>>();
		if (!CollectionUtils.isEmpty(parentEntitiesWithThresholdPerEntity)) {
			thresholdPerEntityFound = true;
			threshouldAccounts = getPagesIDs(parentEntitiesWithThresholdPerEntity, nbRuns, pageSise);
			for (List<ThresholdSummary> tsList : threshouldAccounts) {
				log.info("ThresholdSummary list ============>");
				for (ThresholdSummary ts : tsList) {
					log.info("============>" + ts);
				}
				// #MEL to be continued, just logging to catch in logs in case...
				// entities = getEntitiesToInvoiceHavingParentThreshold(billingRun, pageSise,
				// index, thresholdPerEntityFound);
				// processEntitiesHavingParentThreshold(billingRun, nbRuns, waitingMillis,
				// jobInstanceId, entities, isFullAutomatic);
			}
		}
		int count = pageSise;
		int page = 0;
		List<BillingAccountDetailsItem> items = null;
		while (count >= pageSise) {
			items = getInvoicingItems(billingRun, pageSise, page, thresholdPerEntityFound);
			count = items.size();
			log.info("======== READER : {} ======== ", count);
			processEntitiesHavingNoParentThreshold(billingRun, nbRuns, waitingMillis, jobInstanceId, items, isFullAutomatic, billingCycle);
			log.info("======== {} ITEMS PROCESSED ========",count);
			page += 1;
		}
	}
	
	private List<BillingAccountDetailsItem> getInvoicingItems(BillingRun billingRun, int pageSize, int pageIndex, boolean thresholdPerEntityFound) {
		log.info("========== getInvoicingItems " + pageSize + "-" + pageIndex + " ==========");
		Date startDate = billingRun.getStartDate();
		Date endDate = billingRun.getEndDate();
		if ((startDate != null) && (endDate == null)) {
			endDate = new Date();
		}
		return billingAccountService.getInvoicingItems(billingRun, startDate, endDate, pageSize, pageIndex, thresholdPerEntityFound);
	}

	private void processEntitiesHavingNoParentThreshold(BillingRun billingRun, long nbRuns, long waitingMillis, Long jobInstanceId, List<BillingAccountDetailsItem> items, boolean isFullAutomatic, BillingCycle billingCycle) {
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

	private List<List<ThresholdSummary>> getPagesIDs(List<ThresholdSummary> parentEntitiesWithThresholdPerEntity,
			long nbRuns, int pageSise) {
		List<ThresholdSummary> finalList = new ArrayList<ThresholdSummary>();
		final Map<Long, List<ThresholdSummary>> customersMap = parentEntitiesWithThresholdPerEntity.stream()
				.collect(Collectors.groupingBy(ThresholdSummary::getCustomerId));
		for (List<ThresholdSummary> customerSummury : customersMap.values()) {
			final ThresholdSummary firstSummary = customerSummury.get(0);
			final Long customerId = firstSummary.getCustomerId();
			if (customerId != null && customerSummury.size() > 1) {
				int count = customerSummury.stream().mapToInt(ThresholdSummary::getCount).sum();
				finalList.add(new ThresholdSummary(customerId, null, count));
			} else if (customerId == null) {
				finalList.addAll(customerSummury);
			} else {
				finalList.add(firstSummary);
			}
		}
		final List<ThresholdSummary> sortedThresholdInfos = finalList.stream().sorted(Comparator.comparingInt(ThresholdSummary::getCount).reversed()).collect(Collectors.toList());
		int baCounts = 0;
		List<ThresholdSummary> thresholdSummaryList = new ArrayList<ThresholdSummary>();
		List<List<ThresholdSummary>> pagesIds = new ArrayList<List<ThresholdSummary>>();
		for (ThresholdSummary ts : sortedThresholdInfos) {
			if (thresholdSummaryList.size() < nbRuns || baCounts + ts.getCount() <= pageSise) {
				baCounts = baCounts + ts.getCount();
			} else {
				baCounts = 0;
				pagesIds.add(thresholdSummaryList);
				thresholdSummaryList = new ArrayList<ThresholdSummary>();
			}
			thresholdSummaryList.add(ts);
		}
		if (thresholdSummaryList.size() > 0) {
			pagesIds.add(thresholdSummaryList);
		}
		return pagesIds;
	}

	private List<ThresholdSummary> getEntitiesToInvoiceHavingThresholdPerEntity(BillingRun billingRun) {
		return getEntityManager().createNamedQuery("BillingAccount.findEntitiesToInvoiceHavingThresholdPerEntity",ThresholdSummary.class).setParameter("billingRunId", billingRun.getId()).getResultList();
	}

	private void linkBillableEntitiesToBR(BillingRun billingRun, long count, BillingRunSummary billingRunSummary, long waitingMillis) {
		Long min = billingRunSummary.getFirstBillingAccountId();
		Long max = billingRunSummary.getLastBillingAccountId();
		min = linkBAToBRByInterval(billingRun, min, (count > MAX_UPDATE_WITHOUT_INTERVAL ? min + MAX_UPDATE_PER_INTERVAL : max + 1));
		while (min < max) {
			min = linkBAToBRByInterval(billingRun, min, (max > min + MAX_UPDATE_PER_INTERVAL ? min + MAX_UPDATE_PER_INTERVAL : max + 1));
		}
	}

	private Long linkBAToBRByInterval(BillingRun billingRun, Long min, Long maxRT) {
		log.info("======LINKING BAs BEWEEN {} AND {} TO BR {}=====", min, maxRT, billingRun.getId());
		billingAccountService.linkBillableEntitiesToBR(billingRun, min, maxRT);
		min = maxRT;
		return min;
	}

	private BillingRunSummary getBRSummury(BillingRun billingRun, BillingCycle billingCycle) {
		Date startDate = billingRun.getStartDate();
		Date endDate = billingRun.getEndDate();
		if ((startDate != null) && (endDate == null)) {
			endDate = new Date();
		}
		if (endDate != null && startDate == null) {
			startDate = new Date(0);
		}
		String sqlName = startDate == null ? "RatedTransaction.sumBRByBA" : "RatedTransaction.sumBRByBALimitByNextInvoiceDate";
		TypedQuery<BillingRunSummary> query = getEntityManager().createNamedQuery(sqlName, BillingRunSummary.class)
				.setParameter("firstTransactionDate", new Date(0))
				.setParameter("lastTransactionDate", billingRun.getLastTransactionDate())
				.setParameter("billingCycle", billingCycle);

		if (startDate != null) {
			startDate = DateUtils.setDateToEndOfDay(startDate);
			if (Boolean.parseBoolean(paramBeanFactory.getInstance().getProperty("invoicing.includeEndDate", "false"))) {
				endDate = DateUtils.setDateToEndOfDay(endDate);
			} else {
				endDate = DateUtils.setDateToStartOfDay(endDate);
			}

			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		return query.getSingleResult();
	}

	private List<List<BillingAccountDetailsItem>> dispatchInvoicingItems(List<BillingAccountDetailsItem> items, int threads) {
		final List<List<BillingAccountDetailsItem>> result = IntStream.range(0, threads).mapToObj(ArrayList<BillingAccountDetailsItem>::new).collect(Collectors.toList());
		int[] counters = new int[threads];
		items.stream().sorted((i1, i2) -> Integer.compare(i2.getTotalRTs(), i1.getTotalRTs())).forEach(item -> dispatch(item, result, counters));
		log.info("======= DISPATCHED: {} =========", result.stream().map(x -> "" + x.size()).collect(Collectors.joining(",")));
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