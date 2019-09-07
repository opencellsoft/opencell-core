package org.meveo.model.billing;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 * Rated transaction processing status
 * 
 * @author Andrius Karpavicius
 */
@Entity
@Table(name = "billing_rated_transaction_status")
public class RatedTransactionProcessingStatus implements Serializable {

    private static final long serialVersionUID = 187585752545621875L;

    /**
     * Rated transaction identifier
     */
    @Id
    @Column(name = "id")
    @Access(AccessType.PROPERTY) // Access is set to property so a call to getId() wont trigger hibernate proxy loading
    protected Long id;

    /**
     * Rated transaction
     */
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aggregate_id_f")
    private SubCategoryInvoiceAgregate invoiceAgregateF;

    /**
     * Status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull
    private RatedTransactionStatusEnum status;

    /**
     * Status change date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "status_date")
    private Date statusDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Rated transaction processing status constructor
     */
    public RatedTransactionProcessingStatus() {
    }

    /**
     * Rated transaction processing status constructor
     * 
     * @param id Rated transaction identifier
     * @param billingRun Billing run to associate
     * @param invoice Invoice
     * @param invoiceAgregateF Invoice sub aggregate
     * @param status Processing status
     */
    public RatedTransactionProcessingStatus(Long id, BillingRun billingRun, Invoice invoice, SubCategoryInvoiceAgregate invoiceAgregateF, RatedTransactionStatusEnum status) {
        super();
        this.id = id;
        this.billingRun = billingRun;
        this.invoice = invoice;
        this.invoiceAgregateF = invoiceAgregateF;
        this.status = status;
        this.statusDate = new Date();
    }

    /**
     * Rated transaction processing status constructor
     * 
     * @param ratedTransaction Rated Transaction
     * @param status Processing status
     */
    public RatedTransactionProcessingStatus(RatedTransaction ratedTransaction, RatedTransactionStatusEnum status) {
        if (ratedTransaction.getId() != null) {
            this.id = ratedTransaction.getId();
        } else {
            this.ratedTransaction = ratedTransaction;
        }
        this.status = status;
        this.statusDate = new Date();
    }

    /**
     * @return Billing run that invoiced this Rated transaction
     */
    public BillingRun getBillingRun() {
        return billingRun;
    }

    /**
     * @param billingRun Billing run that invoiced this Rated transaction
     */
    public void setBillingRun(BillingRun billingRun) {
        this.billingRun = billingRun;
    }

    /**
     * @return Invoice that invoiced this Rated transaction
     */
    public Invoice getInvoice() {
        return invoice;
    }

    /**
     * @param invoice Invoice that invoiced this Rated transaction
     */
    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    /**
     * @return Subcategory invoice aggregate that Rated transaction was invoiced under
     */
    public SubCategoryInvoiceAgregate getInvoiceAgregateF() {
        return invoiceAgregateF;
    }

    /**
     * @param invoiceAgregateF Subcategory invoice aggregate that Rated transaction was invoiced under
     */
    public void setInvoiceAgregateF(SubCategoryInvoiceAgregate invoiceAgregateF) {
        this.invoiceAgregateF = invoiceAgregateF;
    }

    /**
     * @return Processing status
     */
    public RatedTransactionStatusEnum getStatus() {
        return status;
    }

    /**
     * @param status Processing status
     */
    public void setStatus(RatedTransactionStatusEnum status) {
        this.status = status;
    }

    /**
     * @return Rated transaction
     */
    public RatedTransaction getRatedTransaction() {
        return ratedTransaction;
    }

    /**
     * @param ratedTransaction Rated transaction
     */
    public void setRatedTransaction(RatedTransaction ratedTransaction) {
        this.ratedTransaction = ratedTransaction;
    }

    /**
     * @return Status change date
     */
    public Date getStatusDate() {
        return statusDate;
    }

    /**
     * @param statusDate Status change date
     */
    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }
}