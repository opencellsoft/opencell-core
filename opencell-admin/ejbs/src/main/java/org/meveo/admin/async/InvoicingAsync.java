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

import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.InvoiceService;
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
    public Future<Integer> updateBillingAccountTotalAmountsAsync(List<BillingAccount> billingAccounts, BillingRun billingRun) {
        int count = 0;
        for (BillingAccount billingAccount : billingAccounts) {
            if (billingAccountService.updateBillingAccountTotalAmounts(billingAccount, billingRun)) {
                count++;
            }
        }
        log.info("WorkSet billableBA:" + count);
        return new AsyncResult<Integer>(new Integer(count));
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> createAgregatesAndInvoiceAsync(List<BillingAccount> billingAccounts, BillingRun billingRun) {

        for (BillingAccount billingAccount : billingAccounts) {
            try {
                invoiceService.createAgregatesAndInvoice(billingAccount, billingRun, null, null, null, null);
            } catch (Exception e) {
                log.error("Error for BA=" + billingAccount.getCode() + " : " + e);
            }
        }
        return new AsyncResult<String>("OK");
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> incrementInvoiceDatesAsync(List<Long> invoiceIds) {

        for (Long invoiceId : invoiceIds) {
            try {
                invoiceService.incrementInvoiceDates(invoiceId);
            } catch (Exception e) {
                log.error("Failed to increment invoice date for invoice {}", invoiceId, e);
            }
        }
        return new AsyncResult<String>("OK");
    }
}