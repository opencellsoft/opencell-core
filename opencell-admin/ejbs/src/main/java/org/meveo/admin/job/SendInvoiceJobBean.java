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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.billing.Invoice;
import org.meveo.model.communication.email.MailingTypeEnum;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;

/**
 * A bean used to send invoices by Email
 * 
 * @author HORRI Khalid
 * @lastModifiedVersion 7.0
 */
public class SendInvoiceJobBean extends BaseJobBean {

    @Inject
    protected Logger log;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    InvoiceService invoiceService;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        Boolean sendDraft = Boolean.FALSE;
        if(this.getParamOrCFValue(jobInstance, "sendDraft") != null) {
            sendDraft = (Boolean) this.getParamOrCFValue(jobInstance, "sendDraft");
        }

        List<String> billingRunCodes = null;
        Date invoiceDateRangeFrom = null;
        Date invoiceDateRangeTo = null;

        if(this.getParamOrCFValue(jobInstance, "SendInvoiceJob_billingCycle") != null){
            billingRunCodes = ((List<EntityReferenceWrapper>) this.getParamOrCFValue(jobInstance, "SendInvoiceJob_billingRun"))
                    .stream()
                    .map(EntityReferenceWrapper::getCode)
                    .collect(Collectors.toList());

        }
        if(this.getParamOrCFValue(jobInstance, "invoiceDateRangeFrom") != null){
           invoiceDateRangeFrom = ValueExpressionWrapper.evaluateExpression((String) this.getParamOrCFValue(jobInstance, "invoiceDateRangeFrom"), new HashMap<>(), Date.class);
        }
        if(this.getParamOrCFValue(jobInstance, "invoiceDateRangeTo") != null){
            invoiceDateRangeTo = ValueExpressionWrapper.evaluateExpression((String) this.getParamOrCFValue(jobInstance, "invoiceDateRangeTo"), new HashMap<>(), Date.class);
        }

        HashMap<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("context", jobInstance);
        String overrideEmailEl = (String) this.getParamOrCFValue(jobInstance, "overrideEmailEl");
        try {

            List<Invoice> invoices = invoiceService.findByNotAlreadySentAndDontSend(billingRunCodes, invoiceDateRangeFrom, invoiceDateRangeTo);
            result.setNbItemsToProcess(invoices.size());
            jobExecutionService.initCounterElementsRemaining(result, invoices.size());
            int i = 0;
            for (Invoice invoice : invoices) {
                String overrideEmail = invoiceService.evaluateOverrideEmail(overrideEmailEl, userMap, invoice);
                i++;
                if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
                    break;
                }
                if (invoiceService.isDraft(invoice) && !sendDraft) {
                    log.warn("Sending draft invoice is deactivated");
                    continue;
                }
                if (!hasPdf(invoice)) {
                    log.warn("Pdf file not found for the invoice:" + invoice.getId());
                    jobExecutionService.registerWarning(result, "Pdf file not found");
                    continue;
                }
                Boolean isSent = invoiceService.sendByEmail(invoice, MailingTypeEnum.BATCH, overrideEmail);
                if (!isSent) {
                    jobExecutionService.registerError(result, "could not send the invoice by Email");
                    continue;
                }
                jobExecutionService.registerSucces(result);
                jobExecutionService.decCounterElementsRemaining(result);
            }

        } catch (BusinessException e) {
            log.error("Failed to run the job : SendInvoiceJob", e);
            jobExecutionService.registerError(result, e.getMessage());
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
