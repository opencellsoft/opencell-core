package org.meveo.admin.job;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.communication.email.MailingTypeEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Created by HORRI on 07/01/2019.
 */
public class SendInvoiceJobBean extends BaseJobBean {

    @Inject
    protected Logger log;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    InvoiceService invoiceService;
    @Inject
    private InvoiceTypeService invoiceTypeService;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        Boolean sendDraft = Boolean.valueOf((String) this.getParamOrCFValue(jobInstance, "sendDraft"));
        HashMap<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("context", jobInstance);
        String overrideEmailEl = (String) this.getParamOrCFValue(jobInstance, "overrideEmailEl");
        try {

            List<Invoice> invoices = invoiceService.findByNotAlreadySentAndDontSend();
            result.setNbItemsToProcess(invoices.size());
            for (Invoice invoice : invoices) {
                String overrideEmail = invoiceService.evaluateOverrideEmail(overrideEmailEl, userMap, invoice);
                if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
                    break;
                }
                if (invoiceService.isDraft(invoice) && !sendDraft) {
                    log.warn("Sending draft invoice is deactivated");
                    continue;
                }
                if (!hasPdf(invoice)) {
                    log.warn("Pdf file not found for the invoice:" + invoice.getId());
                    result.registerWarning("Pdf file not found");
                    continue;
                }
                Boolean isSent = invoiceService.sendByEmail(invoice, MailingTypeEnum.BATCH, overrideEmail);
                if (!isSent) {
                    result.registerError("could not send the invoice by Email");
                    continue;
                }
                result.registerSucces();
            }

        } catch (BusinessException e) {
            log.error("Failed to run the job : SendInvoiceJob", e);
            result.registerError(e.getMessage());
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
        if (!pdfFile.exists()) {
            return false;
        }
        return true;

    }
}


