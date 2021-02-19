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

package org.meveo.model.dunning;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IWFEntity;
import org.meveo.model.WorkflowedEntity;
import org.meveo.model.billing.Subscription;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.RecordedInvoice;

/**
 * Dunning document
 */
@Entity
@WorkflowedEntity
@Table(name="dunning_document")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "dunning_document_seq"), })
public class DunningDocument extends BusinessEntity implements IWFEntity {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Customer account
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "customer_account_id")
    private CustomerAccount customerAccount;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "billing_subscription_id")
    private Subscription subscription;

    /**
     * Unpaid invoices associated to this dunning document
     */
    @OneToMany(mappedBy = "dunningDocument", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH, orphanRemoval = true)
    @Where(clause = "transaction_type='I'")
    private List<RecordedInvoice> dueInvoices = new ArrayList<>();

    /**
     * payments done withing the dunning process
     * associated with this dunning doc
     */
    @OneToMany(mappedBy = "dunningDocument", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH, orphanRemoval = true)
    @Where(clause = "transaction_type='P'")
    private List<Payment> payments = new ArrayList<>();

    /**
     * Dunning document status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 25)
    private DunningDocumentStatus status;

    public CustomerAccount getCustomerAccount() {
        return customerAccount;
    }

    public void setCustomerAccount(CustomerAccount customerAccount) {
        this.customerAccount = customerAccount;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public List<RecordedInvoice> getDueInvoices() {
        return dueInvoices;
    }

    public void setDueInvoices(List<RecordedInvoice> dueInvoices) {
        this.dueInvoices = dueInvoices;
    }

    public List<Payment> getPayments() {
        return payments;
    }
    
    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }
    
    @Transient
    public List<Payment> addPayment(Payment payment) {
        if(payments == null) {
            payments = new ArrayList<Payment>();
        }
        payments.add(payment);
        return payments;
    }
    
    @Transient
    public BigDecimal getAmountWithoutTax() {
		return dueInvoices.stream().map(RecordedInvoice::getAmountWithoutTax).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Transient
    public BigDecimal getAmountWithTax() {
    	return dueInvoices.stream().map(RecordedInvoice::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Transient
    public BigDecimal getPaidAmount() {
    	return payments.stream().map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void setStatus(DunningDocumentStatus status) {
        this.status = status;
    }

    public DunningDocumentStatus getStatus() {
        return status;
    }
}
