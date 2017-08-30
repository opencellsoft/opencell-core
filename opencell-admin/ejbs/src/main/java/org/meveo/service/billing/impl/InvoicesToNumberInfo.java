package org.meveo.service.billing.impl;

import java.util.Date;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Sequence;

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
    private Sequence numberingSequence;

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

    public void setNumberingSequence(Sequence numberingSequence) {
        this.numberingSequence = numberingSequence;
        this.lastInvoiceNumber = numberingSequence.getPreviousInvoiceNb();
    }

    public Sequence getNumberingSequence() {
        return numberingSequence;
    }

    /**
     * Increment by 1 an return a new invoice number formated to a sequence size
     * 
     * @return New invoice number formated with leading zeros
     * @throws BusinessException
     */
    public synchronized String nextInvoiceNumber() throws BusinessException {
        lastInvoiceNumber++;
        if (lastInvoiceNumber.longValue() > numberingSequence.getCurrentInvoiceNb()) {
            throw new BusinessException("Can not assign an invoice number beyond the number " + numberingSequence.getCurrentInvoiceNb() + " that was reseved");
        }
        return StringUtils.getLongAsNChar(lastInvoiceNumber, numberingSequence.getSequenceSize());
    }
}