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

package org.meveo.api.dto.invoice;

import static java.lang.Boolean.FALSE;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;

/**
 * The Class GetPdfInvoiceRequestDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetPdfInvoiceRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetPdfInvoiceRequestDto extends BaseEntityDto {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "id")
    private Long invoiceId;
    
    /** The invoice number. */
    private String invoiceNumber;
    
    /** The invoice type. */
    private String invoiceType;
    
    /** The generate pdf. */
    private Boolean generatePdf = FALSE;

    /**
     * Instantiates a new gets the pdf invoice request dto.
     */
    public GetPdfInvoiceRequestDto() {
    }

    /**
     * Gets the invoice number.
     *
     * @return the invoice number
     */
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * Sets the invoice number.
     *
     * @param invoiceNumber the new invoice number
     */
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    /**
     * Gets the invoice type.
     *
     * @return the invoice type
     */
    public String getInvoiceType() {
        return invoiceType;
    }

    /**
     * Sets the invoice type.
     *
     * @param invoiceType the new invoice type
     */
    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    /**
     * Gets the generate pdf.
     *
     * @return the generatePdf
     */
    public Boolean getGeneratePdf() {
        return generatePdf;
    }

    /**
     * Sets the generate pdf.
     *
     * @param generatePdf the generatePdf to set
     */
    public void setGeneratePdf(Boolean generatePdf) {
        this.generatePdf = generatePdf;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    @Override
    public String toString() {
        return "GetPdfInvoiceRequestDto [invoiceNumber=" + invoiceNumber + ", invoiceType=" + invoiceType + ", generatePdf=" + generatePdf + "]";
    }
}