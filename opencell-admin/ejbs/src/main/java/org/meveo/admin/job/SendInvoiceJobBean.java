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

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.exception.ValidationException;
import org.meveo.model.billing.Invoice;
import org.meveo.model.communication.email.MailingTypeEnum;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.InvoiceService;

/**
 * /** Job implementation to send invoice PDF by email
 * 
 * @author HORRI Khalid
 * @lastModifiedVersion 7.0
 */
public class SendInvoiceJobBean extends IteratorBasedJobBean<Invoice> {

    private static final long serialVersionUID = -6541907298222206427L;

    @Inject
    private InvoiceService invoiceService;

    private String overrideEmailEl;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::sendByEmail, null, null);
    }

    /**
     * Initialize job settings and retrieve data to process
     * 
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list of Wallet operation Ids to re-rate
     */
    @SuppressWarnings("unchecked")
    private Optional<Iterator<Invoice>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {

        boolean sendDraft = (boolean) this.getParamOrCFValue(jobExecutionResult.getJobInstance(), "sendDraft", false);

        List<String> billingRunCodes = null;
        Date invoiceDateRangeFrom = null;
        Date invoiceDateRangeTo = null;

        if (this.getParamOrCFValue(jobExecutionResult.getJobInstance(), "SendInvoiceJob_billingCycle") != null) {
            billingRunCodes = ((List<EntityReferenceWrapper>) this.getParamOrCFValue(jobExecutionResult.getJobInstance(), "SendInvoiceJob_billingRun")).stream().map(EntityReferenceWrapper::getCode)
                .collect(Collectors.toList());

        }
        if (this.getParamOrCFValue(jobExecutionResult.getJobInstance(), "invoiceDateRangeFrom") != null) {
            invoiceDateRangeFrom = ValueExpressionWrapper.evaluateExpression((String) this.getParamOrCFValue(jobExecutionResult.getJobInstance(), "invoiceDateRangeFrom"), new HashMap<>(), Date.class);
        }
        if (this.getParamOrCFValue(jobExecutionResult.getJobInstance(), "invoiceDateRangeTo") != null) {
            invoiceDateRangeTo = ValueExpressionWrapper.evaluateExpression((String) this.getParamOrCFValue(jobExecutionResult.getJobInstance(), "invoiceDateRangeTo"), new HashMap<>(), Date.class);
        }

        overrideEmailEl = (String) this.getParamOrCFValue(jobExecutionResult.getJobInstance(), "overrideEmailEl");

        List<Invoice> invoices = invoiceService.findByNotAlreadySentAndDontSend(billingRunCodes, invoiceDateRangeFrom, invoiceDateRangeTo, sendDraft);

        return Optional.of(new SynchronizedIterator<Invoice>(invoices));
    }

    /**
     * Send invoice PDF by email
     * 
     * @param invoice Invoice to send
     * @param jobExecutionResult Job execution result
     */
    private void sendByEmail(Invoice invoice, JobExecutionResultImpl jobExecutionResult) {

        HashMap<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("context", jobExecutionResult.getJobInstance());

        String overrideEmail = invoiceService.evaluateOverrideEmail(overrideEmailEl, userMap, invoice);

        if (!hasPdf(invoice)) {
            throw new ValidationException("Pdf file not found for Invoice " + invoice.getId());
        }

        invoice = invoiceService.retrieveIfNotManaged(invoice);

        boolean isSent = invoiceService.sendByEmail(invoice, MailingTypeEnum.BATCH, overrideEmail);
        if (!isSent) {
            throw new ValidationException("Could not send Invoice " + invoice.getId());
        }
    }

    private boolean hasPdf(Invoice invoice) {
        if (invoice.getPdfFilename() == null) {
            return false;
        }
        String pdfPath = invoiceService.getFullPdfFilePath(invoice, false);
        if (pdfPath == null) {
            return false;
        }
        File pdfFile = new File(pdfPath);
        return pdfFile.exists();

    }
}