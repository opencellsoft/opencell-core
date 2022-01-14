package org.meveo.service.billing.invoicing.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.TypedQuery;

import org.apache.commons.collections4.CollectionUtils;
import org.meveo.admin.async.AmountsToInvoice;
import org.meveo.admin.async.InvoicingAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.IBillableEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingEntityTypeEnum;
import org.meveo.model.billing.BillingProcessTypesEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.InvoiceSequence;
import org.meveo.model.billing.MinAmountForAccounts;
import org.meveo.model.billing.ThresholdSummary;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.shared.DateUtils;
import org.meveo.security.MeveoUser;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingRunExtensionService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoicesToNumberInfo;
import org.meveo.service.billing.impl.InvoicingService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.ServiceSingleton;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.order.OrderService;

@Stateless
public class BillingService extends PersistenceService<BillingRun> {

	/**
	 * The billing account service.
	 */
	@Inject
	private BillingAccountService billingAccountService;

	/**
	 * The rated transaction service.
	 */
	@Inject
	private RatedTransactionService ratedTransactionService;

	/**
	 * The invoicing async.
	 */
	@Inject
	private InvoicingAsync invoicingAsync;

	/**
	 * The invoice service.
	 */
	@Inject
	private InvoiceService invoiceService;

	/**
	 * The billing run extension service.
	 */
	@Inject
	private BillingRunExtensionService billingRunExtensionService;

	/**
	 * The service singleton.
	 */
	@Inject
	private ServiceSingleton serviceSingleton;

	@Inject
	private SubscriptionService subscriptionService;

	@Inject
	private OrderService orderService;

	@Inject
	private InvoicingService invoicingService;

	/**
	 * Round.
	 *
	 * @param amount  the amount
	 * @param decimal the decimal
	 * @return the big decimal
	 */
	public static BigDecimal round(BigDecimal amount, int decimal) {
		if (amount == null) {
			return null;
		}
		amount = amount.setScale(decimal, RoundingMode.HALF_UP);

		return amount;
	}

	/**
	 * Gets entities.
	 *
	 * @param billingRun the billing run
	 * @param pageIndex
	 * @param pageSize
	 * @return the entity objects
	 */
	private List<? extends IBillableEntity> getEntitiesToInvoice(BillingRun billingRun, int pageSize, int pageIndex,
			boolean thresholdPerEntityFound) {
		log.info("getInvoicingItems ====== > " + pageSize + "-" + pageIndex);
		BillingCycle billingCycle = billingRun.getBillingCycle();

		if (billingCycle != null) {

			Date startDate = billingRun.getStartDate();
			Date endDate = billingRun.getEndDate();
			if ((startDate != null) && (endDate == null)) {
				endDate = new Date();
			}
			if (billingCycle.getType() == BillingEntityTypeEnum.SUBSCRIPTION) {
				return subscriptionService.findSubscriptions(billingCycle, startDate, endDate);
			}
			if (billingCycle.getType() == BillingEntityTypeEnum.ORDER) {
				return orderService.findOrders(billingCycle, startDate, endDate);
			}
			return billingAccountService.findAccountsToInvoice(billingRun, startDate, endDate, pageSize, pageIndex,
					thresholdPerEntityFound);

		} else {

			List<BillingAccount> result = new ArrayList<BillingAccount>();
			String[] baIds = billingRun.getSelectedBillingAccounts().split(",");

			for (String id : Arrays.asList(baIds)) {
				// Long baId = Long.valueOf(id);
				// result.add(baId);
				result.add(billingAccountService.findById(new Long(id)));
			}
			return result;
		}
	}
	
	private List<BillingAccountDetailsItem> getInvoicingItems(BillingRun billingRun, int pageSize, int pageIndex,
			boolean thresholdPerEntityFound, boolean expectMassRtsPerInvoice) {
		log.info("getInvoicingItems ====== > " + pageSize + "-" + pageIndex);
		BillingCycle billingCycle = billingRun.getBillingCycle();

		//if (billingCycle != null) {

			Date startDate = billingRun.getStartDate();
			Date endDate = billingRun.getEndDate();
			if ((startDate != null) && (endDate == null)) {
				endDate = new Date();
			}
			/*
			if (billingCycle.getType() == BillingEntityTypeEnum.SUBSCRIPTION) {
				return subscriptionService.findSubscriptions(billingCycle, startDate, endDate);
			}
			if (billingCycle.getType() == BillingEntityTypeEnum.ORDER) {
				return orderService.findOrders(billingCycle, startDate, endDate);
			}*/
			return billingAccountService.getInvoicingItems(billingRun, startDate, endDate, pageSize, pageIndex, thresholdPerEntityFound, expectMassRtsPerInvoice);
	}

