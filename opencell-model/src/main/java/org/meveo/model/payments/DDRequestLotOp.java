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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.filter.Filter;
import org.meveo.model.scripts.ScriptInstance;

/**
 * The Class DDRequestLotOp.
 *
 * @author anasseh
 * @author Said Ramli
 * @lastModifiedVersion 5.3
 */
@Entity
@Table(name = "ar_ddrequest_lot_op")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_ddrequest_lot_op_seq"), })
public class DDRequestLotOp extends AuditableEntity {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The from due date. */
    @Column(name = "from_due_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fromDueDate;

    /** The to due date. */
    @Column(name = "to_due_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date toDueDate;

    /** The ddrequest op. */
    @Column(name = "ddrequest_op")
    @Enumerated(EnumType.STRING)
    private DDRequestOpEnum ddrequestOp;

    /** The status. */
    @Column(name = "ddrequest_op_status")
    @Enumerated(EnumType.STRING)
    private DDRequestOpStatusEnum status;

    /** The ddrequest LOT. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ddrequest_lot_id")
    private DDRequestLOT ddrequestLOT;

    /** The error cause. */
    @Column(name = "error_cause", length = 255)
    @Size(max = 255)
    private String errorCause;

    /** The dd request builder. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ddrequest_builder_id")
    private DDRequestBuilder ddRequestBuilder;

    /** The filter. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filter_id")
    private Filter filter;

    /** The script instance. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "script_instance_id")
    private ScriptInstance scriptInstance;
    
    /** The recurrent. */
    @Type(type = "numeric_boolean")
    @Column(name = "recurrent")
    private Boolean recurrent;
    
    /** The Payment Or Refund Enum. */
    @Column(name = "payment_or_refund")
    @Enumerated(EnumType.STRING)
    PaymentOrRefundEnum paymentOrRefundEnum;
    
    /** The Seller. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;
    
    /** Flag to activate generation of payment lines (by default true). */
    @Type(type = "numeric_boolean")
    @Column(name = "generate_payment_lines")
    private Boolean generatePaymentLines = Boolean.TRUE;

    /**
     * Gets the from due date.
     *
     * @return the fromDueDate
     */
    public Date getFromDueDate() {
        return fromDueDate;
    }

    /**
     * Sets the from due date.
     *
     * @param fromDueDate the fromDueDate to set
     */
    public void setFromDueDate(Date fromDueDate) {
        this.fromDueDate = fromDueDate;
    }

    /**
     * Gets the to due date.
     *
     * @return the toDueDate
     */
    public Date getToDueDate() {
        return toDueDate;
    }

    /**
     * Sets the to due date.
     *
     * @param toDueDate the toDueDate to set
     */
    public void setToDueDate(Date toDueDate) {
        this.toDueDate = toDueDate;
    }

    /**
     * Gets the ddrequest op.
     *
     * @return the ddrequestOp
     */
    public DDRequestOpEnum getDdrequestOp() {
        return ddrequestOp;
    }

    /**
     * Sets the ddrequest op.
     *
     * @param ddrequestOp the ddrequestOp to set
     */
    public void setDdrequestOp(DDRequestOpEnum ddrequestOp) {
        this.ddrequestOp = ddrequestOp;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public DDRequestOpStatusEnum getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the status to set
     */
    public void setStatus(DDRequestOpStatusEnum status) {
        this.status = status;
    }

    /**
     * Gets the ddrequest LOT.
     *
     * @return the ddrequestLOT
     */
    public DDRequestLOT getDdrequestLOT() {
        return ddrequestLOT;
    }

    /**
     * Sets the ddrequest LOT.
     *
     * @param ddrequestLOT the ddrequestLOT to set
     */
    public void setDdrequestLOT(DDRequestLOT ddrequestLOT) {
        this.ddrequestLOT = ddrequestLOT;
    }

    /**
     * Sets the error cause.
     *
     * @param errorCause the new error cause
     */
    public void setErrorCause(String errorCause) {
        this.errorCause = errorCause;
    }

    /**
     * Gets the error cause.
     *
     * @return the error cause
     */
    public String getErrorCause() {
        return errorCause;
    }

    /**
     * Gets the dd request builder.
     *
     * @return the ddRequestBuilder
     */
    public DDRequestBuilder getDdRequestBuilder() {
        return ddRequestBuilder;
    }

    /**
     * Sets the dd request builder.
     *
     * @param ddRequestBuilder the ddRequestBuilder to set
     */
    public void setDdRequestBuilder(DDRequestBuilder ddRequestBuilder) {
        this.ddRequestBuilder = ddRequestBuilder;
    }

    /**
     * Gets the filter.
     *
     * @return the filter
     */
    public Filter getFilter() {
        return filter;
    }

    /**
     * Sets the filter.
     *
     * @param filter the filter to set
     */
    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    /**
     * Gets the script instance.
     *
     * @return the scriptInstance
     */
    public ScriptInstance getScriptInstance() {
        return scriptInstance;
    }


    /**
     * Sets the script instance.
     *
     * @param scriptInstance the scriptInstance to set
     */
    public void setScriptInstance(ScriptInstance scriptInstance) {
        this.scriptInstance = scriptInstance;
}


    /**
     * Gets the recurrent.
     *
     * @return the recurrent
     */
    public Boolean getRecurrent() {
        return recurrent;
    }

    /**
     * Sets the recurrent.
     *
     * @param recurrent the recurrent to set
     */
    public void setRecurrent(Boolean recurrent) {
        this.recurrent = recurrent;
    }
   
    /**
     * @return the seller
     */
    public Seller getSeller() {
        return seller;
    }

    /**
     * @param seller the seller to set
     */
    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    /**
     * @return the paymentOrRefundEnum
     */
    public PaymentOrRefundEnum getPaymentOrRefundEnum() {
        return paymentOrRefundEnum;
    }

    /**
     * @param paymentOrRefundEnum the paymentOrRefundEnum to set
     */
    public void setPaymentOrRefundEnum(PaymentOrRefundEnum paymentOrRefundEnum) {
        this.paymentOrRefundEnum = paymentOrRefundEnum;
    }

	/**
	 * @return the generatePaymentLines
	 */
	public Boolean isGeneratePaymentLines() {
		return generatePaymentLines;
	}

	/**
	 * @param generatePaymentLines the generatePaymentLines to set
	 */
	public void setGeneratePaymentLines(Boolean generatePaymentLines) {
		this.generatePaymentLines = generatePaymentLines;
	}
   
}
