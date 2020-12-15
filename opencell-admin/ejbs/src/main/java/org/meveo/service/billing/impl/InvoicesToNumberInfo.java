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

package org.meveo.service.billing.impl;

import java.util.Date;
import java.util.Objects;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.InvoiceSequence;

/**
 * A summary of invoices for assigning invoice numbers to. All grouped invoices match same invoice type, seller and invoice date
 * 
 * @author Andrius Karpavicius
 *
 */
public class InvoicesToNumberInfo {

    /**
     * Invoice type identifier
     */
    private Long invoiceTypeId;

    /**
     * Seller identifier
     */
    private Long sellerId;

    /**
     * Invoice date
     */
    private Date invoiceDate;

    /**
     * Number of invoices matching invoice type, seller and invoice date
     */
    private Long nrOfInvoices;

    /**
     * A numbering sequence containing rules how to compose an invoice number
     */
    private InvoiceSequence numberingSequence;

    /**
     * A last invoice number assigned.It gets incremented by 1 with every call to
     */
    private Long lastInvoiceNumber;

    public InvoicesToNumberInfo(Long invoiceTypeId, Long sellerId, Date invoiceDate, Long nrOfInvoices) {
        this.invoiceTypeId = invoiceTypeId;
        this.sellerId = sellerId;
        this.invoiceDate = invoiceDate;
        this.nrOfInvoices = nrOfInvoices;
    }

    public Long getInvoiceTypeId() {
        return invoiceTypeId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public Long getNrOfInvoices() {
        return nrOfInvoices;
    }

    public void setNumberingSequence(InvoiceSequence numberingSequence) {
        this.numberingSequence = numberingSequence;
        this.lastInvoiceNumber = numberingSequence.getPreviousInvoiceNb();
    }

    public InvoiceSequence getNumberingSequence() {
        return numberingSequence;
    }

    /**
     * Increment by 1 an return a new invoice number formated to a sequence size
     * 
     * @return New invoice number formated with leading zeros
     * @throws BusinessException business exception.
     */
    public synchronized String nextInvoiceNumber() throws BusinessException {
        lastInvoiceNumber = Objects.requireNonNullElse(lastInvoiceNumber, 0).longValue() + 1;
        if (lastInvoiceNumber > numberingSequence.getCurrentNumber()) {
            throw new BusinessException("Can not assign an invoice number beyond the number " + numberingSequence.getCurrentNumber() + " that was reseved");
        }
        return StringUtils.getLongAsNChar(lastInvoiceNumber, numberingSequence.getSequenceSize());
    }
}