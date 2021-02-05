package org.meveo.model.billing;

import org.meveo.model.admin.Seller;
import org.meveo.model.cpq.commercial.InvoiceLine;
import org.meveo.model.payments.PaymentMethod;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class InvoiceLinesGroup implements Serializable {

    private BillingAccount billingAccount;
    private BillingCycle billingCycle;
    private Seller seller;
    private InvoiceType invoiceType;
    private boolean prepaid;
    private String invoiceKey;
    private PaymentMethod paymentMethod;
    private List<InvoiceLine> invoiceLines = new ArrayList<>();

    public InvoiceLinesGroup() { }

    public InvoiceLinesGroup(BillingAccount billingAccount, BillingCycle billingCycle, Seller seller, InvoiceType invoiceType,
                             boolean prepaid, String invoiceKey, PaymentMethod paymentMethod) {
        this.billingAccount = billingAccount;
        this.billingCycle = billingCycle;
        this.seller = seller;
        this.invoiceType = invoiceType;
        this.prepaid = prepaid;
        this.invoiceKey = invoiceKey;
        this.paymentMethod = paymentMethod;
    }

    public BillingAccount getBillingAccount() {
        return billingAccount;
    }

    public void setBillingAccount(BillingAccount billingAccount) {
        this.billingAccount = billingAccount;
    }

    public BillingCycle getBillingCycle() {
        return billingCycle;
    }

    public void setBillingCycle(BillingCycle billingCycle) {
        this.billingCycle = billingCycle;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    public boolean isPrepaid() {
        return prepaid;
    }

    public void setPrepaid(boolean prepaid) {
        this.prepaid = prepaid;
    }

    public String getInvoiceKey() {
        if (invoiceKey == null) {
            invoiceKey = format("%d_%d_%d_%s_%d", billingAccount.getId(), seller.getId(), invoiceType.getId(),
                    prepaid, getPaymentMethod().getId());
        }
        return invoiceKey;
    }

    public void setInvoiceKey(String invoiceKey) {
        this.invoiceKey = invoiceKey;
    }

    public PaymentMethod getPaymentMethod() {
        if (paymentMethod == null) {
            paymentMethod = billingAccount.getCustomerAccount().getPreferredPaymentMethod();
        }
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<InvoiceLine> getInvoiceLines() {
        return invoiceLines;
    }

    public void setInvoiceLines(List<InvoiceLine> invoiceLines) {
        this.invoiceLines = invoiceLines;
    }
}