	/**
	 * Creates the agregates and invoice.
	 *
	 * @param billingRun    the billing run
	 * @param nbRuns        the nb runs
	 * @param waitingMillis the waiting millis
	 * @param jobInstanceId the job instance id
	 * @param isFullAutomatic 
	 * @throws BusinessException the business exception
	 */
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void createAgregatesAndInvoice(BillingRun billingRun, long nbRuns, long waitingMillis, Long jobInstanceId, boolean isFullAutomatic)
			throws BusinessException {
		//#MEL must be used in all RT read queries!
        Date lastTransactionDate = billingRun.getLastTransactionDate();
        if (Boolean.parseBoolean(paramBeanFactory.getInstance().getProperty("invoicing.includeEndDate", "false"))) {
            lastTransactionDate = DateUtils.setDateToEndOfDay(lastTransactionDate);
        } else {
        	lastTransactionDate = DateUtils.setDateToStartOfDay(lastTransactionDate);
        }
		// #MEL where to put pageSise param?
		final int pageSise = 30000;
		final boolean expectMassRtsPerInvoice = true;
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

		while (count != 0) {
			items = getInvoicingItems(billingRun, pageSise, page, thresholdPerEntityFound, expectMassRtsPerInvoice);
			count = items.size();
			log.info("======== READER : " + count);
			processEntitiesHavingNoParentThreshold(billingRun, nbRuns, waitingMillis, jobInstanceId, items, isFullAutomatic, expectMassRtsPerInvoice);
			log.info("======== PROCESSED ");
			page += 1;
		}
	}

