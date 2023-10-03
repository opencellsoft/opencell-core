package org.meveo.admin.job.invoicing;
import static java.lang.String.format;
import static java.math.BigDecimal.ZERO;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.meveo.model.billing.BillingProcessTypesEnum.AUTOMATIC;
import static org.meveo.model.billing.BillingProcessTypesEnum.FULL_AUTOMATIC;
import static org.meveo.model.billing.BillingRunStatusEnum.DRAFT_INVOICES;
import static org.meveo.model.billing.BillingRunStatusEnum.INVOICE_LINES_CREATED;
import static org.meveo.model.billing.BillingRunStatusEnum.OPEN;
import static org.meveo.model.billing.BillingRunStatusEnum.POSTVALIDATED;
import static org.meveo.model.billing.BillingRunStatusEnum.PREVALIDATED;
import static org.meveo.model.billing.BillingRunStatusEnum.REJECTED;
import static org.meveo.model.billing.BillingRunStatusEnum.VALIDATED;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.collections4.ListUtils;
import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ImportInvoiceException;
import org.meveo.admin.exception.InvoiceExistException;
import org.meveo.admin.job.BaseJobBean;
import org.meveo.admin.job.IteratorBasedJobProcessing;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.job.utils.BillinRunApplicationElFilterUtils;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingProcessTypesEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.InvoiceSequence;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.JobSpeedEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.BillingRunExtensionService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoicesToNumberInfo;
import org.meveo.service.billing.impl.ServiceSingleton;
@Stateless
public class InvoicingJobV3Bean extends BaseJobBean {
	private static final long serialVersionUID = 1L;
	@Inject
	private BillingRunService billingRunService;
	@Inject
	private InvoiceService invoiceService;
	@Inject
	private InvoicingService invoicingService;
	@Inject
	private ServiceSingleton serviceSingleton;
	@Inject
	private IteratorBasedJobProcessing iteratorBasedJobProcessing;
	@Inject
	private BillingRunExtensionService billingRunExtensionService;
	private static BigDecimal amountTax = ZERO;
	private static BigDecimal amountWithTax = ZERO;
	private static BigDecimal amountWithoutTax = ZERO;
	@JpaAmpNewTx
	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) {
		log.debug("Running InvoicingV3Job with parameter={}", jobInstance.getParametres());
		try {
			List<BillingRun> billingRuns = BillinRunApplicationElFilterUtils.filterByApplicationEL(
					readValidBillingRunsToProcess(jobInstance), jobInstance);
			if (billingRuns.isEmpty()) {
				List<String> errors = List.of("No valid billing run with status=INVOICE_LINES_CREATED found");
				result.setErrors(errors);
			} else {
				for (BillingRun billingRun : billingRuns) {
					executeBillingRun(billingRun, jobInstance, result,
							billingRun.getBillingCycle() != null
									? billingRun.getBillingCycle().getBillingRunValidationScript()
									: null);
				}
				result.setNbItemsCorrectlyProcessed(billingRuns.size());
			}
		} catch (Exception exception) {
			result.registerError(exception.getMessage());
			log.error(format("Failed to run invoicing job: %s", exception), exception);
		}
		return result;
	}

	private List<BillingRun> readValidBillingRunsToProcess(JobInstance jobInstance) {
		List<EntityReferenceWrapper> billingRunWrappers = (List<EntityReferenceWrapper>) this
				.getParamOrCFValue(jobInstance, "InvoicingJobV3_billingRun");
		List<EntityReferenceWrapper> billingRunWrappersV2 = (List<EntityReferenceWrapper>) this
				.getParamOrCFValue(jobInstance, "InvoicingJobV2_billingRun");
		if(billingRunWrappers != null) {
			ofNullable(billingRunWrappersV2).ifPresent(billingRunWrappers::addAll);
		} else {
			billingRunWrappers = billingRunWrappersV2;
		}
		List<Long> billingRunIds = billingRunWrappers != null ? extractBRIds(billingRunWrappers) : emptyList();
		Map<String, Object> filters = new HashedMap();
		if (!billingRunIds.isEmpty()) {
			filters.put("inList id", billingRunIds);
			filters.put("ne status", OPEN);
		} else {
			filters.put("status", INVOICE_LINES_CREATED);
		}
		filters.put("disabled", false);
		PaginationConfiguration paginationConfiguration = new PaginationConfiguration(filters);
		paginationConfiguration.setFetchFields(Arrays.asList("billingCycle", "billingCycle.billingRunValidationScript"));
		List<BillingRun> billingRuns = billingRunService.list(paginationConfiguration);
		return billingRuns;
	}
	private List<Long> extractBRIds(List<EntityReferenceWrapper> billingRunWrappers) {
		return billingRunWrappers.stream().map(br -> Long.valueOf(br.getCode().split("/")[0])).collect(toList());
	}

	private void executeBillingRun(BillingRun billingRun, JobInstance jobInstance, JobExecutionResultImpl result,
			ScriptInstance billingRunValidationScript) {
		boolean prevalidatedAutomaticPrevBRStatus = false;
		final boolean isFullAutomatic = billingRun.getProcessType() == FULL_AUTOMATIC;
		if (billingRun.getStatus() == INVOICE_LINES_CREATED
				&& (billingRun.getProcessType() == AUTOMATIC || isFullAutomatic)) {
			billingRun.setStatus(PREVALIDATED);
		}
		if (billingRun.getStatus() == PREVALIDATED) {
			Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
			if (nbRuns == -1) {
				nbRuns = (long) Runtime.getRuntime().availableProcessors();
			}
			Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);
			try {
				createAggregatesAndInvoiceWithIl(billingRun, nbRuns, waitingMillis, jobInstance.getId(),
						isFullAutomatic, billingRun.getBillingCycle(), result);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				prevalidatedAutomaticPrevBRStatus = true;
			}
			billingRun.setPrAmountWithTax(amountWithTax);
			billingRun.setPrAmountWithoutTax(amountWithoutTax);
			billingRun.setPrAmountTax(amountTax);
			billingRun.setStatus(DRAFT_INVOICES);
			prevalidatedAutomaticPrevBRStatus = true;
			invoiceService.applyligibleInvoiceForAdvancement(billingRun.getId());
		}
		if (billingRun.getStatus() == DRAFT_INVOICES && isFullAutomatic) {
			billingRun.setStatus(POSTVALIDATED);
		}
		if (billingRunValidationScript != null && billingRun.getBillingCycle() != null) {
			billingRun.getBillingCycle().setBillingRunValidationScript(billingRunValidationScript);
		}
		if ((billingRun.getProcessType() == BillingProcessTypesEnum.FULL_AUTOMATIC
				|| billingRun.getProcessType() == BillingProcessTypesEnum.AUTOMATIC)
				&& (BillingRunStatusEnum.POSTINVOICED.equals(billingRun.getStatus())
						|| BillingRunStatusEnum.DRAFT_INVOICES.equals(billingRun.getStatus())
						|| BillingRunStatusEnum.REJECTED.equals(billingRun.getStatus()))) {
			billingRunService.applyAutomaticValidationActions(billingRun);
			if (billingRunService.isBillingRunValid(billingRun)) {
				if (billingRun.getProcessType() == BillingProcessTypesEnum.FULL_AUTOMATIC) {
					billingRun.setStatus(POSTVALIDATED);
				} else if (billingRun.getProcessType() == BillingProcessTypesEnum.AUTOMATIC
						&& prevalidatedAutomaticPrevBRStatus) {
					billingRun.setStatus(DRAFT_INVOICES);
				} else {
					billingRun.setStatus(POSTVALIDATED);
				}
			} else {
				billingRun.setStatus(REJECTED);
			}
		}
		billingRun = billingRunExtensionService.updateBillingRun(billingRun.getId(), null, null, billingRun.getStatus(),
				null);
		billingRun = billingRunService.refreshOrRetrieve(billingRun);
		if (billingRun.getStatus() == POSTVALIDATED) {
			if (!isFullAutomatic) {
				assignInvoiceNumberAndIncrementBAInvoiceDatesAndGenerateAO(billingRun, result);
			}
			billingRun.setStatus(VALIDATED);
		}
		billingRunService.update(billingRun);
		billingRunService.updateBillingRunStatistics(billingRun);
		billingRunService.updateBillingRunJobExecution(billingRun.getId(), result);
	}
	private void createAggregatesAndInvoiceWithIl(BillingRun billingRun, long nbRuns, long waitingMillis,
			Long jobInstanceId, boolean isFullAutomatic, BillingCycle billingCycle, JobExecutionResultImpl result)
			throws BusinessException {
		List<Long> bAIds = billingRunService.getBAsHavingOpenILs(billingRun);
		if (bAIds.isEmpty()) {
			log.info("=======NO INVOICE LINES TO PROCESS for BR {}=========", billingRun.getId());
			return;
		}
		log.info("=======INVOICING JOB HAVE TO PROCESS {} BAs for BR {}=========", bAIds.size(), billingRun.getId());
		int maxBAsPerTransaction = ((Long) this.getParamOrCFValue(result.getJobInstance(), "maxBAsPerTransaction",
				1000L)).intValue();
		maxBAsPerTransaction = maxBAsPerTransaction > 0 ? maxBAsPerTransaction : 1000;
		int itemsPerSplit = (bAIds.size() / maxBAsPerTransaction) > nbRuns ? maxBAsPerTransaction
				: (int) (bAIds.size() / nbRuns);
		itemsPerSplit = itemsPerSplit > 0 ? itemsPerSplit : 1;
		MeveoUser lastCurrentUser = currentUser.unProxy();
		BiConsumer<List<Long>, JobExecutionResultImpl> task = (item, jobResult) -> {
			invoicingService.createAgregatesAndInvoiceForJob(item, billingRun, billingCycle, jobInstanceId,
					lastCurrentUser, isFullAutomatic, result);
		};
		iteratorBasedJobProcessing.processItems(result,
				new SynchronizedIterator<>(ListUtils.partition(bAIds, itemsPerSplit)), task, null, null, nbRuns,
				waitingMillis, false, JobSpeedEnum.FAST, false);
	}
	/**
	 * Assign invoice number and increment BA invoice dates.
	 *
	 * @param billingRun         The billing run
	 * @param jobExecutionResult the Job execution result
	 * @throws BusinessException the business exception
	 */
	public void assignInvoiceNumberAndIncrementBAInvoiceDatesAndGenerateAO(BillingRun billingRun,
			JobExecutionResultImpl jobExecutionResult) throws BusinessException {
		log.info("Will assign invoice numbers to invoices of Billing run {}", billingRun.getId());
		List<InvoicesToNumberInfo> invoiceSummary = invoiceService.getInvoicesToNumberSummary(billingRun.getId());
		// A quick loop to update job progress with a number of items to process
		for (InvoicesToNumberInfo invoicesToNumberInfo : invoiceSummary) {
			jobExecutionResult.addNbItemsToProcess(invoicesToNumberInfo.getNrOfInvoices());
		}
		jobExecutionResultService.persistResult(jobExecutionResult);
		invoiceService.nullifyInvoiceFileNames(billingRun); // #3600
		// Reserve invoice number for each invoice type/seller/invoice date combination
		for (InvoicesToNumberInfo invoicesToNumberInfo : invoiceSummary) {
			InvoiceSequence sequence = serviceSingleton.reserveInvoiceNumbers(invoicesToNumberInfo.getInvoiceTypeId(),
					invoicesToNumberInfo.getSellerId(), invoicesToNumberInfo.getInvoiceDate(),
					invoicesToNumberInfo.getNrOfInvoices());
			invoicesToNumberInfo.setNumberingSequence(sequence);
		}
		JobInstance jobInstance = jobExecutionResult.getJobInstance();
		Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
		if (nbRuns == -1) {
			nbRuns = (long) Runtime.getRuntime().availableProcessors();
		}
		Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);
		// Find and process invoices
		for (InvoicesToNumberInfo invoicesToNumberInfo : invoiceSummary) {
			List<Long> invoiceIds = invoiceService.getInvoiceIds(billingRun.getId(),
					invoicesToNumberInfo.getInvoiceTypeId(), invoicesToNumberInfo.getSellerId(),
					invoicesToNumberInfo.getInvoiceDate());
			// Validate that what was retrieved as summary matches the details
			if (invoiceIds.size() != invoicesToNumberInfo.getNrOfInvoices().intValue()) {
				throw new BusinessException(String.format(
						"Number of invoices retrieved %s dont match the expected number %s for %s/%s/%s/%s",
						invoiceIds.size(), invoicesToNumberInfo.getNrOfInvoices(), billingRun.getId(),
						invoicesToNumberInfo.getInvoiceTypeId(), invoicesToNumberInfo.getSellerId(),
						invoicesToNumberInfo.getInvoiceDate()));
			}
			// Assign invoice numbers
			BiConsumer<Long, JobExecutionResultImpl> task = (invoiceId, jobResult) -> {
				invoiceService.recalculateDates(invoiceId);
				invoiceService.assignInvoiceNumber(invoiceId, invoicesToNumberInfo);
				invoiceService.updateStatus(invoiceId, InvoiceStatusEnum.VALIDATED);
			};
			iteratorBasedJobProcessing.processItems(jobExecutionResult, new SynchronizedIterator<>(invoiceIds), task,
					null, null, nbRuns, waitingMillis, false, JobSpeedEnum.VERY_FAST, true);
			List<Long> baIds = invoiceService.getBillingAccountIds(billingRun.getId(),
					invoicesToNumberInfo.getInvoiceTypeId(), invoicesToNumberInfo.getSellerId(),
					invoicesToNumberInfo.getInvoiceDate());
			// Increment next invoice date of a billing account
			task = (baId, jobResult) -> {
				invoiceService.incrementBAInvoiceDate(billingRun, baId);
			};
			iteratorBasedJobProcessing.processItems(jobExecutionResult, new SynchronizedIterator<>(baIds), task, null,
					null, nbRuns, waitingMillis, false, JobSpeedEnum.FAST, false);
			if (billingRun.getGenerateAO()) {
				task = (invoiceId, jobResult) -> {
					try {
						invoiceService.generateRecordedInvoiceAO(invoiceId);
					} catch (BusinessException | InvoiceExistException | ImportInvoiceException e) {
						throw new BusinessException(String.format(
								"Error while trying to generate Account Operation for BR: %s, Invoice: %s",
								billingRun.getId(), invoiceId));
					}
				};
				iteratorBasedJobProcessing.processItems(jobExecutionResult, new SynchronizedIterator<>(invoiceIds),
						task, null, null, nbRuns, waitingMillis, false, JobSpeedEnum.FAST, false);
			}
		}
	}
	public static void addNewAmounts(BigDecimal amountTax, BigDecimal amountWithoutTax, BigDecimal amountWithTax) {
		InvoicingJobV3Bean.amountTax = InvoicingJobV3Bean.amountTax.add(amountTax);
		InvoicingJobV3Bean.amountWithTax = InvoicingJobV3Bean.amountWithTax.add(amountWithTax);
		InvoicingJobV3Bean.amountWithoutTax = InvoicingJobV3Bean.amountWithoutTax.add(amountWithoutTax);
	}
	private static void initAmounts() {
		amountTax = ZERO;
		amountWithTax = ZERO;
		amountWithoutTax = ZERO;
	}
}