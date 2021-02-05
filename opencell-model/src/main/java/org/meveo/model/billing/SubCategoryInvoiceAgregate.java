/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.model.billing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.cpq.commercial.InvoiceLine;

/**
 * The Class SubCategoryInvoiceAgregate.
 */
@Entity
@DiscriminatorValue("F")
@NamedQueries({ @NamedQuery(name = "SubCategoryInvoiceAgregate.deleteByBR", query = "delete from SubCategoryInvoiceAgregate ia where ia.billingRun.id=:billingRunId"),
        @NamedQuery(name = "SubCategoryInvoiceAgregate.sumAmountsDiscountByBillingAccount", query =
                "select sum(ia.amountWithoutTax), sum(ia.amountWithTax), ia.invoice.id ,ia.billingAccount.id,  ia.billingAccount.customerAccount.id, ia.billingAccount.customerAccount.customer.id"
                        + " from  SubCategoryInvoiceAgregate ia where ia.billingRun.id=:billingRunId and ia.discountAggregate = true group by ia.invoice.id, ia.billingAccount.id, ia.billingAccount.customerAccount.id, ia.billingAccount.customerAccount.customer.id"),
        @NamedQuery(name = "SubCategoryInvoiceAgregate.sumAmountsDiscountByCustomerAccount", query =
                "select sum(ia.amountWithoutTax), sum(ia.amountWithTax), ia.invoice.id, ia.billingAccount.customerAccount.id"
                        + " from  SubCategoryInvoiceAgregate ia where ia.billingRun.id=:billingRunId and ia.discountAggregate = true group by ia.invoice.id, ia.billingAccount.customerAccount.id"),
        @NamedQuery(name = "SubCategoryInvoiceAgregate.sumAmountsDiscountByCustomer", query =
                "select sum(ia.amountWithoutTax), sum(ia.amountWithTax), ia.invoice.id, ia.billingAccount.customerAccount.customer.id"
                        + " from  SubCategoryInvoiceAgregate ia where ia.billingRun.id=:billingRunId and ia.discountAggregate = true group by ia.invoice.id, ia.billingAccount.customerAccount.customer.id"),
        @NamedQuery(name = "SubCategoryInvoiceAgregate.deleteByInvoiceIds", query = "delete from SubCategoryInvoiceAgregate ia where ia.invoice.id IN (:invoicesIds)") })
public class SubCategoryInvoiceAgregate extends InvoiceAgregate {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The invoice sub category. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoicesubcategory")
    private InvoiceSubCategory invoiceSubCategory;

