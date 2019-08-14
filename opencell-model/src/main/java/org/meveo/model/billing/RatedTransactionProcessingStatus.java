package org.meveo.model.billing;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "billing_rated_transaction_status")
public class RatedTransactionProcessingStatus implements Serializable {

    private static final long serialVersionUID = 187585752545621875L;

    public RatedTransactionProcessingStatus() {
    }

    public RatedTransactionProcessingStatus(Long id, BillingRun billingRun, Invoice invoice, SubCategoryInvoiceAgregate invoiceAgregateF, RatedTransactionStatusEnum status) {
        super();
        this.id = id;
        this.billingRun = billingRun;
        this.invoice = invoice;
        this.invoiceAgregateF = invoiceAgregateF;
        this.status = status;
    }

    @Id
    @Column(name = "id")
    @Access(AccessType.PROPERTY) // Access is set to property so a call to getId() wont trigger hibernate proxy loading
    protected Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private RatedTransaction ratedTransaction;

    /**
     * Billing run that invoiced this Rated transaction
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_run_id")
    private BillingRun billingRun;

    /**
     * Invoice that invoiced this Rated transaction
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    /**
     * Subcategory invoice aggregate that Rated transaction was invoiced under
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "aggregate_id_f")
    private SubCategoryInvoiceAgregate invoiceAgregateF;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RatedTransactionStatusEnum status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BillingRun getBillingRun() {
        return billingRun;
    }

    public void setBillingRun(BillingRun billingRun) {
        this.billingRun = billingRun;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public SubCategoryInvoiceAgregate getInvoiceAgregateF() {
        return invoiceAgregateF;
    }

    public void setInvoiceAgregateF(SubCategoryInvoiceAgregate invoiceAgregateF) {
        this.invoiceAgregateF = invoiceAgregateF;
    }

    public RatedTransactionStatusEnum getStatus() {
        return status;
    }

    public void setStatus(RatedTransactionStatusEnum status) {
        this.status = status;
    }
    
    public RatedTransaction getRatedTransaction() {
        return ratedTransaction;
    }
    
    public void setRatedTransaction(RatedTransaction ratedTransaction) {
        this.ratedTransaction = ratedTransaction;
    }
}