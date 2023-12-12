package org.meveo.admin.job;

import static java.util.Arrays.asList;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.job.utils.BillinRunApplicationElFilterUtils;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.BillingRunExtensionService;

@Stateless
public class XMLInvoiceGenerationJobV2Bean extends IteratorBasedJobBean<Long> {

    private static final long serialVersionUID = 595704895612703257L;

    @Inject
    private BillingRunService billingRunService;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private BillingRunExtensionService billingRunExtensionService;
    
    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::generateXml, null, null, null);
    }

    /**
     * Initialize job settings and retrieve data to process
     *
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list of Invoices to generate XML files
     */
    private Optional<Iterator<Long>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {
        JobInstance jobInstance = jobExecutionResult.getJobInstance();
        List<String> status =
                (List<String>) this.getParamOrCFValue(jobInstance, "invoicesToProcess", asList("VALIDATED"));
        List<InvoiceStatusEnum> statusList = status.stream()
                .map(InvoiceStatusEnum::valueOf)
                .collect(toList());
        String parameter = jobInstance.getParametres();
        Long billingRunId = null;
        if (parameter != null && parameter.trim().length() > 0) {
            try {
                billingRunId = Long.parseLong(parameter);
            } catch (Exception e) {
                log.error("Can not extract billing run ID from a parameter {}", parameter, e);
                jobExecutionResult.addErrorReport(e.getMessage());
            }
        }
        
        if (billingRunId != null) {
            BillingRun billingRun = billingRunService.findById(billingRunId);
            if (billingRun != null) {
                if (!BillinRunApplicationElFilterUtils.isToProcessBR(billingRun, jobInstance)) {
                    log.warn("BillingRun applicationEl='{}' is evaluate to 'false', abort current process.", billingRun.getApplicationEl());
                    return of(new SynchronizedIterator<>(Collections.emptyList()));
                }

                billingRunExtensionService.updateBillingRunWithXMLPDFExecutionResult(billingRunId,
                        jobExecutionResult.getId(), null);
                billingRunService.updateBillingRunJobExecution(billingRun.getId(), jobExecutionResult);
                billingRunService.refreshOrRetrieve(billingRun);
            }
        }

        List<Long> invoiceIds = this.fetchInvoiceIdsToProcess(statusList, billingRunId);
        return of(new SynchronizedIterator<>(invoiceIds));
    }

    /**
     * Generate XML file
     *
     * @param invoiceId Invoice id to create XML for
     * @param jobExecutionResult Job execution result
     */
    private void generateXml(Long invoiceId, JobExecutionResultImpl jobExecutionResult) {
        Invoice invoice = invoiceService.findById(invoiceId);
        invoiceService.produceInvoiceXml(invoice, null, false);
    }

    private List<Long> fetchInvoiceIdsToProcess(List<InvoiceStatusEnum> statusList, Long billingRunId) {
        log.debug("fetchInvoiceIdsToProcess for InvoiceStatusEnums = {} and billingRunId = {} ", statusList, billingRunId);
        return invoiceService.listInvoicesWithoutXml(billingRunId, statusList);
    }
}