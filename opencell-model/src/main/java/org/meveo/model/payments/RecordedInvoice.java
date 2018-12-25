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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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

}