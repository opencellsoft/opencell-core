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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Iterator;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.model.billing.Invoice;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.InvoiceService;

/**
 * Job implementation to generate invoice XML for all valid invoices that don't have it
 * 
 * @author Andrius Karpavicius
 */
@Stateless
public class XMLInvoiceGenerationJobBean extends IteratorBasedJobBean<Long> {

    private static final long serialVersionUID = 7948947993905799076L;

    @Inject
    private InvoiceService invoiceService;

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::generateXml, null, null);
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

        List<String> statusNamesList = (List<String>) this.getParamOrCFValue(jobInstance, "invoicesToProcess", Arrays.asList("VALIDATED"));
        List<InvoiceStatusEnum> statusList = statusNamesList.stream().map(status->InvoiceStatusEnum.valueOf(status)).collect(Collectors.toList());
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
        return Optional.of(new SynchronizedIterator<>(invoiceIds));
    }

    /**
     * Generate XML file
     * 
     * @param invoiceId Invoice id to create XML for
     * @param jobExecutionResult Job execution result
     */
    private void generateXml(Long invoiceId, JobExecutionResultImpl jobExecutionResult) {

        Invoice invoice = invoiceService.findById(invoiceId);
        invoiceService.produceInvoiceXml(invoice, null);
    }

    private List<Long> fetchInvoiceIdsToProcess(List<InvoiceStatusEnum> statusList, Long billingRunId) {
        log.debug(" fetchInvoiceIdsToProcess for InvoiceStatusEnums = {} and billingRunId = {} ", statusList, billingRunId);
        return invoiceService.listInvoicesWithoutXml(billingRunId, statusList);
    }
}