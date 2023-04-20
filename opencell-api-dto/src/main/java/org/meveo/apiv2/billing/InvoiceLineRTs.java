package org.meveo.apiv2.billing;

import java.util.List;

public class InvoiceLineRTs {

    private long invoiceLineId;

    List<Long> ratedTransactionsId;

    public long getInvoiceLineId() {
        return invoiceLineId;
    }

    public void setInvoiceLineId(long invoiceLineId) {
        this.invoiceLineId = invoiceLineId;
    }

    public List<Long> getRatedTransactionsId() {
        return ratedTransactionsId;
    }

    public void setRatedTransactionsId(List<Long> ratedTransactionsId) {
        this.ratedTransactionsId = ratedTransactionsId;
    }
}
