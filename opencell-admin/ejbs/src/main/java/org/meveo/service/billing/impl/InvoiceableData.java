package org.meveo.service.billing.impl;

import java.io.Serializable;
import java.math.BigDecimal;

import org.meveo.model.billing.IInvoiceable;
import org.meveo.model.billing.Tax;

/**
 * The synthesized information to invoice - extract of RT data - to calculate invoice aggregate and invoice amounts
 * 
 * @author Andrius Karpavicius
 *
 */
public class InvoiceableData implements IInvoiceable, Serializable {

    private static final long serialVersionUID = 5359749229280296086L;

    private Long id;

    private Long billingAccountId;

    private Long userAccountId;

    private Long sellerId;

    private Long subscriptionId;

    private Long walletId;

    private Long taxClassId;

    private String orderNumber;

    private BigDecimal unitAmountWithoutTax;

    private BigDecimal unitAmountWithTax;

    private BigDecimal unitAmountTax;

    private BigDecimal amountWithoutTax;

    private BigDecimal amountWithTax;

    private BigDecimal amountTax;

    private Long taxId;

    private BigDecimal taxPercent;

    private Tax tax;

    private boolean prepaid;

    private boolean taxRecalculated;

    private Long invoiceSubCategoryId;

    public InvoiceableData() {

    }

    public InvoiceableData(Long id, Long billingAccountId, Long userAccountId, Long sellerId, Long subscriptionId, Long walletId, Long taxClassId, String orderNumber, BigDecimal unitAmountWithoutTax,
            BigDecimal unitAmountWithTax, BigDecimal unitAmountTax, BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal amountTax, Long taxId, BigDecimal taxPercent, boolean prepaid,
            Long invoiceSubCategoryId) {
        super();
        this.id = id;
        this.billingAccountId = billingAccountId;
        this.userAccountId = userAccountId;
        this.sellerId = sellerId;
        this.subscriptionId = subscriptionId;
        this.walletId = walletId;
        this.taxClassId = taxClassId;
        this.orderNumber = orderNumber;
        this.unitAmountWithoutTax = unitAmountWithoutTax;
        this.unitAmountWithTax = unitAmountWithTax;
        this.unitAmountTax = unitAmountTax;
        this.amountWithoutTax = amountWithoutTax;
        this.amountWithTax = amountWithTax;
        this.amountTax = amountTax;
        this.taxId = taxId;
        this.taxPercent = taxPercent;
        this.prepaid = prepaid;
        this.invoiceSubCategoryId = invoiceSubCategoryId;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Long getBillingAccountId() {
        return billingAccountId;
    }

    @Override
    public Long getSellerId() {
        return sellerId;
    }

    @Override
    public Long getSubscriptionId() {
        return subscriptionId;
    }

    @Override
    public BigDecimal getUnitAmountWithoutTax() {
        return unitAmountWithoutTax;
    }

    @Override
    public void setUnitAmountWithoutTax(BigDecimal unitAmountWithoutTax) {
        this.unitAmountWithoutTax = unitAmountWithoutTax;

    }

    @Override
    public BigDecimal getUnitAmountWithTax() {
        return unitAmountWithTax;

    }

    @Override
    public void setUnitAmountWithTax(BigDecimal unitAmountWithTax) {
        this.unitAmountWithTax = unitAmountWithTax;
    }

    @Override
    public BigDecimal getUnitAmountTax() {
        return unitAmountTax;
    }

    @Override
    public void setUnitAmountTax(BigDecimal unitAmountTax) {
        this.unitAmountTax = unitAmountTax;
    }

    @Override
    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    @Override
    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    @Override
    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    @Override
    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithoutTax = amountWithTax;
    }

    @Override
    public BigDecimal getAmountTax() {
        return amountTax;
    }

    @Override
    public void setAmountTax(BigDecimal amountTax) {
        this.amountTax = amountTax;
    }

    @Override
    public Long getTaxId() {
        return taxId;
    }

    @Override
    public Tax getTax() {
        return tax;
    }

    @Override
    public void setTax(Tax tax) {
        this.taxId = tax.getId();
        this.tax = tax;
    }

    @Override
    public BigDecimal getTaxPercent() {
        return taxPercent;
    }

    @Override
    public void setTaxPercent(BigDecimal taxPercent) {
        this.taxPercent = taxPercent;
    }

    @Override
    public boolean isPrepaid() {
        return prepaid;
    }

    public Long getInvoiceSubCategoryId() {
        return invoiceSubCategoryId;
    }

    public boolean isTaxRecalculated() {
        return taxRecalculated;
    }

    @Override
    public void setTaxRecalculated(boolean taxRecalculated) {
        this.taxRecalculated = taxRecalculated;
    }

    @Override
    public Long getUserAccountId() {
        return userAccountId;
    }

    @Override
    public Long getWalletId() {
        return walletId;
    }

    @Override
    public Long getTaxClassId() {
        return taxClassId;
    }

    @Override
    public String getOrderNumber() {
        return orderNumber;
    }

}