	private void processEntitiesHavingNoParentThreshold(BillingRun billingRun, long nbRuns, long waitingMillis,
			Long jobInstanceId, List<BillingAccountDetailsItem> items, boolean isFullAutomatic, boolean expectMassRtsPerInvoice) {
		SubListCreator<BillingAccountDetailsItem> subListCreator = null;
		try {
			//final List<List<InvoicingItem>> values = items.stream().collect(Collectors.groupingBy(InvoicingItem::getInvoiceKey)).values().stream().collect(Collectors.toList());
			subListCreator = new SubListCreator(items, (int) nbRuns);
		} catch (Exception e1) {
			throw new BusinessException("cannot create  agregates and invoice with nbRuns=" + nbRuns);
		}

		// boolean[] minRTsUsed = ratedTransactionService.isMinRTsUsed();
		MinAmountForAccounts minAmountForAccounts = ratedTransactionService.isMinAmountForAccountsActivated();
		List<Future<String>> asyncReturns = new ArrayList<Future<String>>();
		MeveoUser lastCurrentUser = currentUser.unProxy();
		while (subListCreator.isHasNext()) {
			asyncReturns.add(invoicingService.createAgregatesAndInvoiceForJob(billingRun, subListCreator.getNextWorkSet(),jobInstanceId, minAmountForAccounts, lastCurrentUser, isFullAutomatic, expectMassRtsPerInvoice));
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

	/**
	 * @param parentEntitiesWithThresholdPerEntity
	 * @param pageSise
	 * @param nbRuns
	 * @return
	 */
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
		final List<ThresholdSummary> sortedThresholdInfos = finalList.stream()
				.sorted(Comparator.comparingInt(ThresholdSummary::getCount).reversed()).collect(Collectors.toList());
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

	/**
	 * @param billingRun
	 */
	private List<ThresholdSummary> getEntitiesToInvoiceHavingThresholdPerEntity(BillingRun billingRun) {
		return getEntityManager().createNamedQuery("BillingAccount.findEntitiesToInvoiceHavingThresholdPerEntity",
				ThresholdSummary.class).setParameter("billingRunId", billingRun.getId()).getResultList();
	}

	/**
	 * Assign invoice number and increment BA invoice dates.
	 *
	 * @param billingRun    The billing run
	 * @param nbRuns        the nb runs
	 * @param waitingMillis The waiting millis
	 * @param jobInstanceId The job instance id
	 * @param result        the Job execution result
	 * @throws BusinessException the business exception
	 */
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void assignInvoiceNumberAndIncrementBAInvoiceDates(BillingRun billingRun, long nbRuns, long waitingMillis,
			Long jobInstanceId, JobExecutionResultImpl result) throws BusinessException {
		List<InvoicesToNumberInfo> invoiceSummary = invoiceService.getInvoicesToNumberSummary(billingRun.getId());
		// Reserve invoice number for each invoice type/seller/invoice date combination
		for (InvoicesToNumberInfo invoicesToNumberInfo : invoiceSummary) {
			InvoiceSequence sequence = serviceSingleton.reserveInvoiceNumbers(invoicesToNumberInfo.getInvoiceTypeId(),
					invoicesToNumberInfo.getSellerId(), invoicesToNumberInfo.getInvoiceDate(),
					invoicesToNumberInfo.getNrOfInvoices());
			invoicesToNumberInfo.setNumberingSequence(sequence);
		}
		// Find and process invoices
		for (InvoicesToNumberInfo invoicesToNumberInfo : invoiceSummary) {
			List<Long> invoices = invoiceService.getInvoiceIds(billingRun.getId(),
					invoicesToNumberInfo.getInvoiceTypeId(), invoicesToNumberInfo.getSellerId(),
					invoicesToNumberInfo.getInvoiceDate());
			// Validate that what was retrieved as summary matches the details
			if (invoices.size() != invoicesToNumberInfo.getNrOfInvoices().intValue()) {
				throw new BusinessException(String.format(
						"Number of invoices retrieved %s dont match the expected number %s for %s/%s/%s/%s",
						invoices.size(), invoicesToNumberInfo.getNrOfInvoices(), billingRun.getId(),
						invoicesToNumberInfo.getInvoiceTypeId(), invoicesToNumberInfo.getSellerId(),
						invoicesToNumberInfo.getInvoiceDate()));
			}

			processInvoiceNumberAssignements(billingRun, nbRuns, waitingMillis, jobInstanceId, result,
					invoicesToNumberInfo, invoices);

			List<Long> baIDs = invoiceService.getBillingAccountIds(billingRun.getId(),
					invoicesToNumberInfo.getInvoiceTypeId(), invoicesToNumberInfo.getSellerId(),
					invoicesToNumberInfo.getInvoiceDate());
			processBAInvoiceDatesIncrementAsync(billingRun, nbRuns, waitingMillis, jobInstanceId, result,
					invoicesToNumberInfo, baIDs);

		}
	}

	private void processBAInvoiceDatesIncrementAsync(BillingRun billingRun, long nbRuns, long waitingMillis,
			Long jobInstanceId, JobExecutionResultImpl result, InvoicesToNumberInfo invoicesToNumberInfo,
			List<Long> baIDs) {
		SubListCreator subListCreator = null;
		try {
			subListCreator = new SubListCreator(baIDs, (int) nbRuns);
		} catch (Exception e1) {
			throw new BusinessException("Failed to subdivide an invoice list with nbRuns=" + nbRuns);
		}

		List<Future<String>> asyncReturns = new ArrayList<Future<String>>();
		MeveoUser lastCurrentUser = currentUser.unProxy();
		while (subListCreator.isHasNext()) {
			asyncReturns.add(invoicingAsync.incrementBAInvoiceDatesAsync(billingRun, subListCreator.getNextWorkSet(),
					jobInstanceId, result, lastCurrentUser));
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

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	private void processInvoiceNumberAssignements(BillingRun billingRun, long nbRuns, long waitingMillis,
			Long jobInstanceId, JobExecutionResultImpl result, InvoicesToNumberInfo invoicesToNumberInfo,
			List<Long> invoices) {
		SubListCreator subListCreator = null;

		try {
			subListCreator = new SubListCreator(invoices, (int) nbRuns);
		} catch (Exception e1) {
			throw new BusinessException("Failed to subdivide an invoice list with nbRuns=" + nbRuns);
		}

		List<Future<String>> asyncReturns = new ArrayList<Future<String>>();
		MeveoUser lastCurrentUser = currentUser.unProxy();
		while (subListCreator.isHasNext()) {
			asyncReturns.add(invoicingAsync.assignInvoiceNumberAsync(billingRun, subListCreator.getNextWorkSet(),
					invoicesToNumberInfo, jobInstanceId, result, lastCurrentUser));
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

	/**
	 * Invoicing process for the billingRun, launched by invoicingJob.
	 *
	 * @param billingRun    the billing run to process
	 * @param nbRuns        the nb runs
	 * @param waitingMillis the waiting millis
	 * @param jobInstanceId the job instance
	 * @param result        the Job execution result
	 * @throws Exception the exception
	 */
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void validate(BillingRun billingRun, long nbRuns, long waitingMillis, Long jobInstanceId,
			JobExecutionResultImpl result) throws Exception {
		log.info("==================== START Processing billingRun id={} status={} ====================", billingRun.getId(),
				billingRun.getStatus());
		// List<IBillableEntity> a = new ArrayList<>();

		BillingCycle billingCycle = billingRun.getBillingCycle();
		BillingEntityTypeEnum type = null;
		if (billingCycle != null) {
			type = billingCycle.getType();
		}

		MinAmountForAccounts minAmountForAccounts = new MinAmountForAccounts();
		if (BillingRunStatusEnum.NEW.equals(billingRun.getStatus()) || BillingRunStatusEnum.PREVALIDATED.equals(billingRun.getStatus())) {
			minAmountForAccounts = ratedTransactionService.isMinAmountForAccountsActivated();
		}

		if (BillingRunStatusEnum.NEW.equals(billingRun.getStatus())) {
			log.info("==================== 2");
			int totalEntityCount = 0;

			// Use billable amount calculation one billable entity at a time when minimum
			// billable amount rule is used or billable entities are provided as parameter
			// of billing run
			// (billingCycle=null)
			// NOTE: invoice by order is also included here as there is no FK between Order
			// and RT
			if (billingCycle == null || billingCycle.getType() == BillingEntityTypeEnum.ORDER || minAmountForAccounts.isMinAmountCalculationActivated()) {
				List<? extends IBillableEntity> entities = getEntitiesToInvoice(billingRun, 0, 0, false);

				totalEntityCount = entities != null ? entities.size() : 0;
				log.info(
						"Will create min RTs and update billable amount totals for Billing run {} for {} entities of type {}. Minimum invoicing amount is used for accounts hierarchy {}",
						billingRun.getId(), totalEntityCount, type, minAmountForAccounts);
				if ((entities != null) && (entities.size() > 0)) {
					SubListCreator subListCreator = new SubListCreator(entities, (int) nbRuns);
					List<Future<List<IBillableEntity>>> asyncReturns = new ArrayList<Future<List<IBillableEntity>>>();
					MeveoUser lastCurrentUser = currentUser.unProxy();
					while (subListCreator.isHasNext()) {
						Future<List<IBillableEntity>> billableEntitiesAsynReturn = invoicingAsync
								.calculateBillableAmountsAsync(subListCreator.getNextWorkSet(), billingRun,
										jobInstanceId, minAmountForAccounts, lastCurrentUser);
						asyncReturns.add(billableEntitiesAsynReturn);
						try {
							Thread.sleep(waitingMillis);
						} catch (InterruptedException e) {
							log.error("", e);
						}
					}
//#MEL remove billableEntities
					// for (Future<List<IBillableEntity>> futureItsNow : asyncReturns) {
					// billableEntities.addAll(futureItsNow.get());
					// }
				}

				// A simplified form of calculating of total amounts when no need to worry about
				// minimum amounts
			} else {
				log.info("==================== 3");
				//#MEL TODO manage subscription/order cases
				AmountsToInvoice billableAmountSummary = getBRSummury(billingRun);
				billingAccountService.linkBillableEntitiesToBR(billingRun);
				totalEntityCount = billableAmountSummary.getEntityToInvoiceId().intValue();
				billingRunExtensionService.updateBRAmounts(billingRun.getId(), billableAmountSummary);
				log.info("Minimum invoicing amount is skipped.");
			}
			log.info("==================== 4");
			// #MEL BE<>
			billingRunExtensionService.updateBillingRun(billingRun.getId(), totalEntityCount, totalEntityCount, BillingRunStatusEnum.PREINVOICED, new Date());
		}

		final boolean isFullAutomatic = billingRun.getProcessType() == BillingProcessTypesEnum.FULL_AUTOMATIC;
		boolean proceedToInvoiceGenerating = BillingRunStatusEnum.PREVALIDATED.equals(billingRun.getStatus()) || 
				(BillingRunStatusEnum.NEW.equals(billingRun.getStatus()) && ((billingRun.getProcessType() == BillingProcessTypesEnum.AUTOMATIC || isFullAutomatic) || appProvider.isAutomaticInvoicing()));

		if (proceedToInvoiceGenerating) {
			log.info("recalculateTaxes IS SKIPPED==================== 5");

			// boolean instantiateMinRts = !includesFirstRun &&
			// (minAmountForAccounts.isMinAmountCalculationActivated());
			// MinAmountForAccounts minAmountForAccountsIncludesFirstRun =
			// minAmountForAccounts.includesFirstRun(!includesFirstRun);
			
			//#MEL resolve tax category for BAs with empty tax Category and category with ELs
			//invoiceService.recalculateTaxes(billingRun);
			
			log.info("createAgregatesAndInvoice ==================== 6");
			createAgregatesAndInvoice(billingRun, nbRuns, waitingMillis, jobInstanceId, isFullAutomatic);
			billingRunExtensionService.updateBillingRun(billingRun.getId(), null, null,
					BillingRunStatusEnum.INVOICES_GENERATED, null);
			billingRun = billingRunExtensionService.findById(billingRun.getId());
		}
		if (BillingRunStatusEnum.INVOICES_GENERATED.equals(billingRun.getStatus())) {
			
			log.info("apply threshold rules for all invoices generated with {}", billingRun);
			// billingRunService.applyThreshold(billingRun);
			// rejectBAWithoutBillableTransactions(billingRun, nbRuns, waitingMillis,
			// jobInstanceId, result);
			billingRunExtensionService.updateBillingRun(billingRun.getId(), null, null,
					BillingRunStatusEnum.POSTINVOICED, null);
			if (isFullAutomatic) {
				billingRun = billingRunExtensionService.findById(billingRun.getId());
			}
		}

		if (BillingRunStatusEnum.POSTINVOICED.equals(billingRun.getStatus()) && isFullAutomatic) {
			log.info("==================== 7");
			billingRunExtensionService.updateBillingRun(billingRun.getId(), null, null,
					BillingRunStatusEnum.POSTVALIDATED, null);
			billingRun = billingRunExtensionService.findById(billingRun.getId());
		}

		if (BillingRunStatusEnum.POSTVALIDATED.equals(billingRun.getStatus()) && !isFullAutomatic) {
			log.info("==================== 8");
			log.info("Will assign invoice numbers to invoices of Billing run {} of type {}", billingRun.getId(), type);
			invoiceService.nullifyInvoiceFileNames(billingRun); // #3600
			assignInvoiceNumberAndIncrementBAInvoiceDates(billingRun, nbRuns, waitingMillis, jobInstanceId, result);
			billingRunExtensionService.updateBillingRun(billingRun.getId(), null, null, BillingRunStatusEnum.VALIDATED,
					null);
		}
		log.info("==================== 9");
	}

	private AmountsToInvoice getBRSummury(BillingRun billingRun) {
		BillingCycle billingCycle = billingRun.getBillingCycle();
		Date startDate = billingRun.getStartDate();
		Date endDate = billingRun.getEndDate();
		if ((startDate != null) && (endDate == null)) {
			endDate = new Date();
		}
		if (endDate != null && startDate == null) {
			startDate = new Date(0);
		}
		String sqlName = billingCycle.getType() == BillingEntityTypeEnum.SUBSCRIPTION
				? "RatedTransaction.sumBRBySubscription"
				: startDate == null ? "RatedTransaction.sumBRByBA" : "RatedTransaction.sumBRByBALimitByNextInvoiceDate";
		TypedQuery<AmountsToInvoice> query = getEntityManager().createNamedQuery(sqlName, AmountsToInvoice.class)
				.setParameter("firstTransactionDate", new Date(0))
				.setParameter("lastTransactionDate", billingRun.getLastTransactionDate())
				.setParameter("billingCycle", billingCycle);

		if (billingCycle.getType() == BillingEntityTypeEnum.BILLINGACCOUNT && startDate != null) {
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

}