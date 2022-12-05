package org.meveo.model.billing;

import java.io.Serializable;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Stores temporary data for mass Rated transaction update during Invoicing process
 * 
 * @author Andrius Karpavicius
 *
 */
@Entity
@Table(name = "billing_rated_transaction_pending")
public class RatedTransactionPendingUpdate implements Serializable {

    private static final long serialVersionUID = 5298055304065898789L;

    /**
     * Record/entity identifier
     */
    @Id
    @Column(name = "id")
    @Access(AccessType.PROPERTY) // Access is set to property so a call to getId() wont trigger hibernate proxy loading
    protected Long id;

    /**
     * Subcategory invoice aggregate that Rated transaction was invoiced under
     */
    @Column(name = "aggregate_id_f")
    private Long invoiceAgregateF;

    /**
     * Invoice that invoiced this Rated transaction
     */
    @Column(name = "invoice_id")
    private Long invoice;

    /**
     * Billing run that invoiced this Rated transaction
     */
    @Column(name = "billing_run_id")
    private Long billingRun;

    /**
     * Constructor
     */
    public RatedTransactionPendingUpdate() {

    }

    /**
     * Constructor
     * 
     * @param id Rated transaction ID
     * @param invoiceAgregateF Invoice Aggregate
     * @param invoice Invoice
     * @param billingRun Billing Run
     */
    public RatedTransactionPendingUpdate(Long id, Long invoiceAgregateF, Long invoice, Long billingRun) {
        super();
        this.id = id;
        this.invoiceAgregateF = invoiceAgregateF;
        this.invoice = invoice;
        this.billingRun = billingRun;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return Invoice that invoiced this Rated transaction
     */
    public Long getInvoice() {
        return invoice;
    }

    /**
     * @param invoice Invoice that invoiced this Rated transaction
     */
    public void setInvoice(Long invoice) {
        this.invoice = invoice;
    }

    /**
     * @return Subcategory invoice aggregate that Rated transaction was invoiced under
     */
    public Long getInvoiceAgregateF() {
        return invoiceAgregateF;
    }

    /**
     * @param invoiceAgregateF Subcategory invoice aggregate that Rated transaction was invoiced under
     */
    public void setInvoiceAgregateF(Long invoiceAgregateF) {
        this.invoiceAgregateF = invoiceAgregateF;
    }

    /**
     * @return Billing run that invoiced this Rated transaction
     */
    public Long getBillingRun() {
        return billingRun;
    }

    /**
     * @param billingRun Billing run that invoiced this Rated transaction
     */
    public void setBillingRun(Long billingRun) {
        this.billingRun = billingRun;
    }
}