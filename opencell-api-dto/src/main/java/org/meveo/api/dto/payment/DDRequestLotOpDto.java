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

import java.util.Date;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.AuditableEntityDto;
import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.model.payments.DDRequestOpEnum;
import org.meveo.model.payments.DDRequestOpStatusEnum;
import org.meveo.model.payments.PaymentOrRefundEnum;
import org.meveo.model.payments.PaymentStatusEnum;

/**
 * The Class DDRequestLotOpDto.
 *
 * @author Tyshan Shi(tyshan@manaty.net)
 * @author anasseh
 * @author Said Ramli
 * @since Jul 11, 2016 7:15:09 PM
 * @lastModifiedVersion 5.3
 */
@XmlRootElement(name = "DDRequestLotOp")
@XmlAccessorType(XmlAccessType.FIELD)
public class DDRequestLotOpDto extends AuditableEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6185045412352889135L;

    /** The from due date. */
    @XmlElement(required = true)
    private Date fromDueDate;
    
    /** The to due date. */
    @XmlElement(required = true)
    private Date toDueDate;
    
    /** The filter code. */
    private String filterCode;
    
    /** The ddrequest op. */
    private DDRequestOpEnum ddrequestOp;
    
    /** The status. */
    private DDRequestOpStatusEnum status;
    
    /** The error cause. */
    private String errorCause;
    
    /** The dd request builder code. */
    private String ddRequestBuilderCode;
    
    /** The due date rage script code : to get the custom script computing the AOs due date range. */
    private String dueDateRageScriptCode;
    
    /** The recurrent flag : to decide if a new dd request lot operation will be created at end , or not. */
    private Boolean recurrent;
    
    private PaymentOrRefundEnum paymentOrRefundEnum;
    
    /** The seller code. */
    private String sellerCode;
    
    /** Flag to activate generation of payment lines (by default true). */
    private Boolean generatePaymentLines = Boolean.TRUE;

    /** Default payment status. */
    private PaymentStatusEnum paymentStatus;
    
    /**
     * Instantiates a new DD request lot op dto.
     */
    public DDRequestLotOpDto() {

    }

    /**
     * Instantiates a new DD request lot op dto.
     *
     * @param ddrequestLotOp the DDRequestLotOp entity
     */
    public DDRequestLotOpDto(DDRequestLotOp ddrequestLotOp) {
        super(ddrequestLotOp);
        this.fromDueDate = ddrequestLotOp.getFromDueDate();
        this.toDueDate = ddrequestLotOp.getToDueDate();
        this.ddrequestOp = ddrequestLotOp.getDdrequestOp();
        this.status = ddrequestLotOp.getStatus();
        this.errorCause = ddrequestLotOp.getErrorCause();
        this.ddRequestBuilderCode = ddrequestLotOp.getDdRequestBuilder() != null ? ddrequestLotOp.getDdRequestBuilder().getCode() : null;
        this.filterCode = ddrequestLotOp.getFilter() != null ? ddrequestLotOp.getFilter().getCode() : null;
        this.paymentOrRefundEnum = ddrequestLotOp.getPaymentOrRefundEnum();
        this.sellerCode = ddrequestLotOp.getSeller() != null ? ddrequestLotOp.getSeller().getCode() : null;
        this.generatePaymentLines = ddrequestLotOp.isGeneratePaymentLines();
        this.paymentStatus = ddrequestLotOp.getPaymentStatus();
    }

    /**
     * Gets the from due date.
     *
     * @return the from due date
     */
    public Date getFromDueDate() {
        return fromDueDate;
    }

    /**
     * Sets the from due date.
     *
     * @param fromDueDate the new from due date
     */
    public void setFromDueDate(Date fromDueDate) {
        this.fromDueDate = fromDueDate;
    }

    /**
     * Gets the to due date.
     *
     * @return the to due date
     */
    public Date getToDueDate() {
        return toDueDate;
    }

    /**
     * Sets the to due date.
     *
     * @param toDueDate the new to due date
     */
    public void setToDueDate(Date toDueDate) {
        this.toDueDate = toDueDate;
    }

    /**
     * Gets the ddrequest op.
     *
     * @return the ddrequest op
     */
    public DDRequestOpEnum getDdrequestOp() {
        return ddrequestOp;
    }

    /**
     * Sets the ddrequest op.
     *
     * @param ddrequestOp the new ddrequest op
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
     * @param status the new status
     */
    public void setStatus(DDRequestOpStatusEnum status) {
        this.status = status;
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
     * Sets the error cause.
     *
     * @param errorCause the new error cause
     */
    public void setErrorCause(String errorCause) {
        this.errorCause = errorCause;
    }

    /**
     * Gets the filter code.
     *
     * @return the filterCode
     */
    public String getFilterCode() {
        return filterCode;
    }

    /**
     * Sets the filter code.
     *
     * @param filterCode the filterCode to set
     */
    public void setFilterCode(String filterCode) {
        this.filterCode = filterCode;
    }

    /**
     * Gets the dd request builder code.
     *
     * @return the ddRequestBuilderCode
     */
    public String getDdRequestBuilderCode() {
        return ddRequestBuilderCode;
    }

    /**
     * Sets the dd request builder code.
     *
     * @param ddRequestBuilderCode the ddRequestBuilderCode to set
     */
    public void setDdRequestBuilderCode(String ddRequestBuilderCode) {
        this.ddRequestBuilderCode = ddRequestBuilderCode;
    }

    /**
     * Gets the due date rage script code.
     *
     * @return the dueDateRageScriptCode
     */
    public String getDueDateRageScriptCode() {
        return dueDateRageScriptCode;
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
     * Sets the due date rage script code.
     *
     * @param dueDateRageScriptCode the dueDateRageScriptCode to set
     */
    public void setDueDateRageScriptCode(String dueDateRageScriptCode) {
        this.dueDateRageScriptCode = dueDateRageScriptCode;
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
     * Gets the payment gateway code.
     *
     * @return the sellerCode
     */
    public String getSellerCode() {
        return sellerCode;
    }

    /**
     * Sets the payment gateway code.
     *
     * @param sellerCode the sellerCode to set
     */
    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
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

    /**
     * @return the paymentStatus
     */
    public PaymentStatusEnum getPaymentStatus() {
        return paymentStatus;
    }

    /**
     * @param paymentStatus the paymentStatus to set
     */
    public void setPaymentStatus(PaymentStatusEnum paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

}