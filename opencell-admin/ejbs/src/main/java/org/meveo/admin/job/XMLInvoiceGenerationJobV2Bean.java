package org.meveo.admin.job;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.InvoiceService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

@Stateless
public class XMLInvoiceGenerationJobV2Bean extends IteratorBasedJobBean<Long> {

    @Inject
    private InvoiceService invoiceService;

    @Override
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess,
                this::generateXml, null, null);
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