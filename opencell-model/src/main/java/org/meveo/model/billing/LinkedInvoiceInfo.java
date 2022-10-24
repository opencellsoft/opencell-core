package org.meveo.model.billing;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class LinkedInvoiceInfo {

    private String invoiceNumber;
    private Timestamp invoiceDate;
    private BigDecimal amountWithTax;

    public LinkedInvoiceInfo(String invoiceNumber, Timestamp invoiceDate, BigDecimal amountWithTax) {
        this.invoiceNumber = invoiceNumber;
        this.invoiceDate = invoiceDate;
        this.amountWithTax = amountWithTax;
    }


    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public Timestamp getInvoiceDate() {
        return invoiceDate;
    }

    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

}
