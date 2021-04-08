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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.billing.Invoice;
import org.meveo.model.dunning.DunningDocument;

@Entity
@DiscriminatorValue(value = "I")
public class RecordedInvoice extends AccountOperation {

    private static final long serialVersionUID = 1L;

    @Column(name = "production_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date productionDate;

    @Column(name = "invoice_date")
    @Temporal(TemporalType.DATE)
    private Date invoiceDate;

    @Column(name = "net_to_pay", precision = 23, scale = 12)
    private BigDecimal netToPay;

    @OneToMany(mappedBy = "recordedInvoice", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RecordedInvoiceCatAgregate> recordedInvoiceCatAgregates = new ArrayList<RecordedInvoiceCatAgregate>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pay_schdl_inst_item_id")
    private PaymentScheduleInstanceItem paymentScheduleInstanceItem;

    /**
     * if an invoice becomes unpaid then, it's associated with a dunning doc
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dunning_document_id")
    private DunningDocument dunningDocument;
    
    /**
     * if an invoice becomes unpaid then, it's associated with a dunning doc
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    public Date getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(Date productionDate) {
        this.productionDate = productionDate;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public BigDecimal getNetToPay() {
        return netToPay;
    }

    public void setNetToPay(BigDecimal netToPay) {
        this.netToPay = netToPay;
    }

    /**
     * @return the recordedInvoiceCatAgregates
     */
    public List<RecordedInvoiceCatAgregate> getRecordedInvoiceCatAgregates() {
        return recordedInvoiceCatAgregates;
    }

    /**
     * @param recordedInvoiceCatAgregates the recordedInvoiceCatAgregates to set
     */
    public void setRecordedInvoiceCatAgregates(List<RecordedInvoiceCatAgregate> recordedInvoiceCatAgregates) {
        this.recordedInvoiceCatAgregates = recordedInvoiceCatAgregates;
    }

    /**
     * @return the paymentScheduleInstanceItem
     */
    public PaymentScheduleInstanceItem getPaymentScheduleInstanceItem() {
        return paymentScheduleInstanceItem;
    }

    /**
     * @param paymentScheduleInstanceItem the paymentScheduleInstanceItem to set
     */
    public void setPaymentScheduleInstanceItem(PaymentScheduleInstanceItem paymentScheduleInstanceItem) {
        this.paymentScheduleInstanceItem = paymentScheduleInstanceItem;
    }

    /**
     * @return dunning doc
     */
    public DunningDocument getDunningDocument() {
        return dunningDocument;
    }

    /**
     *
     * @param dunningDocument dunning Document
     */
    public void setDunningDocument(DunningDocument dunningDocument) {
        this.dunningDocument = dunningDocument;
    }

    
    /**
     * @return invoiceTypeCode
     */
	public Invoice getInvoice() {
		return invoice;
	}
	
	/**
    *
    * @param invoiceTypeCode
    */
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}


}