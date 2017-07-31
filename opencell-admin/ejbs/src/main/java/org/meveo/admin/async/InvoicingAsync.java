/**
 * 
 */
package org.meveo.admin.async;

import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.model.billing.BillingRun;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoicesToNumberInfo;
import org.slf4j.Logger;

/**
 * @author anasseh
 *
 */

@Stateless
public class InvoicingAsync {

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    protected Logger log;

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<Integer> updateBillingAccountTotalAmountsAsync(List<Long> billingAccountIds, BillingRun billingRun) {
        int count = 0;
        for (Long billingAccountId : billingAccountIds) {
            if (billingAccountService.updateBillingAccountTotalAmounts(billingAccountId, billingRun)) {
                count++;
            }
        }
        log.info("WorkSet billableBA:" + count);
        return new AsyncResult<Integer>(new Integer(count));
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> createAgregatesAndInvoiceAsync(List<Long> billingAccountIds, BillingRun billingRun) {

        for (Long billingAccountId : billingAccountIds) {
            try {
                invoiceService.createAgregatesAndInvoice(billingAccountId, billingRun, null, null, null, null);
            } catch (Exception e) {
                log.error("Error for BA=" + billingAccountId + " : " + e);
            }
        }
        return new AsyncResult<String>("OK");
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> assignInvoiceNumberAndIncrementBAInvoiceDatesAsync(List<Long> invoiceIds, InvoicesToNumberInfo invoicesToNumberInfo) {

        for (Long invoiceId : invoiceIds) {
            try {
                invoiceService.assignInvoiceNumberAndIncrementBAInvoiceDate(invoiceId, invoicesToNumberInfo);
            } catch (Exception e) {
                log.error("Failed to increment invoice date for invoice {}", invoiceId, e);
            }
        }
        return new AsyncResult<String>("OK");
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> generatePdfAsync(List<Long> invoiceIds, JobExecutionResultImpl result) {
        for (Long invoiceId : invoiceIds) {
            try {
                invoiceService.produceInvoicePdfInNewTransaction(invoiceId);
                result.registerSucces();
            } catch (Exception e) {
                result.registerError(invoiceId, e.getMessage());
                log.error("Failed to create PDF invoice for invoice {}", invoiceId, e);
            }
        }

        return new AsyncResult<String>("OK");
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<Boolean> generateXmlAsync(List<Long> invoiceIds, JobExecutionResultImpl result) {

        boolean allOk = true;

        for (Long invoiceId : invoiceIds) {
            long startDate = System.currentTimeMillis();
            try {
                invoiceService.produceInvoiceXmlInNewTransaction(invoiceId);
                result.registerSucces();
            } catch (Exception e) {
                result.registerError(invoiceId, e.getMessage());
                allOk = false;
                log.error("Failed to create XML invoice for invoice {}", invoiceId, e);
            }
            log.info("Invoice creation delay :" + (System.currentTimeMillis() - startDate));
        }

        return new AsyncResult<Boolean>(allOk);
    }
}