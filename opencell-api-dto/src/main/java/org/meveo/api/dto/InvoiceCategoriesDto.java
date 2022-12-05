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

package org.meveo.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The Class InvoiceCategoriesDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "InvoiceCategories")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceCategoriesDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7706480655782126035L;

    /** The invoice category. */
    private List<InvoiceCategoryDto> invoiceCategory;

    /**
     * Gets the invoice category.
     *
     * @return the invoice category
     */
    public List<InvoiceCategoryDto> getInvoiceCategory() {
        if (invoiceCategory == null)
            invoiceCategory = new ArrayList<InvoiceCategoryDto>();
        return invoiceCategory;
    }

    /**
     * Sets the invoice category.
     *
     * @param invoiceCategory the new invoice category
     */
    public void setInvoiceCategory(List<InvoiceCategoryDto> invoiceCategory) {
        this.invoiceCategory = invoiceCategory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "InvoiceCategoriesDto [invoiceCategory=" + invoiceCategory + "]";
    }

}
