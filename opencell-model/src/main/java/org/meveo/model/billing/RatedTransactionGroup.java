package org.meveo.model.billing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.meveo.model.admin.Seller;

/**
 * Rated transactions split for invoicing based on Billing account, seller and invoice type
 * 
 * @author Andrius Karpavicius
 */
@SuppressWarnings("serial")
public class RatedTransactionGroup implements Serializable {

    /**
     * Billing account
     */
    private BillingAccount billingAccount;
    /**
     * Billing cycle
     */
    private BillingCycle billingCycle;

    /**
     * Seller
     */
    private Seller seller;

    /**
     * Invoice type
     */
    private InvoiceType invoiceType;

    /**
     * Rated transactions
     */
    private List<RatedTransaction> ratedTransactions = new ArrayList<RatedTransaction>();

    public RatedTransactionGroup() {

    }

    /**
     * Constructror
     * 
     * @param billingAccount Billing account
     * @param seller Seller
     * @param billingCycle Billing cycle
     * @param invoiceType Invoice type
     */
    public RatedTransactionGroup(BillingAccount billingAccount, Seller seller, BillingCycle billingCycle, InvoiceType invoiceType) {
        this.billingAccount = billingAccount;
        this.seller = seller;
        this.billingCycle = billingCycle;
        this.invoiceType = invoiceType;
    }

    /**
     * @return Billing account
     */
    public BillingAccount getBillingAccount() {
        return billingAccount;
    }

    /**
     * @param billingAccount Billing account
     */
    public void setBillingAccount(BillingAccount billingAccount) {
        this.billingAccount = billingAccount;
    }

    /**
     * @return Seller
     */
    public Seller getSeller() {
        return seller;
    }

    /**
     * @param seller Seller
     */
    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    /**
     * @return Rated transactions
     */
    public List<RatedTransaction> getRatedTransactions() {
        return ratedTransactions;
    }

    /**
     * @param ratedTransactions Rated transactions
     */
    public void setRatedTransactions(List<RatedTransaction> ratedTransactions) {
        this.ratedTransactions = ratedTransactions;
    }

    /**
     * @return Billing cycle
     */
    public BillingCycle getBillingCycle() {
        return billingCycle;
    }

    /**
     * @param billingCycle Billing cycle
     */
    public void setBillingCycle(BillingCycle billingCycle) {
        this.billingCycle = billingCycle;
    }

    /**
     * @return Invoice type
     */
    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    /**
     * @param invoiceType Invoice type
     */
    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }
}