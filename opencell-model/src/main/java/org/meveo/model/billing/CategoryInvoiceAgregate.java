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
 * Invoice category aggregate for the invoice
 */
@Entity
@DiscriminatorValue("R")
public class CategoryInvoiceAgregate extends InvoiceAgregate {

    private static final long serialVersionUID = 1L;

    /**
     * The invoice category
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoicecategory")
    private InvoiceCategory invoiceCategory;

    /**
     * User account
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;

    /**
     * The sub category invoice aggregate
     */
    @OneToMany(mappedBy = "categoryInvoiceAgregate", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates = new HashSet<>();

    /**
     * Instantiates a new category invoice aggregate.
     */
    public CategoryInvoiceAgregate() {

    }

    /**
     * Instantiates a new category invoice aggregate
     * 
     * @param invoiceCategory Invoice category
     * @param billingAccount Billing account
     * @param userAccount User account
     * @param invoice Invoice
     */
    public CategoryInvoiceAgregate(InvoiceCategory invoiceCategory, BillingAccount billingAccount, UserAccount userAccount, Invoice invoice) {
        super();
        this.invoiceCategory = invoiceCategory;
        this.billingAccount = billingAccount;
        this.userAccount = userAccount;
        this.invoice = invoice;
        if (invoice != null) {
            this.billingRun = invoice.getBillingRun();
        }
    }

    /**
     * Copies invoice category aggregate
     *
     * @param categoryInvoiceAgregate The category invoice aggregate to copy
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
    }

    /**
     * @return The invoice category
     */
    public InvoiceCategory getInvoiceCategory() {
        return invoiceCategory;
    }

    /**
     * @param invoiceCategory Invoice category
     */
    public void setInvoiceCategory(InvoiceCategory invoiceCategory) {
        this.invoiceCategory = invoiceCategory;
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
     * @return A parent sub category invoice aggregate
     */
    public Set<SubCategoryInvoiceAgregate> getSubCategoryInvoiceAgregates() {
        return subCategoryInvoiceAgregates;
    }

    /**
     * @param subCategoryInvoiceAgregates A parent sub category invoice aggregate
     */
    public void setSubCategoryInvoiceAgregates(Set<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates) {
        this.subCategoryInvoiceAgregates = subCategoryInvoiceAgregates;
    }

    /**
     * Adds the sub category invoice aggregate. Increments item number and amountWithoutTax/amountWithTax/amountTax fields
     *
     * @param subCategoryInvoiceAgregate the sub category invoice aggregate
     */
    public void addSubCategoryInvoiceAggregate(SubCategoryInvoiceAgregate subCategoryInvoiceAgregate) {
        if (subCategoryInvoiceAgregates == null) {
            subCategoryInvoiceAgregates = new HashSet<SubCategoryInvoiceAgregate>();
        }

        if (subCategoryInvoiceAgregate != null) {
            if (!subCategoryInvoiceAgregates.contains(subCategoryInvoiceAgregate)) {
                subCategoryInvoiceAgregates.add(subCategoryInvoiceAgregate);
                subCategoryInvoiceAgregate.setCategoryInvoiceAgregate(this);
                if (itemNumber == null) {
                    itemNumber = 0;
                }
                itemNumber = itemNumber + subCategoryInvoiceAgregate.getItemNumber();

                amountWithoutTax = amountWithoutTax.add(subCategoryInvoiceAgregate.getAmountWithoutTax());
                amountWithTax = amountWithTax.add(subCategoryInvoiceAgregate.getAmountWithTax());
                amountTax = amountTax.add(subCategoryInvoiceAgregate.getAmountTax());
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
            return false;
        }

        return this.getInvoiceCategory().getId().equals(other.getInvoiceCategory().getId()) && ((this.getUserAccount() == null && other.getUserAccount() == null)
                || (this.getUserAccount() != null && other.getUserAccount() != null && this.getUserAccount().getId().equals(other.getUserAccount().getId())));
    }
}