    /** The category invoice agregate. */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "category_invoice_agregate")
    private CategoryInvoiceAgregate categoryInvoiceAgregate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;

    /** The wallet. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private WalletInstance wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounting_code_id")
    private AccountingCode accountingCode;

    /** The discount plan code. */
    @Column(name = "discount_plan_code", length = 50)
    @Size(max = 50)
    private String discountPlanCode;

    /** The discount plan item code. */
    @Column(name = "discount_plan_item_code", length = 50)
    @Size(max = 50)
    private String discountPlanItemCode;

    /**
     * Discount plan item applied
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_plan_item_id")
    private DiscountPlanItem discountPlanItem;

    /**
     * Discount percent applied
     */
    @Column(name = "discount_percent", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal discountPercent;

    // Field no longer used since v.5.2
    @Deprecated
    @Column(name = "quantity")
    private BigDecimal quantity;

    // Field no longer used since v.5.2
    @Deprecated
    @Column(name = "discount", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal discount;

    @Type(type = "numeric_boolean")
    @Column(name = "discount_aggregate", nullable = false)
    @NotNull
    private boolean discountAggregate;

    /** The old amount without tax. */
    @Transient
    private BigDecimal oldAmountWithoutTax;

    /** The old amount with tax. */
    @Transient
    private BigDecimal oldAmountWithTax;

    @Transient
    private List<RatedTransaction> ratedtransactionsToAssociate = new ArrayList<>();

    @Transient
    private List<InvoiceLine> invoiceLinesToAssociate = new ArrayList<>();

    /**
     * Tracks cumulative amounts by tax
     */
    @Transient
    private Map<Tax, BigDecimal> amountsByTax;

    /**
     * Instantiates a new sub category invoice aggregate.
     */
    public SubCategoryInvoiceAgregate() {

    }

    /**
     * Instantiates a new sub category invoice aggregate
     * 
     * @param invoiceSubCategory Invoice subcategory
     * @param billingAccount Billing account
     * @param userAccount User account
     * @param wallet Wallet instance
     * @param invoice Invoice
     * @param accountingCode Accounting code
     */
    public SubCategoryInvoiceAgregate(InvoiceSubCategory invoiceSubCategory, BillingAccount billingAccount, UserAccount userAccount, WalletInstance wallet, Invoice invoice, AccountingCode accountingCode) {
        super();
        this.invoiceSubCategory = invoiceSubCategory;
        this.billingAccount = billingAccount;
        this.userAccount = userAccount;
        this.wallet = wallet;
        this.invoice = invoice;
        if (invoice != null) {
            this.billingRun = invoice.getBillingRun();
        }
        this.accountingCode = accountingCode;
    }

    /**
     * Copies sub category invoice aggregate
     *
     * @param subCategoryInvoiceAgregate Sub category invoice aggregate to copy
     */
    public SubCategoryInvoiceAgregate(SubCategoryInvoiceAgregate subCategoryInvoiceAgregate) {
        this.setAccountingCode(subCategoryInvoiceAgregate.getAccountingCode());
        this.setInvoiceSubCategory(subCategoryInvoiceAgregate.getInvoiceSubCategory());
        this.setWallet(subCategoryInvoiceAgregate.getWallet());
        this.setItemNumber(subCategoryInvoiceAgregate.getItemNumber());
        this.setAmountWithoutTax(subCategoryInvoiceAgregate.getAmountWithoutTax());
        this.setAmountWithTax(subCategoryInvoiceAgregate.getAmountWithTax());
        this.setAmountTax(subCategoryInvoiceAgregate.getAmountTax());
        this.setBillingAccount(subCategoryInvoiceAgregate.getBillingAccount());
        this.setBillingRun(subCategoryInvoiceAgregate.getBillingRun());
        this.setUserAccount(subCategoryInvoiceAgregate.getUserAccount());
        this.setDiscountAggregate(false);
    }

    /**
     * Gets the invoice sub category.
     *
     * @return the invoice sub category
     */
    public InvoiceSubCategory getInvoiceSubCategory() {
        return invoiceSubCategory;
    }

    /**
     * Sets the invoice sub category.
     *
     * @param invoiceSubCategory the new invoice sub category
     */
    public void setInvoiceSubCategory(InvoiceSubCategory invoiceSubCategory) {
        this.invoiceSubCategory = invoiceSubCategory;
    }

    /**
     * Gets the category invoice agregate.
     *
     * @return the category invoice agregate
     */
    public CategoryInvoiceAgregate getCategoryInvoiceAgregate() {
        return categoryInvoiceAgregate;
    }

    /**
     * Sets the category invoice agregate.
     *
     * @param categoryInvoiceAgregate the new category invoice agregate
     */
    public void setCategoryInvoiceAgregate(CategoryInvoiceAgregate categoryInvoiceAgregate) {
        this.categoryInvoiceAgregate = categoryInvoiceAgregate;
        if (categoryInvoiceAgregate != null && categoryInvoiceAgregate.getSubCategoryInvoiceAgregates() != null) {
            categoryInvoiceAgregate.getSubCategoryInvoiceAgregates().add(this);
        }
    }

    /**
     * Associate Rated transaction to an aggregate, increment the itemNumber field value and sum up amounts.
     * 
     * @param ratedTransaction Rated transaction to associate
     * @param isEnterprise Is it enterprise/b2b installation - interested to track
     */
    public void addRatedTransaction(RatedTransaction ratedTransaction, boolean isEnterprise, boolean addAmounts) {
        
    	if (this.itemNumber == null) {
            this.itemNumber = 0;
        }
        this.itemNumber++;
        this.ratedtransactionsToAssociate.add(ratedTransaction);
        
        BigDecimal amount = isEnterprise?ratedTransaction.getAmountWithoutTax():ratedTransaction.getAmountWithTax();
        if(addAmounts) {
	        if (isEnterprise) {
	            addAmountWithoutTax(ratedTransaction.getAmountWithoutTax());
	        } else {
	            addAmountWithTax(ratedTransaction.getAmountWithTax());
	        }
	        addAmountTax(ratedTransaction.getAmountTax());
        }
        if (amountsByTax == null) {
            amountsByTax = new HashMap<>();
        }
        if (!amountsByTax.containsKey(ratedTransaction.getTax())) {
            amountsByTax.put(ratedTransaction.getTax(), amount);
        } else {
            amountsByTax.put(ratedTransaction.getTax(), amountsByTax.get(ratedTransaction.getTax()).add(amount));
        }
    }

    /**
     * @return User account
     */
    public UserAccount getUserAccount() {
        return userAccount;
    }

    /**
     * @param userAccount User account
     */
    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    /**
     * @return Wallet instance
     */
    public WalletInstance getWallet() {
        return wallet;
    }

    /**
     * @param wallet Wallet instance
     */
    public void setWallet(WalletInstance wallet) {
        this.wallet = wallet;
    }

    /**
     * @return Corresponding accounting code
     */
    public AccountingCode getAccountingCode() {
        return accountingCode;
    }

    /**
     * @param accountingCode Corresponding accounting code
     */
    public void setAccountingCode(AccountingCode accountingCode) {
        this.accountingCode = accountingCode;
    }

    /**
     * @return the discount plan code
     */
    public String getDiscountPlanCode() {
        return discountPlanCode;
    }

    /**
     * Sets the discount plan code.
     *
     * @param discountPlanCode the new discount plan code
     */
    public void setDiscountPlanCode(String discountPlanCode) {
        this.discountPlanCode = discountPlanCode;
    }

    /**
     * Gets the discount plan item code.
     *
     * @return the discount plan item code
     */
    public String getDiscountPlanItemCode() {
        return discountPlanItemCode;
    }

    /**
     * Sets the discount plan item code.
     *
     * @param discountPlanItemCode the new discount plan item code
     */
    public void setDiscountPlanItemCode(String discountPlanItemCode) {
        this.discountPlanItemCode = discountPlanItemCode;
    }

    /**
     * @return Discount percent applied
     */
    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    /**
     * @param discountPercent Discount percent applied
     */
    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }

    /**
     * Gets the old amount without tax.
     *
     * @return the old amount without tax
     */
    public BigDecimal getOldAmountWithoutTax() {
        return oldAmountWithoutTax;
    }

    /**
     * Sets the old amount without tax.
     *
     * @param oldAmountWithoutTax the new old amount without tax
     */
    public void setOldAmountWithoutTax(BigDecimal oldAmountWithoutTax) {
        this.oldAmountWithoutTax = oldAmountWithoutTax;
    }

    /**
     * Gets the old amount with tax.
     *
     * @return the old amount with tax
     */
    public BigDecimal getOldAmountWithTax() {
        return oldAmountWithTax;
    }

    /**
     * Sets the old amount with tax.
     *
     * @param oldAmountWithTax the new old amount with tax
     */
    public void setOldAmountWithTax(BigDecimal oldAmountWithTax) {
        this.oldAmountWithTax = oldAmountWithTax;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.model.BaseEntity#hashCode()
     */
    @Override
    public int hashCode() {
        if (id != null) {
            return id.intValue();
        } else if (invoiceSubCategory != null) {
            return invoiceSubCategory.hashCode();
        }

        final int prime = 31;
        int result = super.hashCode();
        result = prime * result;
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof SubCategoryInvoiceAgregate)) {
            return false;
        }

        SubCategoryInvoiceAgregate other = (SubCategoryInvoiceAgregate) obj;
        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }
        if (this.getInvoiceSubCategory() == null) {
            return false;
        }

        return this.getInvoiceSubCategory().getCode().equals(other.getInvoiceSubCategory().getCode())
                && ((this.getUserAccount() == null && other.getUserAccount() == null)
                        || (this.getUserAccount() != null && other.getUserAccount() != null && this.getUserAccount().getId().equals(other.getUserAccount().getId())))
                && this.isDiscountAggregate() == other.isDiscountAggregate();
    }

    @Override
    public String toString() {
        return "SubCategoryInvoiceAgregate [id=" + id + ",invoiceSubCategory=" + (invoiceSubCategory == null ? null : invoiceSubCategory.getCode()) + ", oldAmountWithoutTax=" + oldAmountWithoutTax + ", oldAmountWithTax="
                + oldAmountWithTax + "]";
    }

    /**
     * Field no longer used since v.5.2
     * 
     * @return Quantity
     */
    @Deprecated
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * Field no longer used since v.5.2
     * 
     * @param quantity Quantity
     */
    @Deprecated
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    /**
     * Field no longer used since v.5.2
     * 
     * @param quantity Quantity
     */
    @Deprecated
    public void addQuantity(BigDecimal quantity) {
        this.quantity = this.quantity.add(quantity);
    }

    /**
     * Field no longer used since v.5.2
     * 
     * @return Discount
     */
    @Deprecated
    public BigDecimal getDiscount() {
        return discount;
    }

    /**
     * Field no longer used since v.5.2
     * 
     * @param discount Discount
     */
    @Deprecated
    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    /**
     * @return Is this a discount aggregate
     */
    public boolean isDiscountAggregate() {
        return discountAggregate;
    }

    /**
     * @param discountAggregate Is this a discount aggregate
     */
    public void setDiscountAggregate(boolean discountAggregate) {
        this.discountAggregate = discountAggregate;
    }

    /**
     * @return Discount applied
     */
    public DiscountPlanItem getDiscountPlanItem() {
        return discountPlanItem;
    }

    /**
     * @param discountPlanItem Discount applied
     */
    public void setDiscountPlanItem(DiscountPlanItem discountPlanItem) {
        this.discountPlanItem = discountPlanItem;
    }

    public BigDecimal getIsEnterpriseAmount(boolean isEnterprise) {
        return isEnterprise ? getAmountWithoutTax() : getAmountWithTax();
    }

    /**
     * A transient method.
     * 
     * @return A list of rated transactions to associate with an invoice subcategory aggregate.
     */
    public List<RatedTransaction> getRatedtransactionsToAssociate() {
        return ratedtransactionsToAssociate;
    }

    /**
     * A transient method.
     * 
     * @param ratedtransactionsToAssociate A list of rated transactions to associate with an invoice subcategory aggregate
     */
    public void setRatedtransactionsToAssociate(List<RatedTransaction> ratedtransactionsToAssociate) {
        this.ratedtransactionsToAssociate = ratedtransactionsToAssociate;
    }

    /**
     * Compute derived amounts amountWithoutTax/amountWithTax/amountTax. If taxPercent is null, or ZERO returned amountWithoutTax and amountWithTax values will be the same
     * (whichone, depending on isEnterprise value)
     * 
     * @param isEnterprise Is application used used in B2B (base prices are without tax) or B2C mode (base prices are with tax)
     * @param rounding Rounding precision to apply
     * @param roundingMode Rounding mode to apply
     */
    public void computeDerivedAmounts(boolean isEnterprise, int invoiceRounding, RoundingMode roundingMode) {

        BigDecimal[] amounts = NumberUtils.computeDerivedAmountsWoutTaxPercent(getAmountWithoutTax(), getAmountWithTax(), getAmountTax(), isEnterprise, invoiceRounding, roundingMode);
        setAmountWithoutTax(amounts[0]);
        setAmountWithTax(amounts[1]);
        setAmountTax(amounts[2]);
    }

    /**
     * @return Cumulative amounts by tax
     */
    public Map<Tax, BigDecimal> getAmountsByTax() {
        return amountsByTax;
    }

    /**
     * @param amountsByTax Cumulative amounts by tax
     */
    public void setAmountsByTax(Map<Tax, BigDecimal> amountsByTax) {
        this.amountsByTax = amountsByTax;
    }

    public List<InvoiceLine> getInvoiceLinesToAssociate() {
        return invoiceLinesToAssociate;
    }

    public void setInvoiceLinesToAssociate(List<InvoiceLine> invoiceLinesToAssociate) {
        this.invoiceLinesToAssociate = invoiceLinesToAssociate;
    }

    public void addInvoiceLine(InvoiceLine invoiceLine, boolean isEnterprise, boolean addAmounts) {

        if (this.itemNumber == null) {
            this.itemNumber = 0;
        }
        this.itemNumber++;
        this.invoiceLinesToAssociate.add(invoiceLine);

        BigDecimal amount = isEnterprise? invoiceLine.getAmountWithoutTax(): invoiceLine.getAmountWithTax();
        if(addAmounts) {
            if (isEnterprise) {
                addAmountWithoutTax(invoiceLine.getAmountWithoutTax());
            } else {
                addAmountWithTax(invoiceLine.getAmountWithTax());
            }
            addAmountTax(invoiceLine.getAmountTax());
        }
        if (amountsByTax == null) {
            amountsByTax = new HashMap<>();
        }
        if (!amountsByTax.containsKey(invoiceLine.getTax())) {
            amountsByTax.put(invoiceLine.getTax(), amount);
        } else {
            amountsByTax.put(invoiceLine.getTax(), amountsByTax.get(invoiceLine.getTax()).add(amount));
        }
    }
}