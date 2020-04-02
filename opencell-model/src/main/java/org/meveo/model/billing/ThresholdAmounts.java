package org.meveo.model.billing;

import java.io.Serializable;
import java.util.List;

public class ThresholdAmounts implements Serializable {

    /**
     * The discount amount.
     */
    private Amounts amount;

    /**
     * list of invoice.
     */
    private List<Long> invoices;

    /**
     * Default constructor.
     */
    public ThresholdAmounts() {
    }

    /**
     * @param amounts
     * @param invoices
     */
    public ThresholdAmounts(Amounts amounts, List<Long> invoices) {
        this.amount = amounts;
        this.invoices = invoices;
    }

    public Amounts getAmount() {
        return amount;
    }

    public void setAmount(Amounts amount) {
        this.amount = amount;
    }

    public List<Long> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Long> invoices) {
        this.invoices = invoices;
    }
}
