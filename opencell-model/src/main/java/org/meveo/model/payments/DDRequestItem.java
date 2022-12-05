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
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "ar_ddrequest_item")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_ddrequest_item_seq"), })
public class DDRequestItem extends AuditableEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "payment_info", length = 255)
    @Size(max = 255)
    private String paymentInfo;// IBAN for direct debit

    @Column(name = "payment_info1", length = 255)
    @Size(max = 255)
    private String paymentInfo1;// bank code

    @Column(name = "payment_info2", length = 255)
    @Size(max = 255)
    private String paymentInfo2;// code guichet

    @Column(name = "payment_info3", length = 255)
    @Size(max = 255)
    private String paymentInfo3;// Num compte

    @Column(name = "payment_info4", length = 255)
    @Size(max = 255)
    private String paymentInfo4;// RIB

    @Column(name = "payment_info5", length = 255)
    @Size(max = 255)
    private String paymentInfo5;// bankName

    @Column(name = "payment_info6", length = 255)
    @Size(max = 255)
    private String paymentInfo6;// bic

    @Column(name = "due_date")
    @Temporal(TemporalType.DATE)
    private Date dueDate;

    @Column(name = "billing_account_name", length = 255)
    @Size(max = 255)
    private String billingAccountName;

    @Column(name = "reference", length = 255)
    @Size(max = 255)
    private String reference;

    @ManyToOne(optional = true)
    @JoinColumn(name = "ddrequest_lot_id")
    private DDRequestLOT ddRequestLOT;

    @OneToMany(mappedBy = "ddRequestItem", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AccountOperation> accountOperations;

    @Column(name = "error_msg", length = 1000)
    @Size(max = 1000)
    private String errorMsg;

    @OneToOne(optional = true)
    @JoinColumn(name = "payment_id")
    private AutomatedPayment automatedPayment;
    
    @OneToOne(optional = true)
    @JoinColumn(name = "refund_id")
    private AutomatedRefund automatedRefund;
    
    @Column(name = "rejected_file_name", length = 1000)
    @Size(max = 1000)    
    private String rejectedFileName;


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

    /**
     * @return the accountOperations
     */
    public List<AccountOperation> getAccountOperations() {
        return accountOperations;
    }

    /**
     * @param accountOperations the accountOperations to set
     */
    public void setAccountOperations(List<AccountOperation> accountOperations) {
        this.accountOperations = accountOperations;
    }
    
    

    /**
     * @return the refund
     */
    public AutomatedRefund getAutomatedRefund() {
        return automatedRefund;
    }

    /**
     * @param refund the refund to set
     */
    public void setAutomatedRefund(AutomatedRefund refund) {
        this.automatedRefund = refund;
    }

    
    public String getRejectedFileName() {
		return rejectedFileName;
	}

	public void setRejectedFileName(String rejectedFileName) {
		this.rejectedFileName = rejectedFileName;
	}

	@Transient
    public boolean hasError() {
        return !(errorMsg == null || errorMsg.trim().length() == 0);
    }	

}
