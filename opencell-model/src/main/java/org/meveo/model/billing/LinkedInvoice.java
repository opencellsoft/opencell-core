package org.meveo.model.billing;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "billing_linked_invoices", uniqueConstraints = @UniqueConstraint(columnNames = { "id", "linked_invoice_id" }))
@SuppressWarnings("serial")
public class LinkedInvoice implements Serializable {

    

    public static final int NB_PRECISION = 23;
    public static final int NB_DECIMALS = 12;
    
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", nullable = false, referencedColumnName = "id")
    private Invoice id;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linked_invoice_id", nullable = false, referencedColumnName = "id")
    private Invoice linkedInvoice;
    
    
    @Column(name = "amount", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal amount;
    
    @Column(name = "type", length = 50)
    @Enumerated(EnumType.STRING)
    private InvoiceTypeEnum type;


    public LinkedInvoice(Invoice id, Invoice linkedInvoice) {
        super();
        this.id = id;
        this.linkedInvoice = linkedInvoice;
    }

    
    public LinkedInvoice() {
        
    }
    
    public Invoice getId() {
        return  this.id;
    }

    public void setId(Invoice id) {
        this.id = id;
    }

    public Invoice getLinkedInvoice() {
        return this.linkedInvoice;
    }

    public void setLinkedInvoice(Invoice linkedInvoice) {
        this.linkedInvoice = linkedInvoice;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public InvoiceTypeEnum getType() {
        return type;
    }

    public void setType(InvoiceTypeEnum type) {
        this.type = type;
    }
    
}
