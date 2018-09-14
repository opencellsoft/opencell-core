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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.meveo.model.catalog.DiscountPlanItem;

@Entity
@DiscriminatorValue("F")
public class SubCategoryInvoiceAgregate extends InvoiceAgregate {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoicesubcategory")
    private InvoiceSubCategory invoiceSubCategory;

    // Field no longer used since v.5.2. Use tax and taxPercent fields instead
    @Deprecated
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "billing_invoice_agregate_taxes", joinColumns = @JoinColumn(name = "sub_cat_invoice_aggregat_id"), inverseJoinColumns = @JoinColumn(name = "tax_id"))
    private Set<Tax> subCategoryTaxes = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "category_invoice_agregate")
    private CategoryInvoiceAgregate categoryInvoiceAgregate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private WalletInstance wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounting_code_id")
    private AccountingCode accountingCode;

    @OneToMany(mappedBy = "invoiceAgregateF", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RatedTransaction> ratedtransactions = new ArrayList<>();

    @Column(name = "discount_plan_code", length = 50)
    @Size(max = 50)
    private String discountPlanCode;

    @Column(name = "discount_plan_item_code", length = 50)
    @Size(max = 50)
    private String discountPlanItemCode;

    /**
     * Discount plan item applied
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_plan_item_id")
    private DiscountPlanItem discountPlanItem;

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

    @Transient
    private BigDecimal oldAmountWithoutTax;

    @Transient
    private BigDecimal oldAmountWithTax;

    public SubCategoryInvoiceAgregate() {

    }

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

    public InvoiceSubCategory getInvoiceSubCategory() {
        return invoiceSubCategory;
    }

    public void setInvoiceSubCategory(InvoiceSubCategory invoiceSubCategory) {
        this.invoiceSubCategory = invoiceSubCategory;
    }

    public CategoryInvoiceAgregate getCategoryInvoiceAgregate() {
        return categoryInvoiceAgregate;
    }

    public void setCategoryInvoiceAgregate(CategoryInvoiceAgregate categoryInvoiceAgregate) {
        this.categoryInvoiceAgregate = categoryInvoiceAgregate;
        if (categoryInvoiceAgregate != null && categoryInvoiceAgregate.getSubCategoryInvoiceAgregates() != null) {
            categoryInvoiceAgregate.getSubCategoryInvoiceAgregates().add(this);
        }
    }

    public List<RatedTransaction> getRatedtransactions() {
        return ratedtransactions;
    }

    public void setRatedtransactions(List<RatedTransaction> ratedtransactions) {
        this.ratedtransactions = ratedtransactions;
    }

    public WalletInstance getWallet() {
        return wallet;
    }

    public void setWallet(WalletInstance wallet) {
        this.wallet = wallet;
    }

    public AccountingCode getAccountingCode() {
        return accountingCode;
    }

    public void setAccountingCode(AccountingCode accountingCode) {
        this.accountingCode = accountingCode;
    }

    public Set<Tax> getSubCategoryTaxes() {
        return subCategoryTaxes;
    }

    public void setSubCategoryTaxes(Set<Tax> subCategoryTaxes) {
        this.subCategoryTaxes = subCategoryTaxes;
    }

    public void addSubCategoryTax(Tax subCategoryTax) {
        if (subCategoryTaxes == null) {
            subCategoryTaxes = new HashSet<Tax>();
        }
        if (subCategoryTax != null) {
            subCategoryTaxes.add(subCategoryTax);
        }
    }

    public String getDiscountPlanCode() {
        return discountPlanCode;
    }

    public void setDiscountPlanCode(String discountPlanCode) {
        this.discountPlanCode = discountPlanCode;
    }

    public String getDiscountPlanItemCode() {
        return discountPlanItemCode;
    }

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

    public BigDecimal getOldAmountWithoutTax() {
        return oldAmountWithoutTax;
    }

    public void setOldAmountWithoutTax(BigDecimal oldAmountWithoutTax) {
        this.oldAmountWithoutTax = oldAmountWithoutTax;
    }

    public BigDecimal getOldAmountWithTax() {
        return oldAmountWithTax;
    }

    public void setOldAmountWithTax(BigDecimal oldAmountWithTax) {
        this.oldAmountWithTax = oldAmountWithTax;
    }

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

        if (invoiceSubCategory == null) {
            if (other.getInvoiceSubCategory() != null) {
                return false;
            }
        } else if (!invoiceSubCategory.equals(other.getInvoiceSubCategory())) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SubCategoryInvoiceAgregate [id=" + id + ",invoiceSubCategory=" + (invoiceSubCategory == null ? null : invoiceSubCategory.getCode()) + ", oldAmountWithoutTax="
                + oldAmountWithoutTax + ", oldAmountWithTax=" + oldAmountWithTax + "]";
    }

    public Set<Tax> getSubCategoryTaxesTransient() {
        return subCategoryTaxesTransient;
    }

    public void setSubCategoryTaxesTransient(Set<Tax> subCategoryTaxesTransient) {
        this.subCategoryTaxesTransient = subCategoryTaxesTransient;
    }

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
     */
    @Deprecated
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * Field no longer used since v.5.2
     */
    @Deprecated
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    /**
     * Field no longer used since v.5.2
     * 
     */
    @Deprecated
    public void addQuantity(BigDecimal quantity) {
        this.quantity = this.quantity.add(quantity);
    }

    /**
     * Field no longer used since v.5.2
     * 
     */
    @Deprecated
    public BigDecimal getDiscount() {
        return discount;
    }

    /**
     * Field no longer used since v.5.2
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
}