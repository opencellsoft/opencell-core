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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * The Class CategoryInvoiceAgregate.
 */
@Entity
@DiscriminatorValue("R")
public class CategoryInvoiceAgregate extends InvoiceAgregate {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The invoice category. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoicecategory")
    private InvoiceCategory invoiceCategory;

    /** The sub category invoice agregates. */
    @OneToMany(mappedBy = "categoryInvoiceAgregate", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates = new HashSet<>();

    /**
     * Instantiates a new category invoice agregate.
     */
    public CategoryInvoiceAgregate() {

    }

    /**
     * Instantiates a new category invoice agregate.
     *
     * @param categoryInvoiceAgregate the category invoice agregate
     */
    public CategoryInvoiceAgregate(CategoryInvoiceAgregate categoryInvoiceAgregate) {
        this.setInvoiceCategory(categoryInvoiceAgregate.getInvoiceCategory());
        this.setItemNumber(categoryInvoiceAgregate.getItemNumber());
        this.setAmountWithoutTax(categoryInvoiceAgregate.getAmountWithoutTax());
        this.setAmountWithTax(categoryInvoiceAgregate.getAmountWithTax());
        this.setAmountTax(categoryInvoiceAgregate.getAmountTax());
        this.setBillingAccount(categoryInvoiceAgregate.getBillingAccount());
        this.setBillingRun(categoryInvoiceAgregate.getBillingRun());
        this.setUserAccount(categoryInvoiceAgregate.getUserAccount());
        this.setDiscountAggregate(false);
    }

    /**
     * Gets the invoice category.
     *
     * @return the invoice category
     */
    public InvoiceCategory getInvoiceCategory() {
        return invoiceCategory;
    }

    /**
     * Sets the invoice category.
     *
     * @param invoiceCategory the new invoice category
     */
    public void setInvoiceCategory(InvoiceCategory invoiceCategory) {
        this.invoiceCategory = invoiceCategory;
    }

    /**
     * Gets the sub category invoice agregates.
     *
     * @return the sub category invoice agregates
     */
    public Set<SubCategoryInvoiceAgregate> getSubCategoryInvoiceAgregates() {
        return subCategoryInvoiceAgregates;
    }

    /**
     * Sets the sub category invoice agregates.
     *
     * @param subCategoryInvoiceAgregates the new sub category invoice agregates
     */
    public void setSubCategoryInvoiceAgregates(Set<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates) {
        this.subCategoryInvoiceAgregates = subCategoryInvoiceAgregates;
    }

    /**
     * Adds the sub category invoice aggregate.
     *
     * @param subCategoryInvoiceAgregate the sub category invoice agregate
     */
    public void addSubCategoryInvoiceAggregate(SubCategoryInvoiceAgregate subCategoryInvoiceAgregate) {
        if (subCategoryInvoiceAgregates == null) {
            subCategoryInvoiceAgregates = new HashSet<SubCategoryInvoiceAgregate>();
        }

        if (subCategoryInvoiceAgregate != null) {
            if (!subCategoryInvoiceAgregates.contains(subCategoryInvoiceAgregate)) {
                subCategoryInvoiceAgregates.add(subCategoryInvoiceAgregate);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.meveo.model.BaseEntity#hashCode()
     */
    @Override
    public int hashCode() {
        if (id != null) {
            return id.intValue();
        }
        if (invoiceCategory != null) {
            return invoiceCategory.hashCode();
        }

        return 961;
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
        } else if (!(obj instanceof CategoryInvoiceAgregate)) {
            return false;
        }

        CategoryInvoiceAgregate other = (CategoryInvoiceAgregate) obj;

        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }
        if (invoiceCategory == null) {
            if (other.getInvoiceCategory() != null)
                return false;
        } else if (!invoiceCategory.equals(other.getInvoiceCategory()))
            return false;
        return true;
    }

}
