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

package org.meveo.api.dto.payment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.model.payments.RecordedInvoice;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The Class RecordedInvoiceDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class RecordedInvoiceDto extends AccountOperationDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6965598553420278018L;

    /** The production date. */
    @Schema(description = "The production date")
    private Date productionDate;

    /** The invoice date. */
    @Schema(description = "The invoice date")
    private Date invoiceDate;

    /** The net to pay. */
    @Schema(description = "The net to pay")
    private BigDecimal netToPay;

    /**
     * Instantiates a new recorded invoice dto.
     */
    public RecordedInvoiceDto() {
        super.setType("I");
    }

    /**
     * Instantiates a new recorded invoice dto.
     *
     * @param recordedInvoice the RecordedInvoice entity
     */
    public RecordedInvoiceDto(RecordedInvoice recordedInvoice) {
        super(recordedInvoice);
        setInvoiceDate(recordedInvoice.getInvoiceDate());
    }

    /**
     * Gets the production date.
     *
     * @return the production date
     */
    public Date getProductionDate() {
        return productionDate;
    }

    /**
     * Sets the production date.
     *
     * @param productionDate the new production date
     */
    public void setProductionDate(Date productionDate) {
        this.productionDate = productionDate;
    }

    /**
     * Gets the invoice date.
     *
     * @return the invoice date
     */
    public Date getInvoiceDate() {
        return invoiceDate;
    }

    /**
     * Sets the invoice date.
     *
     * @param invoiceDate the new invoice date
     */
    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    /**
     * Gets the net to pay.
     *
     * @return the net to pay
     */
    public BigDecimal getNetToPay() {
        return netToPay;
    }

    /**
     * Sets the net to pay.
     *
     * @param netToPay the new net to pay
     */
    public void setNetToPay(BigDecimal netToPay) {
        this.netToPay = netToPay;
    }

}
