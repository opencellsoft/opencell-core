package org.meveo.admin.job;

import static java.lang.String.format;
import static java.math.BigDecimal.ZERO;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;
import static org.meveo.model.billing.BillingProcessTypesEnum.AUTOMATIC;
import static org.meveo.model.billing.BillingProcessTypesEnum.FULL_AUTOMATIC;
import static org.meveo.model.billing.BillingRunStatusEnum.DRAFT_INVOICES;
import static org.meveo.model.billing.BillingRunStatusEnum.INVOICES_CREATED;
import static org.meveo.model.billing.BillingRunStatusEnum.INVOICE_LINES_CREATED;
import static org.meveo.model.billing.BillingRunStatusEnum.POSTVALIDATED;
import static org.meveo.model.billing.BillingRunStatusEnum.PREVALIDATED;
import static org.meveo.model.billing.BillingRunStatusEnum.REJECTED;
import static org.meveo.model.billing.BillingRunStatusEnum.VALIDATED;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.collections4.ListUtils;
import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.invoicing.InvoicingService;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.InvoiceSequence;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.JobSpeedEnum;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoicesToNumberInfo;
import org.meveo.service.billing.impl.ServiceSingleton;

@Stateless
public class InvoicingJobV2Bean extends BaseJobBean {

    private static final long serialVersionUID = -2523209868278837776L;
    
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

