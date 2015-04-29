/*
* (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
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
    @JoinColumn(name = "invoiceCategory")
    private InvoiceCategory invoiceCategory;

    @OneToMany(mappedBy = "categoryInvoiceAgregate", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates = new HashSet<SubCategoryInvoiceAgregate>();

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

}
