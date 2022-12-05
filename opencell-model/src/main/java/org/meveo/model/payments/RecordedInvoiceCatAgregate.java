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
package org.meveo.model.payments;

import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@DiscriminatorValue(value = "IC")
public class RecordedInvoiceCatAgregate extends RecordedInvoice {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_invoice_id")
    private RecordedInvoice recordedInvoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_inv_agregate_id")
    private CategoryInvoiceAgregate categoryInvoiceAgregate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_cat_inv_agregate_id")
    private SubCategoryInvoiceAgregate subCategoryInvoiceAgregate;

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

    /**
     * @return the subCategoryInvoiceAgregate
     */
    public SubCategoryInvoiceAgregate getSubCategoryInvoiceAgregate() {
        return subCategoryInvoiceAgregate;
    }

    /**
     * @param subCategoryInvoiceAgregate the subCategoryInvoiceAgregate to set
     */
    public void setSubCategoryInvoiceAgregate(SubCategoryInvoiceAgregate subCategoryInvoiceAgregate) {
        this.subCategoryInvoiceAgregate = subCategoryInvoiceAgregate;
    }

}