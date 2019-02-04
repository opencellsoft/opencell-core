package org.meveo.model.dunning;

import org.hibernate.annotations.GenericGenerator;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IWFEntity;
import org.meveo.model.WorkflowedEntity;
import org.meveo.model.billing.Subscription;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.RecordedInvoice;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

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
     * Customer account
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_account_id")
    private CustomerAccount customerAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_subscription_id")
    private Subscription subscription;

    /**
     * Unpaid invoices associated to this dunning document
     */
    @OneToMany(mappedBy = "dunningDocument", fetch = FetchType.EAGER, orphanRemoval = true)
    private List<RecordedInvoice> dueInvoices;

    /**
     * payments done withing the dunning process
     * associated with this dunning doc
     */
    @OneToMany(mappedBy = "dunningDocument", fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Payment> payments;

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
    public BigDecimal getAmountWithoutTax() {
    	BigDecimal amountWithoutTax = BigDecimal.ZERO;
    	if (dueInvoices != null && !dueInvoices.isEmpty()) {
    		for (RecordedInvoice recordedInvoice : dueInvoices) {
    			amountWithoutTax = amountWithoutTax.add(recordedInvoice.getAmountWithoutTax());
			}
    	}
    	return amountWithoutTax;
    }
    
    @Transient
    public BigDecimal getAmountWithTax() {
    	BigDecimal amountWithTax = BigDecimal.ZERO;
    	if (dueInvoices != null && !dueInvoices.isEmpty()) {
    		for (RecordedInvoice recordedInvoice : dueInvoices) {
    			amountWithTax = amountWithTax.add(recordedInvoice.getAmount());
			}
    	}
    	return amountWithTax;
    }
    
    @Transient
    public BigDecimal getPaidAmount() {
    	BigDecimal paidAmount = BigDecimal.ZERO;
    	if (payments != null && !payments.isEmpty()) {
    		for (Payment payment : payments) {
    			paidAmount = paidAmount.add(payment.getAmount());
			}
    	}
    	return paidAmount;
    }
}
