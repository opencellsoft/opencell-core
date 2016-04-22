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
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.meveo.model.AuditableEntity;

@Entity
@Table(name = "AR_DDREQUEST_ITEM")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "AR_DDREQUEST_ITEM_SEQ")
public class DDRequestItem extends AuditableEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "AMOUNT")
	private BigDecimal amount;


	@Column(name = "PAYMENT_INFO", length = 255)
    @Size(max = 255)
	private String paymentInfo;// IBAN for direct debit

	@Column(name = "PAYMENT_INFO1", length = 255)
    @Size(max = 255)
	private String paymentInfo1;// bank code

	@Column(name = "PAYMENT_INFO2", length = 255)
    @Size(max = 255)
	private String paymentInfo2;// code guichet

	@Column(name = "PAYMENT_INFO3", length = 255)
    @Size(max = 255)
	private String paymentInfo3;// Num compte

	@Column(name = "PAYMENT_INFO4", length = 255)
    @Size(max = 255)
	private String paymentInfo4;// RIB

	@Column(name = "PAYMENT_INFO5", length = 255)
    @Size(max = 255)
	private String paymentInfo5;// bankName
	
	@Column(name = "PAYMENT_INFO6", length = 255)
    @Size(max = 255)
	private String paymentInfo6;// bic

	@Column(name = "DUE_DATE")
	@Temporal(TemporalType.DATE)
	private Date dueDate;

	@Column(name = "BILLING_ACCOUNT_NAME", length = 255)
    @Size(max = 255)
	private String billingAccountName;

	@Column(name = "REFERENCE", length = 255)
    @Size(max = 255)
	private String reference;

	@ManyToOne(optional = true, cascade = CascadeType.ALL)
	@JoinColumn(name = "DDREQUEST_LOT_ID")
	private DDRequestLOT ddRequestLOT;

	@ManyToOne(optional = true, cascade = CascadeType.ALL)
	@JoinColumn(name = "ACCOUNT_OPERATION_ID")
	private RecordedInvoice recordedInvoice;
	
	@Column(name = "ERROR_MSG", length = 1000)
    @Size(max = 1000)
	private String errorMsg;
	
	@OneToOne(optional = true)
	@JoinColumn(name = "PAYMENT_ID")
	private AutomatedPayment automatedPayment;

	public DDRequestItem() {

	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getPaymentInfo() {
		return paymentInfo;
	}

	public void setPaymentInfo(String paymentInfo) {
		this.paymentInfo = paymentInfo;
	}

	public String getPaymentInfo1() {
		return paymentInfo1;
	}

	public void setPaymentInfo1(String paymentInfo1) {
		this.paymentInfo1 = paymentInfo1;
	}

	public String getPaymentInfo2() {
		return paymentInfo2;
	}

	public void setPaymentInfo2(String paymentInfo2) {
		this.paymentInfo2 = paymentInfo2;
	}

	public String getPaymentInfo3() {
		return paymentInfo3;
	}

	public void setPaymentInfo3(String paymentInfo3) {
		this.paymentInfo3 = paymentInfo3;
	}

	public String getPaymentInfo4() {
		return paymentInfo4;
	}

	public void setPaymentInfo4(String paymentInfo4) {
		this.paymentInfo4 = paymentInfo4;
	}

	public String getPaymentInfo5() {
		return paymentInfo5;
	}

	public void setPaymentInfo5(String paymentInfo5) {
		this.paymentInfo5 = paymentInfo5;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String getBillingAccountName() {
		return billingAccountName;
	}

	public void setBillingAccountName(String billingAccountName) {
		this.billingAccountName = billingAccountName;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public DDRequestLOT getDdRequestLOT() {
		return ddRequestLOT;
	}

	public void setDdRequestLOT(DDRequestLOT ddRequestLOT) {
		this.ddRequestLOT = ddRequestLOT;
	}



	

	/**
	 * @return the recordedInvoice
	 */
	public RecordedInvoice getRecordedInvoice() {
		return recordedInvoice;
	}

	/**
	 * @param recordedInvoice the recordedInvoice to set
	 */
	public void setRecordedInvoice(RecordedInvoice recordedInvoice) {
		this.recordedInvoice = recordedInvoice;
	}

	public String getPaymentInfo6() {
		return paymentInfo6;
	}

	public void setPaymentInfo6(String paymentInfo6) {
		this.paymentInfo6 = paymentInfo6;
	}

	public AutomatedPayment getAutomatedPayment() {
		return automatedPayment;
	}

	public void setAutomatedPayment(AutomatedPayment automatedPayment) {
		this.automatedPayment = automatedPayment;
	}

	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	/**
	 * @param errorMsg the errorMsg to set
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	
	
	@Transient
	public boolean hasError(){
		 return !(errorMsg == null || errorMsg.trim().length() == 0);
	}
	

}
