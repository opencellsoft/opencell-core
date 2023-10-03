/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.admin.job;

import java.util.*;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.job.utils.BillinRunApplicationElFilterUtils;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.Invoice;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.BillingRunExtensionService;
import org.meveo.service.billing.impl.InvoiceService;

import static java.util.Optional.of;

/**
 * Job definition to generate PDF for all valid invoices that don't have it.
 * 
 * @author Andrius Karpavicius
 */
@Stateless
public class PDFInvoiceGenerationJobBean extends IteratorBasedJobBean<Long> {

    private static final long serialVersionUID = 4420234995792447633L;
    
    @Inject
    private BillingRunService billingRunService;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private BillingRunExtensionService billingRunExtensionService;

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::convertToPdf, this::convertToPdfBatch, null, null, this::clearJasperReportCache);
    }

    /**
     * Initialize job settings and retrieve data to process
     * 
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list of Invoices to generate PDF files
     */
    private Optional<Iterator<Long>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {

        JobInstance jobInstance = jobExecutionResult.getJobInstance();

        InvoicesToProcessEnum invoicesToProcessEnum = InvoicesToProcessEnum.valueOf((String) this.getParamOrCFValue(jobInstance, "invoicesToProcess", "FinalOnly"));
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
                billingRunExtensionService.updateBillingRunWithXMLPDFExecutionResult(billingRunId, null, jobExecutionResult.getId());
                billingRunService.updateBillingRunJobExecution(billingRun.getId(), jobExecutionResult);
                billingRunService.refreshOrRetrieve(billingRun);
            }

        }

        List<Long> ids = this.fetchInvoiceIdsToProcess(invoicesToProcessEnum, billingRunId);

        return Optional.of(new SynchronizedIterator<>(ids));
    }

    /**
     * Generate PDF file
     * 
     * @param invoiceId Invoice id to create PDF for
     * @param jobExecutionResult Job execution result
     */
    private void convertToPdf(Long invoiceId, JobExecutionResultImpl jobExecutionResult) {

        Invoice invoice = invoiceService.findById(invoiceId, Arrays.asList("billingAccount"));
        invoiceService.produceInvoicePdf(invoice, null);
    }

    /**
     * Generate PDF files
     * 
     * @param invoiceIds Invoice ids to create PDF for
     * @param jobExecutionResult Job execution result
     */
    private void convertToPdfBatch(List<Long> invoiceIds, JobExecutionResultImpl jobExecutionResult) {

        List<Invoice> invoices = invoiceService.findByIds(invoiceIds, Arrays.asList("billingAccount"));
        for (Invoice invoice : invoices) {
            invoiceService.produceInvoicePdf(invoice, null);
        }
    }

    private List<Long> fetchInvoiceIdsToProcess(InvoicesToProcessEnum invoicesToProcessEnum, Long billingRunId) {

        log.debug(" fetchInvoiceIdsToProcess for invoicesToProcessEnum = {} and billingRunId = {} ", invoicesToProcessEnum, billingRunId);
        List<Long> invoiceIds = null;

        switch (invoicesToProcessEnum) {
        case FinalOnly:
            invoiceIds = invoiceService.getInvoicesIdsValidatedWithNoPdf(billingRunId);
            break;

        case DraftOnly:
            invoiceIds = invoiceService.getDraftInvoiceIdsByBRWithNoPdf(billingRunId);
            break;

        case All:
            invoiceIds = invoiceService.getInvoiceIdsIncludeDraftByBRWithNoPdf(billingRunId);
            break;
        }
        return invoiceIds;

    }

    /**
     * Clear cached Jasper reports
     * 
     * @param jobExecutionResult Job execution result
     */
    private void clearJasperReportCache(JobExecutionResultImpl jobExecutionResult) {
        InvoiceService.clearJasperReportCache();
    }
}