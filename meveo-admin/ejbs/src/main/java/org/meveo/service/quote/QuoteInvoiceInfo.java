package org.meveo.service.quote;

import java.util.Date;
import java.util.List;

import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.Subscription;

public class QuoteInvoiceInfo {

    private String quoteCode;
    private List<String> cdrs;
    private Subscription subscription;
    private List<ProductInstance> productInstances;
    private Date fromDate;
    private Date toDate;

    public QuoteInvoiceInfo(String quoteCode, List<String> cdrs, Subscription subscription, List<ProductInstance> productInstances, Date fromDate, Date toDate) {
        super();
        this.quoteCode = quoteCode;
        this.cdrs = cdrs;
        this.subscription = subscription;
        this.productInstances = productInstances;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getQuoteCode() {
        return quoteCode;
    }

    public List<String> getCdrs() {
        return cdrs;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public List<ProductInstance> getProductInstances() {
        return productInstances;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    @Override
    public String toString() {
        return String.format("QuoteInvoiceInfo [quoteCode=%s, fromDate=%s, toDate=%s]", quoteCode, fromDate, toDate);
    }
}