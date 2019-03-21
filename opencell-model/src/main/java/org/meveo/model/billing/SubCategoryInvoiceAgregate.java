/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.meveo.model.catalog.DiscountPlanItem;

/**
 * The Class SubCategoryInvoiceAgregate.
 */
@Entity
@DiscriminatorValue("F")
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

    @OneToMany(mappedBy = "invoiceAgregateF", fetch = FetchType.LAZY)
    private List<RatedTransaction> ratedtransactions = new ArrayList<>();

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

    /** The sub category taxes transient. */
    @Transient
    private Set<Tax> subCategoryTaxesTransient;

    /**
     * Discount percent applied
     */
    @Column(name = "discount_percent", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal discountPercent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_id")
    private Tax tax;

    @Column(name = "tax_percent", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal taxPercent;

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
     * @param tax Tax applied
     * @param taxPercent Tax percent applied
     * @param invoice Invoice
     * @param accountingCode Accounting code
     */
    public SubCategoryInvoiceAgregate(InvoiceSubCategory invoiceSubCategory, BillingAccount billingAccount, UserAccount userAccount, WalletInstance wallet, Tax tax,
            BigDecimal taxPercent, Invoice invoice, AccountingCode accountingCode) {
        super();
        this.invoiceSubCategory = invoiceSubCategory;
        this.billingAccount = billingAccount;
        this.userAccount = userAccount;
        this.wallet = wallet;
        this.tax = tax;
        this.taxPercent = taxPercent;
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
        this.setQuantity(subCategoryInvoiceAgregate.getQuantity());
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
     * @return Associated Rated transactions
     */
    public List<RatedTransaction> getRatedtransactions() {
        return ratedtransactions;
    }

    /**
     * Associated Rated transactions
     *
     * @param ratedtransactions Associated Rated transactions
     */
    public void setRatedtransactions(List<RatedTransaction> ratedtransactions) {
        this.ratedtransactions = ratedtransactions;
    }

    /**
     * Associate Rated transaction to an aggregate and increment the itemNumber field value.
     * 
     * @param ratedTransaction Rated transaction to associate
     */
    public void addRatedTransaction(RatedTransaction ratedTransaction) {
        if (this.itemNumber == null) {
            this.itemNumber = 0;
        }
        this.itemNumber++;
        this.ratedtransactions.add(ratedTransaction);
        ratedTransaction.setInvoiceAgregateF(this);
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
        if (id != null)
            return id.intValue();
        if (invoiceSubCategory != null)
            return invoiceSubCategory.hashCode();

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
                && this.isDiscountAggregate() == other.isDiscountAggregate() && this.getTax().getId().equals(other.getTax().getId())
                && this.getTaxPercent().compareTo(other.getTaxPercent()) == 0;
    }

    @Override
    public String toString() {
        return "SubCategoryInvoiceAgregate [id=" + id + ",invoiceSubCategory=" + (invoiceSubCategory == null ? null : invoiceSubCategory.getCode()) + ", oldAmountWithoutTax="
                + oldAmountWithoutTax + ", oldAmountWithTax=" + oldAmountWithTax + "]";
    }

    /**
     * Gets the sub category taxes transient.
     *
     * @return the sub category taxes transient
     */
    public Set<Tax> getSubCategoryTaxesTransient() {
        return subCategoryTaxesTransient;
    }

    /**
     * Sets the sub category taxes transient.
     *
     * @param subCategoryTaxesTransient the new sub category taxes transient
     */
    public void setSubCategoryTaxesTransient(Set<Tax> subCategoryTaxesTransient) {
        this.subCategoryTaxesTransient = subCategoryTaxesTransient;
    }

    /**
     * Adds the sub category tax transient.
     *
     * @param subCategoryTax the sub category tax
     */
    public void addSubCategoryTaxTransient(Tax subCategoryTax) {
        if (subCategoryTaxesTransient == null) {
            subCategoryTaxesTransient = new HashSet<>();
        }
        if (subCategoryTax != null) {
            subCategoryTaxesTransient.add(subCategoryTax);
        }
    }

    public Tax getTax() {
        return tax;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }

    public BigDecimal getTaxPercent() {
        return taxPercent;
    }

    public void setTaxPercent(BigDecimal taxPercent) {
        this.taxPercent = taxPercent;
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
}