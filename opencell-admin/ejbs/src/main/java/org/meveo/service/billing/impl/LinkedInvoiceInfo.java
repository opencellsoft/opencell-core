package org.meveo.service.billing.impl;

public class LinkedInvoiceInfo {

    private Long invoiceNumber;
    private String invoiceDate;
    private Long amountWithTax;

    public LinkedInvoiceInfo(Long invoiceNumber, String invoiceDate, Long amountWithTax) {
        this.invoiceNumber = invoiceNumber;
        this.invoiceDate = invoiceDate;
        this.amountWithTax = amountWithTax;
    }


    public Long getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public Long getAmountWithTax() {
        return amountWithTax;
    }


}
