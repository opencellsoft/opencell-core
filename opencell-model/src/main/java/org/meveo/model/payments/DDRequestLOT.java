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
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;

@Entity
@Table(name = "ar_ddrequest_lot")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_ddrequest_lot_seq"), })
public class DDRequestLOT extends AuditableEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "file_name", columnDefinition = "text")
    private String fileName;

    @Column(name = "return_file_name", length = 255)
    @Size(max = 255)
    private String returnFileName;

    @Column(name = "send_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sendDate;

    @Column(name = "invoice_number")
    private Integer nbItemsOk;

    @Type(type = "numeric_boolean")
    @Column(name = "is_payment_created")
    private boolean paymentCreated;

    @Column(name = "invoice_amount", precision = 23, scale = 12)
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "ddRequestLOT", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DDRequestItem> ddrequestItems = new ArrayList<DDRequestItem>();

    @Column(name = "return_status_code", length = 255)
    @Size(max = 255)
    private String returnStatusCode;

    @Column(name = "rejected_cause", length = 255)
    @Size(max = 255)
    private String rejectedCause;

    @Column(name = "rejected_invoices")
    private Integer nbItemsKo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ddrequest_builder_id")
    private DDRequestBuilder ddRequestBuilder;
    
    @Column(name = "payment_or_refund")
    @Enumerated(EnumType.STRING)
    private PaymentOrRefundEnum paymentOrRefundEnum;


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public Integer getNbItemsOk() {
        return nbItemsOk;
    }

    public void setNbItemsOk(Integer invoicesNumber) {
        this.nbItemsOk = invoicesNumber;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal invoicesAmount) {
        this.totalAmount = invoicesAmount;
    }

    public boolean isPaymentCreated() {
        return paymentCreated;
    }

    public void setPaymentCreated(boolean paymentCreated) {
        this.paymentCreated = paymentCreated;
    }

    public void setDdrequestItems(List<DDRequestItem> ddrequestItems) {
        this.ddrequestItems = ddrequestItems;
    }

    public List<DDRequestItem> getDdrequestItems() {
        return ddrequestItems;
    }

    public String getReturnStatusCode() {
        return returnStatusCode;
    }

    public void setReturnStatusCode(String returnStatusCode) {
        this.returnStatusCode = returnStatusCode;
    }

    public String getReturnFileName() {
        return returnFileName;
    }

    public void setReturnFileName(String returnFileName) {
        this.returnFileName = returnFileName;
    }

    public String getRejectedCause() {
        return rejectedCause;
    }

    public void setRejectedCause(String rejectedCause) {
        this.rejectedCause = rejectedCause;
    }

    public Integer getNbItemsKo() {
        return nbItemsKo;
    }

    public void setNbItemsKo(Integer rejectedInvoices) {
        this.nbItemsKo = rejectedInvoices;
    }

    /**
     * @return the ddRequestBuilder
     */
    public DDRequestBuilder getDdRequestBuilder() {
        return ddRequestBuilder;
    }

    /**
     * @param ddRequestBuilder the ddRequestBuilder to set
     */
    public void setDdRequestBuilder(DDRequestBuilder ddRequestBuilder) {
        this.ddRequestBuilder = ddRequestBuilder;
    }

	public PaymentOrRefundEnum getPaymentOrRefundEnum() {
		return paymentOrRefundEnum;
	}

	public void setPaymentOrRefundEnum(PaymentOrRefundEnum paymentOrRefundEnum) {
		this.paymentOrRefundEnum = paymentOrRefundEnum;
	}

  
    
 
}
