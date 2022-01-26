package org.meveo.service.billing.invoicing.impl;

import java.io.Serializable;
import java.math.BigDecimal;

import org.meveo.model.billing.Amounts;


public class BillingRunSummary implements Serializable {

    private static final long serialVersionUID = 3109687834951882877L;

    private Long billingAccountsCount;
    
    private Long firstBillingAccountId;
    
    private Long lastBillingAccountId;

    /**
     * Amounts to invoice
     */
    private Amounts amountsToInvoice;

    /**
     * Constructor
     */
    public BillingRunSummary() {
    }

    /**
     * Constructor
     * 
     * @param entityToInvoiceId ID of an entity to invoice
     * @param amountsToInvoice Amounts to invoice
     */
    public BillingRunSummary(Long BillingAccountsCount, BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal amountTax, long firstBillingAccountId, long lastBillingAccountId) {
        this.billingAccountsCount = BillingAccountsCount;
        this.lastBillingAccountId = lastBillingAccountId;
        this.firstBillingAccountId = firstBillingAccountId;
        this.amountsToInvoice = new Amounts(amountWithoutTax, amountWithTax, amountTax);
    }

    /**
     * @return Amounts to invoice
     */
    public Amounts getAmountsToInvoice() {
        return amountsToInvoice;
    }

    /**
     * @param amountsToInvoice Amounts to invoice
     */
    public void setAmountsToInvoice(Amounts amountsToInvoice) {
        this.amountsToInvoice = amountsToInvoice;
    }

	/**
	 * @return the billingAccountsCount
	 */
	public Long getBillingAccountsCount() {
		return billingAccountsCount;
	}

	/**
	 * @param billingAccountsCount the billingAccountsCount to set
	 */
	public void setBillingAccountsCount(Long billingAccountsCount) {
		billingAccountsCount = billingAccountsCount;
	}

	/**
	 * @return the firstBillingAccountId
	 */
	public Long getFirstBillingAccountId() {
		return firstBillingAccountId;
	}

	/**
	 * @param firstBillingAccountId the firstBillingAccountId to set
	 */
	public void setFirstBillingAccountId(Long firstBillingAccountId) {
		this.firstBillingAccountId = firstBillingAccountId;
	}

	/**
	 * @return the lastBillingAccountId
	 */
	public Long getLastBillingAccountId() {
		return lastBillingAccountId;
	}

	/**
	 * @param lastBillingAccountId the lastBillingAccountId to set
	 */
	public void setLastBillingAccountId(Long lastBillingAccountId) {
		this.lastBillingAccountId = lastBillingAccountId;
	}
}