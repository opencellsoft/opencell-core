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

package org.meveo.api.dto.billing;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.model.billing.Invoice;

/**
 * Invoice generate request DTO
 */
@XmlRootElement(name = "GenerateInvoiceResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class GenerateInvoiceResultDto extends InvoiceDto {

    private static final long serialVersionUID = 3593774505433626017L;

    /** The temporary invoice number. */
    private String temporaryInvoiceNumber;

    /** The invoice type code. */
    private String invoiceTypeCode;

    /** The amount. */
    private BigDecimal amount;

    /** The account operation id. */
    private Long accountOperationId;

    /**
     * Gets the temporary invoice number.
     *
     * @return the temporary invoice number
     */
    public String getTemporaryInvoiceNumber() {
        return temporaryInvoiceNumber;
    }

    /**
     * Sets the temporary invoice number.
     *
     * @param temporaryInvoiceNumber the new temporary invoice number
     */
    public void setTemporaryInvoiceNumber(String temporaryInvoiceNumber) {
        this.temporaryInvoiceNumber = temporaryInvoiceNumber;
    }

    /**
     * Gets the invoice type code.
     *
     * @return the invoice type code
     */
    public String getInvoiceTypeCode() {
        return invoiceTypeCode;
    }

    /**
     * Sets the invoice type code.
     *
     * @param invoiceTypeCode the new invoice type code
     */
    public void setInvoiceTypeCode(String invoiceTypeCode) {
        this.invoiceTypeCode = invoiceTypeCode;
    }

    /**
     * Gets the amount.
     *
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Sets the amount.
     *
     * @param amount the new amount
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Gets the account operation id.
     *
     * @return the account operation id
     */
    public Long getAccountOperationId() {
        return accountOperationId;
    }

    /**
     * Sets the account operation id.
     *
     * @param accountOperationId the new account operation id
     */
    public void setAccountOperationId(Long accountOperationId) {
        this.accountOperationId = accountOperationId;
    }

    @Override
    public String toString() {
        return "GenerateInvoiceResultDto [invoiceNumber=" + (invoiceNumber != null ? invoiceNumber : temporaryInvoiceNumber) + " ,invoiceId:" + invoiceId + "]";
    }
}