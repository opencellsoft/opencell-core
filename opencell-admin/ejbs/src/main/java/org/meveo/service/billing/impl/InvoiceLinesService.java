package org.meveo.service.billing.impl;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.IBillableEntity;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.Subscription;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.InvoiceLine;
import org.meveo.model.cpq.commercial.OrderProduct;
import org.meveo.model.filter.Filter;
import org.meveo.model.order.Order;
import org.meveo.service.base.BusinessService;
import org.meveo.service.filter.FilterService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static java.util.Collections.emptyList;

@Stateless
public class InvoiceLinesService extends BusinessService<InvoiceLine> {

    @Inject
    private FilterService filterService;

    public List<InvoiceLine> findByCommercialOrder(CommercialOrder commercialOrder) {
        return getEntityManager().createNamedQuery("InvoiceLine.findByCommercialOrder", InvoiceLine.class)
                .setParameter("commercialOrder", commercialOrder)
                .getResultList();
    }

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
    
    public List<InvoiceLine> listInvoiceLinesByInvoice(long invoiceId) {
        try {
            return getEntityManager().createNamedQuery("InvoiceLine.InvoiceLinesByInvoiceID", InvoiceLine.class)
                    .setParameter("invoiceId", invoiceId)
                    .getResultList();
        } catch (NoResultException e) {
            log.warn("No invoice found for the provided Invoice : "+invoiceId);
            return emptyList();
        }
    }

    public void createInvoiceLine(CommercialOrder commercialOrder, AccountingArticle accountingArticle, OrderProduct orderProduct, BigDecimal amountWithoutTaxToBeInvoiced, BigDecimal amountWithTaxToBeInvoiced, BigDecimal taxAmountToBeInvoiced, BigDecimal totalTaxRate) {
        InvoiceLine invoiceLine = new InvoiceLine();
        invoiceLine.setCode("COMMERCIAL-GEN");
        invoiceLine.setCode(findDuplicateCode(invoiceLine));
        invoiceLine.setAccountingArticle(accountingArticle);
        invoiceLine.setLabel(accountingArticle.getDescription());
        invoiceLine.setProduct(orderProduct.getProductVersion().getProduct());
        invoiceLine.setProductVersion(orderProduct.getProductVersion());
        invoiceLine.setCommercialOrder(commercialOrder);
        invoiceLine.setOrderLot(orderProduct.getOrderServiceCommercial());
        invoiceLine.setQuantity(BigDecimal.valueOf(1));
        invoiceLine.setUnitPrice(amountWithoutTaxToBeInvoiced);
        invoiceLine.setAmountWithoutTax(amountWithoutTaxToBeInvoiced);
        invoiceLine.setAmountWithTax(amountWithTaxToBeInvoiced);
        invoiceLine.setAmountTax(taxAmountToBeInvoiced);
        invoiceLine.setTaxRate(totalTaxRate);
        invoiceLine.setOrderNumber(commercialOrder.getOrderNumber());
        invoiceLine.setBillingAccount(commercialOrder.getBillingAccount());
        invoiceLine.setValueDate(new Date());
        create(invoiceLine);
    }
}
