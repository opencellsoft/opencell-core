package org.meveo.admin.job;

import static java.lang.String.format;
import static java.math.BigDecimal.ZERO;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.meveo.model.billing.BillingProcessTypesEnum.AUTOMATIC;
import static org.meveo.model.billing.BillingProcessTypesEnum.FULL_AUTOMATIC;
import static org.meveo.model.billing.BillingRunStatusEnum.DRAFT_INVOICES;
import static org.meveo.model.billing.BillingRunStatusEnum.INVOICES_CREATED;
import static org.meveo.model.billing.BillingRunStatusEnum.INVOICE_LINES_CREATED;
import static org.meveo.model.billing.BillingRunStatusEnum.NEW;
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
import javax.ws.rs.BadRequestException;

import org.apache.commons.collections.map.HashedMap;
import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ImportInvoiceException;
import org.meveo.admin.exception.InvoiceExistException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.billing.BillingProcessTypesEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunAutomaticActionEnum;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.InvoiceSequence;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.scripts.ScriptInstance;
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
    private ServiceSingleton serviceSingleton;

    @Inject
    private IteratorBasedJobProcessing iteratorBasedJobProcessing;
    
    private static BigDecimal amountTax = ZERO;
    private static BigDecimal amountWithTax = ZERO;
    private static BigDecimal amountWithoutTax = ZERO;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running InvoiceSplitJob with parameter={}", jobInstance.getParametres());
            List<EntityReferenceWrapper> billingRunWrappers =
                    (List<EntityReferenceWrapper>) this.getParamOrCFValue(jobInstance, "InvoicingJobV2_billingRun");
            List<Long> billingRunIds = billingRunWrappers != null ? extractBRIds(billingRunWrappers) : emptyList();
            Map<String, Object> filters = new HashedMap();
            if (billingRunIds.isEmpty()) {
                filters.put("status", INVOICE_LINES_CREATED);
            } else {
                filters.put("inList id", billingRunIds);
            }
            PaginationConfiguration paginationConfiguration = new PaginationConfiguration(filters);
            paginationConfiguration.setFetchFields(Arrays.asList("billingCycle", "billingCycle.billingRunValidationScript"));
            List<BillingRun> billingRuns = billingRunService.list(paginationConfiguration);
            if (billingRuns.isEmpty()) {
                List<String> errors = List.of("No valid billing run with status=INVOICE_LINES_CREATED found");
                result.setErrors(errors);
            } else {
                validateBRList(billingRuns, result);
                for (BillingRun billingRun : billingRuns) {
                    executeBillingRun(billingRun, jobInstance, result,
                            billingRun.getBillingCycle() != null ? billingRun.getBillingCycle().getBillingRunValidationScript() : null, true);
                    initAmounts();
                }
            }
        
    }

    private List<Long> extractBRIds(List<EntityReferenceWrapper> billingRunWrappers) {
        return billingRunWrappers.stream()
                    .map(br -> Long.valueOf(br.getCode().split("/")[0]))
                    .collect(toList());
    }

    private void validateBRList(List<BillingRun> billingRuns, JobExecutionResultImpl result) {
        List<BillingRun> excludedBRs = billingRuns.stream()
                                            .filter(br -> br.getStatus() == NEW)
                                            .collect(toList());
        excludedBRs.forEach(br -> result.registerWarning(format("BillingRun[id={%d}] has been ignored. " +
                                        "Only Billing runs with status=INVOICE_LINES_CREATED can be processed", br.getId())));
        result.setNbItemsProcessedWithWarning(excludedBRs.size());
        billingRuns.removeAll(excludedBRs);
    }

    private void executeBillingRun(BillingRun billingRun, JobInstance jobInstance, JobExecutionResultImpl result, ScriptInstance billingRunValidationScript, boolean v11Process) {
    	boolean prevalidatedAutomaticPrevBRStatus = false;
    	boolean firstPassAutomatic = billingRun.getStatus() == INVOICE_LINES_CREATED && billingRun.getProcessType() == AUTOMATIC;
    	
        if(billingRun.getStatus() == INVOICE_LINES_CREATED
                && (billingRun.getProcessType() == AUTOMATIC || billingRun.getProcessType() == FULL_AUTOMATIC)) {
            billingRun.setStatus(PREVALIDATED);
        }
        if(billingRun.getStatus() == PREVALIDATED) {
            billingRun.setStatus(INVOICES_CREATED);
            billingRunService.createAggregatesAndInvoiceWithIl(billingRun, 1, 0, jobInstance.getId(), result, v11Process );
            billingRun = billingRunService.refreshOrRetrieve(billingRun);
            billingRun.setPrAmountWithTax(amountWithTax);
            billingRun.setPrAmountWithoutTax(amountWithoutTax);
            billingRun.setPrAmountTax(amountTax);
            billingRun.setStatus(DRAFT_INVOICES);
            prevalidatedAutomaticPrevBRStatus = true;
            billingRunService.applyThreshold(billingRun.getId());
            invoiceService.applyligibleInvoiceForAdvancement(billingRun.getId());
        }
        if(billingRun.getStatus() == DRAFT_INVOICES && billingRun.getProcessType() == FULL_AUTOMATIC) {
            billingRun.setStatus(POSTVALIDATED);
        }
        if(billingRunValidationScript != null && billingRun.getBillingCycle() != null) {
            billingRun.getBillingCycle().setBillingRunValidationScript(billingRunValidationScript);
        }

		if((billingRun.getRejectAutoAction() != null && 
		        billingRun.getRejectAutoAction().equals(BillingRunAutomaticActionEnum.MOVE)) 
		    || (billingRun.getSuspectAutoAction() != null && 
		        billingRun.getSuspectAutoAction().equals(BillingRunAutomaticActionEnum.MOVE))
		    || !billingRunService.isBRValid(billingRun)) {
            billingRun.setStatus(REJECTED);
        }
		
		billingRun = billingRunService.update(billingRun);
		try{
		    billingRunService.executeBillingRunValidationScript(billingRun);
		} catch (BusinessException exception) {		    
		    if(billingRunService.isBillingRunContainingRejectedInvoices(billingRun.getId())) {
                billingRun.setStatus(REJECTED);
                billingRunService.update(billingRun);
                throw new BadRequestException(exception.getMessage());
            }
        }
		
		billingRun = billingRunService.refreshOrRetrieve(billingRun);
		
		if ((billingRun.getProcessType() == BillingProcessTypesEnum.FULL_AUTOMATIC || billingRun.getProcessType() == BillingProcessTypesEnum.AUTOMATIC) 
                && (BillingRunStatusEnum.POSTINVOICED.equals(billingRun.getStatus()) 
                        || BillingRunStatusEnum.POSTVALIDATED.equals(billingRun.getStatus())
                        || BillingRunStatusEnum.DRAFT_INVOICES.equals(billingRun.getStatus())
                        || BillingRunStatusEnum.REJECTED.equals(billingRun.getStatus()))) {
            billingRunService.applyAutomaticValidationActions(billingRun);
            billingRun = billingRunService.refreshOrRetrieve(billingRun);
            if(!billingRunService.isBRValid(billingRun)) {
                billingRun.setStatus(REJECTED);
            }
            else {
                if(billingRun.getProcessType() == BillingProcessTypesEnum.FULL_AUTOMATIC) {
                    billingRun.setStatus(POSTVALIDATED);
                }else if(billingRun.getProcessType() == BillingProcessTypesEnum.AUTOMATIC && prevalidatedAutomaticPrevBRStatus) {
                    billingRun.setStatus(DRAFT_INVOICES);
                } else {
                    billingRun.setStatus(POSTVALIDATED);
                }
            }
        }
        
        if(!firstPassAutomatic || billingRun.getStatus() == POSTVALIDATED) {
            assignInvoiceNumberAndIncrementBAInvoiceDatesAndGenerateAO(billingRun, result);
            if(!billingRunService.isBillingRunContainingRejectedInvoices(billingRun.getId())) {
                billingRun.setStatus(VALIDATED);
            }else{
                billingRun.setStatus(REJECTED);
            }
        }
		
        billingRunService.update(billingRun);
        billingRunService.updateBillingRunStatistics(billingRun);
        billingRunService.updateBillingRunJobExecution(billingRun.getId(), result);

    }

    /**
     * Assign invoice number and increment BA invoice dates.
     *
     * @param billingRun The billing run
     * @param jobExecutionResult the Job execution result
     * @throws BusinessException the business exception
     */
    public void assignInvoiceNumberAndIncrementBAInvoiceDatesAndGenerateAO(BillingRun billingRun, JobExecutionResultImpl jobExecutionResult) throws BusinessException {

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
            BiConsumer<Long, JobExecutionResultImpl> task =
                    (invoiceId, jobResult) -> invoiceService.assignInvoiceNumberAndRecalculateDates(invoiceId, invoicesToNumberInfo);
            iteratorBasedJobProcessing.processItems(jobExecutionResult, new SynchronizedIterator<>(invoiceIds), task, null, null, nbRuns, waitingMillis, false, true);

            List<Long> baIds = invoiceService.getBillingAccountIds(billingRun.getId(), invoicesToNumberInfo.getInvoiceTypeId(), invoicesToNumberInfo.getSellerId(), invoicesToNumberInfo.getInvoiceDate());

            // Increment next invoice date of a billing account
            task = (baId, jobResult) -> invoiceService.incrementBAInvoiceDate(billingRun, baId);

            iteratorBasedJobProcessing.processItems(jobExecutionResult, new SynchronizedIterator<>(baIds), task, null, null, nbRuns, waitingMillis, false, false);
            
            if(billingRun.getGenerateAO()) {
            	task = (invoiceId, jobResult) -> {
    				try {
    					invoiceService.generateRecordedInvoiceAO(invoiceId);
    				} catch (BusinessException | InvoiceExistException | ImportInvoiceException e) {
    					throw new BusinessException(String.format("Error while trying to generate Account Operation for BR: %s, Invoice: %s", billingRun.getId(), invoiceId));
    				}
    			};
    			iteratorBasedJobProcessing.processItems(jobExecutionResult, new SynchronizedIterator<>(invoiceIds), task, null, null, nbRuns, waitingMillis, false, false);
            }
        }
    }

    public static void addNewAmounts(BigDecimal amountTax, BigDecimal amountWithoutTax, BigDecimal amountWithTax) {
        InvoicingJobV2Bean.amountTax = InvoicingJobV2Bean.amountTax.add(amountTax);
        InvoicingJobV2Bean.amountWithTax = InvoicingJobV2Bean.amountWithTax.add(amountWithTax);
        InvoicingJobV2Bean.amountWithoutTax = InvoicingJobV2Bean.amountWithoutTax.add(amountWithoutTax);
    }

    private static void initAmounts() {
        amountTax = ZERO;
        amountWithTax = ZERO;
        amountWithoutTax = ZERO;
    }
}
