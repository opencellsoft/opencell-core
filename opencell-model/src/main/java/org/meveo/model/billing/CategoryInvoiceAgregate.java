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

@Entity
@DiscriminatorValue("R")
public class CategoryInvoiceAgregate extends InvoiceAgregate {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoicecategory")
    private InvoiceCategory invoiceCategory;

    @OneToMany(mappedBy = "categoryInvoiceAgregate", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates = new HashSet<SubCategoryInvoiceAgregate>();

    public CategoryInvoiceAgregate() {

    }

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

    public InvoiceCategory getInvoiceCategory() {
        return invoiceCategory;
    }

    public void setInvoiceCategory(InvoiceCategory invoiceCategory) {
        this.invoiceCategory = invoiceCategory;
    }

    public Set<SubCategoryInvoiceAgregate> getSubCategoryInvoiceAgregates() {
        return subCategoryInvoiceAgregates;
    }

    public void setSubCategoryInvoiceAgregates(Set<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates) {
        this.subCategoryInvoiceAgregates = subCategoryInvoiceAgregates;
    }

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
