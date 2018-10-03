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
import javax.validation.constraints.Size;

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

    /** The sub category taxes. */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "billing_invoice_agregate_taxes", joinColumns = @JoinColumn(name = "sub_cat_invoice_aggregat_id"), inverseJoinColumns = @JoinColumn(name = "tax_id"))
    private Set<Tax> subCategoryTaxes = new HashSet<>();

    /** The category invoice agregate. */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "category_invoice_agregate")
    private CategoryInvoiceAgregate categoryInvoiceAgregate;

    /** The wallet. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private WalletInstance wallet;

    /** The ratedtransactions. */
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
	
	/** The sub category taxes transient. */
	@Transient
	private Set<Tax> subCategoryTaxesTransient;

    /** The discount percent. */
    @Column(name = "discount_percent", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal discountPercent;

    /** The old amount without tax. */
    @Transient
    private BigDecimal oldAmountWithoutTax;

    /** The old amount with tax. */
    @Transient
    private BigDecimal oldAmountWithTax;

    /**
     * Instantiates a new sub category invoice agregate.
     */
    public SubCategoryInvoiceAgregate() {

    }

    /**
     * Instantiates a new sub category invoice agregate.
     *
     * @param subCategoryInvoiceAgregate the sub category invoice agregate
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
     * Gets the ratedtransactions.
     *
     * @return the ratedtransactions
     */
    public List<RatedTransaction> getRatedtransactions() {
        return ratedtransactions;
    }

    /**
     * Sets the ratedtransactions.
     *
     * @param ratedtransactions the new ratedtransactions
     */
    public void setRatedtransactions(List<RatedTransaction> ratedtransactions) {
        this.ratedtransactions = ratedtransactions;
    }

    /**
     * Gets the wallet.
     *
     * @return the wallet
     */
    public WalletInstance getWallet() {
        return wallet;
    }

    /**
     * Sets the wallet.
     *
     * @param wallet the new wallet
     */
    public void setWallet(WalletInstance wallet) {
        this.wallet = wallet;
    }

    /**
     * Gets the sub category taxes.
     *
     * @return the sub category taxes
     */
    public Set<Tax> getSubCategoryTaxes() {
        return subCategoryTaxes;
    }

    /**
     * Sets the sub category taxes.
     *
     * @param subCategoryTaxes the new sub category taxes
     */
    public void setSubCategoryTaxes(Set<Tax> subCategoryTaxes) {
        this.subCategoryTaxes = subCategoryTaxes;
    }

    /**
     * Adds the sub category tax.
     *
     * @param subCategoryTax the sub category tax
     */
    public void addSubCategoryTax(Tax subCategoryTax) {
        if (subCategoryTaxes == null) {
            subCategoryTaxes = new HashSet<Tax>();
        }
        if (subCategoryTax != null) {
            subCategoryTaxes.add(subCategoryTax);
        }
    }

    /**
     * Gets the discount plan code.
     *
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
     * Gets the discount percent.
     *
     * @return the discount percent
     */
    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    /**
     * Sets the discount percent.
     *
     * @param discountPercent the new discount percent
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

    /* (non-Javadoc)
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

    /* (non-Javadoc)
     * @see org.meveo.model.billing.InvoiceAgregate#equals(java.lang.Object)
     */
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

}