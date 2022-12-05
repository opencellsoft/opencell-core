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

package org.meveo.api.dto.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.InvoiceSubCategoriesDto;
import org.meveo.api.dto.InvoiceSubCategoryDto;

/**
 * The Class InvoiceSubCategoryResponseDto.
 *
 * @author akadid abdelmounaim
 */
@XmlRootElement(name = "InvoiceSubCategoryResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceSubCategoryResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 28350706655272858L;

    /** The invoiceSubCategories. */
    private InvoiceSubCategoriesDto invoiceSubCategories = new InvoiceSubCategoriesDto();
    
    /**
     * Constructor
     */
    public InvoiceSubCategoryResponseDto() {
        super();
    }
    
    /**
     * Constructor
     */
    public InvoiceSubCategoryResponseDto(GenericSearchResponse<InvoiceSubCategoryDto> searchResponse) {
        super(searchResponse.getPaging());
        this.invoiceSubCategories.setInvoiceSubCategory(searchResponse.getSearchResults());
    }

    /**
     * Gets the invoiceSubCategories.
     *
     * @return the invoiceSubCategories
     */
    public InvoiceSubCategoriesDto getInvoiceSubCategories() {
        return invoiceSubCategories;
    }

    /**
     * Sets the invoiceSubCategories.
     *
     * @param invoiceSubCategories the new invoiceSubCategories
     */
    public void setInvoiceSubCategories(InvoiceSubCategoriesDto invoiceSubCategories) {
        this.invoiceSubCategories = invoiceSubCategories;
    }

    @Override
    public String toString() {
        return "InvoiceSubCategoryResponse [invoiceSubCategories=" + invoiceSubCategories + ", toString()=" + super.toString() + "]";
    }
}