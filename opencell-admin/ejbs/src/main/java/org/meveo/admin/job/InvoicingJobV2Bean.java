package org.meveo.admin.job;

import org.apache.commons.collections.map.HashedMap;
import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.InvoiceSequence;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.JobSpeedEnum;
import org.meveo.service.billing.impl.BillingRunExtensionService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoicesToNumberInfo;
import org.meveo.service.billing.impl.ServiceSingleton;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;
import static org.meveo.model.billing.BillingRunStatusEnum.PREVALIDATED;

@Stateless
public class InvoicingJobV2Bean extends BaseJobBean {

    @Inject
    private Logger log;
    @Inject
    private BillingRunService billingRunService;
    @Inject
    private BillingRunExtensionService billingRunExtensionService;
    /** The service singleton. */
    @Inject
    private InvoicingJobBean invoicingJobBean;
    
    @Inject
    private InvoiceService invoiceService;
    
    @Inject
    private ServiceSingleton serviceSingleton;
    

    @Inject
    private IteratorBasedJobProcessing iteratorBasedJobProcessing;
    

    @JpaAmpNewTx
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running InvoiceSplitJob with parameter={}", jobInstance.getParametres());
        try {
            List<EntityReferenceWrapper> billingRunWrappers =
                    (List<EntityReferenceWrapper>) this.getParamOrCFValue(jobInstance, "InvoicingJobV2_billingRun");
            List<Long> billingRunIds = billingRunWrappers != null ? extractBRIds(billingRunWrappers) : emptyList();
            Map<String, Object> filters = new HashedMap();
            if (billingRunIds.isEmpty()) {
                filters.put("status", BillingRunStatusEnum.INVOICE_LINES_CREATED);
            } else {
                filters.put("inList id", billingRunIds);
            }
            PaginationConfiguration paginationConfiguration = new PaginationConfiguration(filters);
            List<BillingRun> billingRuns = billingRunService.list(paginationConfiguration);
            if (billingRuns.isEmpty()) {
                List<String> errors = List.of("No valid billing run with status=PREVALIDATED found");
                result.setErrors(errors);
            } else {
                validateBRList(billingRuns, result);
                for (BillingRun billingRun : billingRuns) {
                    billingRunService.createAggregatesAndInvoiceWithIl(billingRun, 1, 0, jobInstance.getId());
                    assignInvoiceNumberAndIncrementBAInvoiceDates(billingRun, result);
                    billingRunExtensionService.updateBillingRun(billingRun.getId(), null, null, BillingRunStatusEnum.VALIDATED, null);;
                }
                result.setNbItemsCorrectlyProcessed(billingRuns.size());
            }
        } catch (Exception exception) {
            result.registerError(exception.getMessage());
            log.error(format("Failed to run invoice lines job: %s", exception));
        }
    }

    private List<Long> extractBRIds(List<EntityReferenceWrapper> billingRunWrappers) {
        return billingRunWrappers.stream()
                    .map(br -> Long.valueOf(br.getCode().split("/")[0]))
                    .collect(toList());
    }

    private void validateBRList(List<BillingRun> billingRuns, JobExecutionResultImpl result) {
        List<BillingRun> excludedBRs = billingRuns.stream()
                                            .filter(br -> br.getStatus() != BillingRunStatusEnum.INVOICE_LINES_CREATED)
                                            .collect(toList());
        excludedBRs.forEach(br -> result.registerWarning(format("BillingRun[id={%d}] has been ignored. " +
                                        "Only Billing runs with status=PREVALIDATED can be processed", br.getId())));
        result.setNbItemsProcessedWithWarning(excludedBRs.size());
        billingRuns.removeAll(excludedBRs);
    }
    /**
     * Assign invoice number and increment BA invoice dates.
     *
     * @param billingRun The billing run
     * @param nbRuns the nb runs
     * @param waitingMillis The waiting millis
     * @param jobInstanceId The job instance id
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
            Consumer<Long> task = (invoiceId) -> {
                invoiceService.recalculateDates(invoiceId);
                invoiceService.assignInvoiceNumber(invoiceId, invoicesToNumberInfo);
                invoiceService.updateStatus(invoiceId, InvoiceStatusEnum.VALIDATED);

            };
            iteratorBasedJobProcessing.processItems(jobExecutionResult, new SynchronizedIterator<Long>((Collection<Long>) invoiceIds), task, nbRuns, waitingMillis, false, JobSpeedEnum.VERY_FAST, true);

            List<Long> baIds = invoiceService.getBillingAccountIds(billingRun.getId(), invoicesToNumberInfo.getInvoiceTypeId(), invoicesToNumberInfo.getSellerId(), invoicesToNumberInfo.getInvoiceDate());

            // Increment next invoice date of a billing account
            task = (baId) -> {
                invoiceService.incrementBAInvoiceDate(billingRun, baId);
            };

            iteratorBasedJobProcessing.processItems(jobExecutionResult, new SynchronizedIterator<Long>((Collection<Long>) baIds), task, nbRuns, waitingMillis, false, JobSpeedEnum.FAST, false);
        }
    }

}