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
package org.meveo.model.payments;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.meveo.model.billing.CategoryInvoiceAgregate;

@Entity
@DiscriminatorValue(value = "IC")
public class RecordedInvoiceCatAgregate extends RecordedInvoice {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_invoice_id")
    private RecordedInvoice recordedInvoice;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_inv_agregate_id")
    private  CategoryInvoiceAgregate categoryInvoiceAgregate;

    /**
     * @return the recordedInvoice
     */
    public RecordedInvoice getRecordedInvoice() {
        return recordedInvoice;
    }

    /**
     * @param recordedInvoice the recordedInvoice to set
     */
    public void setRecordedInvoice(RecordedInvoice recordedInvoice) {
        this.recordedInvoice = recordedInvoice;
    }

    /**
     * @return the categoryInvoiceAgregate
     */
    public CategoryInvoiceAgregate getCategoryInvoiceAgregate() {
        return categoryInvoiceAgregate;
    }

    /**
     * @param categoryInvoiceAgregate the categoryInvoiceAgregate to set
     */
    public void setCategoryInvoiceAgregate(CategoryInvoiceAgregate categoryInvoiceAgregate) {
        this.categoryInvoiceAgregate = categoryInvoiceAgregate;
    }
    
    
    
}