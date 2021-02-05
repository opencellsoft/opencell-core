package org.meveo.service.billing.impl;

import static java.util.Collections.emptyList;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.IBillableEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.Subscription;
import org.meveo.model.cpq.commercial.InvoiceLine;
import org.meveo.model.filter.Filter;
import org.meveo.model.order.Order;
import org.meveo.service.base.BusinessService;
import org.meveo.service.filter.FilterService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.util.Date;
import java.util.List;

@Stateless
public class InvoiceLinesService extends BusinessService<InvoiceLine> {

    @Inject
    private FilterService filterService;

    public List<InvoiceLine> listInvoiceLinesToInvoice(IBillableEntity entityToInvoice, Date firstTransactionDate,
                                              Date lastTransactionDate, Filter filter, int pageSize) throws BusinessException {
        if (filter != null) {
            return (List<InvoiceLine>) filterService.filteredListAsObjects(filter, null);

        } else if (entityToInvoice instanceof Subscription) {
            return getEntityManager().createNamedQuery("InvoiceLine.listToInvoiceBySubscription", InvoiceLine.class)
                    .setParameter("subscriptionId", entityToInvoice.getId())
                    .setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate)
                    .setHint("org.hibernate.readOnly", true)
                    .setMaxResults(pageSize)
                    .getResultList();
        } else if (entityToInvoice instanceof BillingAccount) {
            return getEntityManager().createNamedQuery("InvoiceLine.listToInvoiceByBillingAccount", InvoiceLine.class)
                    .setParameter("billingAccountId", entityToInvoice.getId())
                    .setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate)
                    .setHint("org.hibernate.readOnly", true)
                    .setMaxResults(pageSize)
                    .getResultList();

        } else if (entityToInvoice instanceof Order) {
            return getEntityManager().createNamedQuery("InvoiceLine.listToInvoiceByOrderNumber", InvoiceLine.class)
                    .setParameter("orderNumber", ((Order) entityToInvoice).getOrderNumber())
                    .setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate)
                    .setHint("org.hibernate.readOnly", true)
                    .setMaxResults(pageSize)
                    .getResultList();
        }
        return emptyList();
    }

    public List<InvoiceLine> loadInvoiceLinesByBRs(List<BillingRun> billingRuns) {
        try {
            return getEntityManager().createNamedQuery("InvoiceLine.InvoiceLinesByBRs", InvoiceLine.class)
                    .setParameter("BillingRus", billingRuns)
                    .getResultList();
        } catch (NoResultException e) {
            log.warn("No invoice found for the provided billing runs");
            return emptyList();
        }
    }

    public List<InvoiceLine> loadInvoiceLinesByBRId(long BillingRunId) {
        try {
            return getEntityManager().createNamedQuery("InvoiceLine.InvoiceLinesByBRID", InvoiceLine.class)
                    .setParameter("billingRunId", BillingRunId)
                    .getResultList();
        } catch (NoResultException e) {
            log.warn("No invoice found for the provided billing runs");
            return emptyList();
        }
    }
}