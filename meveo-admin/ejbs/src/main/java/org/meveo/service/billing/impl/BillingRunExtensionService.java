package org.meveo.service.billing.impl;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.Invoice;
import org.meveo.service.base.PersistenceService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class BillingRunExtensionService extends PersistenceService<BillingRun> {

    @Inject
    private InvoiceService invoiceService;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BillingRun incrementInvoiceDatesAndValidate(BillingRun billingRun, User currentUser) throws BusinessException {
        log.debug("incrementInvoiceDatesAndValidate");
        billingRun = findById(billingRun.getId(), true);
        
        for (Invoice invoice : billingRun.getInvoices()) {
            invoice.setInvoiceNumber(invoiceService.getInvoiceNumber(invoice, currentUser));
            invoice.setPdf(null);
            BillingAccount billingAccount = invoice.getBillingAccount();
            Date initCalendarDate = billingAccount.getSubscriptionDate();
            if (initCalendarDate == null) {
                initCalendarDate = billingAccount.getAuditable().getCreated();
            }
            Date nextCalendarDate = billingAccount.getBillingCycle().getNextCalendarDate(initCalendarDate);
            billingAccount.setNextInvoiceDate(nextCalendarDate);
            billingAccount.updateAudit(currentUser);
            invoiceService.update(invoice, currentUser);
        }
        billingRun.setStatus(BillingRunStatusEnum.VALIDATED);
        billingRun = update(billingRun, currentUser);
        return billingRun;
    }

}