    private static BigDecimal amountTax = ZERO;
    private static BigDecimal amountWithTax = ZERO;
    private static BigDecimal amountWithoutTax = ZERO;
	@JpaAmpNewTx
	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(REQUIRES_NEW)
	public JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) {
		log.debug("Running InvoicingV2Job with parameter={}", jobInstance.getParametres());
		try {
			List<BillingRun> billingRuns = readValidBillingRunsToProcess(jobInstance, result);
			if (billingRuns.isEmpty()) {
				List<String> errors = List.of("No valid billing run with status=INVOICE_LINES_CREATED found");
				result.setErrors(errors);
			} else {
				for (BillingRun billingRun : billingRuns) {
					boolean updateBillingRun = executeBillingRun(billingRun, jobInstance, result);
					if (updateBillingRun) {
						billingRunService.update(billingRun);
					}
				}
			}
		} catch (Exception exception) {
			result.registerError(exception.getMessage());
			log.error(format("Failed to run invoicing job: %s", exception));
			exception.printStackTrace();
		}
		return result;
	}

	private List<BillingRun> readValidBillingRunsToProcess(JobInstance jobInstance, JobExecutionResultImpl result) {
		List<EntityReferenceWrapper> billingRunWrappers = (List<EntityReferenceWrapper>) this
				.getParamOrCFValue(jobInstance, "InvoicingJobV2_billingRun");
		List<Long> billingRunIds = billingRunWrappers != null ? extractBRIds(billingRunWrappers) : emptyList();
		Map<String, Object> filters = new HashedMap();
		if (billingRunIds.isEmpty()) {
			filters.put("status", INVOICE_LINES_CREATED);
		} else {
			filters.put("inList id", billingRunIds);
		}
		PaginationConfiguration paginationConfiguration = new PaginationConfiguration(filters);
		List<BillingRun> billingRuns = billingRunService.list(paginationConfiguration);
		validateBRList(billingRuns, result);
		return billingRuns;
	}

	private List<Long> extractBRIds(List<EntityReferenceWrapper> billingRunWrappers) {
		return billingRunWrappers.stream().map(br -> Long.valueOf(br.getCode().split("/")[0])).collect(toList());
	}
	
	private void validateBRList(List<BillingRun> billingRuns, JobExecutionResultImpl result) {
		if (billingRuns == null || billingRuns.isEmpty()) {
			return;
		}
		List<BillingRun> excludedBRs = billingRuns.stream().filter(br -> br.getStatus() != INVOICE_LINES_CREATED).collect(toList());
		excludedBRs.forEach(br -> result.registerWarning(format("BillingRun[id={%d}] has been ignored. Only Billing runs with status=INVOICE_LINES_CREATED can be processed",br.getId())));
		result.setNbItemsProcessedWithWarning(excludedBRs.size());
		billingRuns.removeAll(excludedBRs);
	}

	private boolean executeBillingRun(BillingRun billingRun, JobInstance jobInstance, JobExecutionResultImpl result) {
		boolean billingRunUpdated = false;
		final boolean isFullAutomatic = billingRun.getProcessType() == FULL_AUTOMATIC;
		if (billingRun.getStatus() == INVOICE_LINES_CREATED
				&& (billingRun.getProcessType() == AUTOMATIC || isFullAutomatic)) {
			billingRunUpdated = true;
			billingRun.setStatus(PREVALIDATED);
		}
		if (billingRun.getStatus() == PREVALIDATED) {
			Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
			if (nbRuns == -1) {
				nbRuns = (long) Runtime.getRuntime().availableProcessors();
			}
			Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);
			try {
				createAggregatesAndInvoiceWithIl(billingRun, nbRuns, waitingMillis, jobInstance.getId(), isFullAutomatic, billingRun.getBillingCycle(), result);
			}catch (Exception e) {
				log.error(e.getMessage());
				e.printStackTrace();
				return false;
			}
			billingRun.setStatus(INVOICES_CREATED);
			billingRun = billingRunService.refreshOrRetrieve(billingRun);
			billingRun.setStatus(DRAFT_INVOICES);
		}
		if (!billingRunService.isBillingRunValid(billingRun)) {
			billingRun.setStatus(REJECTED);
		}
		if (billingRun.getStatus() == DRAFT_INVOICES && isFullAutomatic) {
			billingRun.setStatus(POSTVALIDATED);
		}
		if (billingRun.getStatus() == POSTVALIDATED) {
			if(!isFullAutomatic) {
				assignInvoiceNumberAndIncrementBAInvoiceDates(billingRun, result);
			}
			billingRun.setStatus(VALIDATED);
			
		}
		return billingRunUpdated;
	}
	
	private void createAggregatesAndInvoiceWithIl(BillingRun billingRun, long nbRuns, long waitingMillis, Long jobInstanceId,
			boolean isFullAutomatic, BillingCycle billingCycle, JobExecutionResultImpl result) throws BusinessException {
		List<Long> bAIds = billingRunService.getBAsHavingOpenILs(billingRun);
		if(bAIds.isEmpty()) {
			log.info("=======NO INVOICE LINES TO PROCESS for BR {}=========", billingRun.getId());
			return;
		}
		log.info("=======INVOICING JOB HAVE TO PROCESS {} BAs for BR {}=========", bAIds.size(), billingRun.getId());
		int maxBAsPerTransaction = ((Long) this.getParamOrCFValue(result.getJobInstance(), "maxBAsPerTransaction", 1000L)).intValue();
		maxBAsPerTransaction = maxBAsPerTransaction > 0 ? maxBAsPerTransaction : 1000;
		int itemsPerSplit = (bAIds.size() / maxBAsPerTransaction) > nbRuns ? maxBAsPerTransaction : (int) (bAIds.size() / nbRuns);
		itemsPerSplit=itemsPerSplit>0?itemsPerSplit:1;
		MeveoUser lastCurrentUser = currentUser.unProxy();
		BiConsumer<List<Long>, JobExecutionResultImpl> task = (item, jobResult) -> {invoicingService.createAgregatesAndInvoiceForJob(item, billingRun, billingCycle, jobInstanceId, lastCurrentUser, isFullAutomatic, result);};
		iteratorBasedJobProcessing.processItems(result, new SynchronizedIterator<>(ListUtils.partition(bAIds, itemsPerSplit)), task, null, null, nbRuns, waitingMillis, false, JobSpeedEnum.FAST, true);
	}

    /**
     * Assign invoice number and increment BA invoice dates.
     *
     * @param billingRun The billing run
     * @param jobExecutionResult the Job execution result
     * @throws BusinessException the business exception
     */
    public void assignInvoiceNumberAndIncrementBAInvoiceDates(BillingRun billingRun, JobExecutionResultImpl jobExecutionResult) throws BusinessException {

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
            InvoiceSequence sequence = serviceSingleton.reserveInvoiceNumbers(invoicesToNumberInfo.getInvoiceTypeId(), invoicesToNumberInfo.getSellerId(), invoicesToNumberInfo.getInvoiceDate(),
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

            List<Long> invoiceIds = invoiceService.getInvoiceIds(billingRun.getId(), invoicesToNumberInfo.getInvoiceTypeId(), invoicesToNumberInfo.getSellerId(), invoicesToNumberInfo.getInvoiceDate());
            // Validate that what was retrieved as summary matches the details
            if (invoiceIds.size() != invoicesToNumberInfo.getNrOfInvoices().intValue()) {
                throw new BusinessException(String.format("Number of invoices retrieved %s dont match the expected number %s for %s/%s/%s/%s", invoiceIds.size(), invoicesToNumberInfo.getNrOfInvoices(),
                    billingRun.getId(), invoicesToNumberInfo.getInvoiceTypeId(), invoicesToNumberInfo.getSellerId(), invoicesToNumberInfo.getInvoiceDate()));
            }

            // Assign invoice numbers
            BiConsumer<Long, JobExecutionResultImpl> task = (invoiceId, jobResult) -> {
                invoiceService.recalculateDates(invoiceId);
                invoiceService.assignInvoiceNumber(invoiceId, invoicesToNumberInfo);
                invoiceService.updateStatus(invoiceId, InvoiceStatusEnum.VALIDATED);

            };
            iteratorBasedJobProcessing.processItems(jobExecutionResult, new SynchronizedIterator<>(invoiceIds), task, null, null, nbRuns, waitingMillis, false, JobSpeedEnum.VERY_FAST, true);

            List<Long> baIds = invoiceService.getBillingAccountIds(billingRun.getId(), invoicesToNumberInfo.getInvoiceTypeId(), invoicesToNumberInfo.getSellerId(), invoicesToNumberInfo.getInvoiceDate());

            // Increment next invoice date of a billing account
            task = (baId, jobResult) -> {
                invoiceService.incrementBAInvoiceDate(billingRun, baId);
            };

            iteratorBasedJobProcessing.processItems(jobExecutionResult, new SynchronizedIterator<>(baIds), task, null, null, nbRuns, waitingMillis, false, JobSpeedEnum.FAST, false);
        }
    }

    public static void addNewAmounts(BigDecimal amountTax, BigDecimal amountWithoutTax, BigDecimal amountWithTax) {
        InvoicingJobV2Bean.amountTax = InvoicingJobV2Bean.amountTax.add(amountTax);
        InvoicingJobV2Bean.amountWithTax = InvoicingJobV2Bean.amountWithTax.add(amountWithTax);
        InvoicingJobV2Bean.amountWithoutTax = InvoicingJobV2Bean.amountWithoutTax.add(amountWithoutTax);
    }
}