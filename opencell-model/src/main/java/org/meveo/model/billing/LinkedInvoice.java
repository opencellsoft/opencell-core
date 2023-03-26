package org.meveo.model.billing;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.IEntity;

import static java.math.BigDecimal.ZERO;

@Entity
@Table(name = "billing_linked_invoices", uniqueConstraints = @UniqueConstraint(columnNames = { "id", "linked_invoice_id" }))
@NamedQueries({ 
    @NamedQuery(name = "LinkedInvoice.deleteByIdInvoiceAndLinkedInvoice", query = "delete from LinkedInvoice l where l.id.id = :invoiceId and l.linkedInvoiceValue.id in (:linkedInvoiceId)"),
    @NamedQuery(name = "LinkedInvoice.deleteAllAdjLink", query = "delete from LinkedInvoice l  where l.linkedInvoiceValue.id in (select inv.id from Invoice inv where inv.invoiceType.code = 'ADJ')"),
    @NamedQuery(name = "LinkedInvoice.deleteByInvoiceIdAndType", query = "delete from LinkedInvoice l where l.id.id = :invoiceId and l.type = (:type)"),
    @NamedQuery(name = "LinkedInvoice.find", query = "select l from LinkedInvoice l where l.id.id = :invoiceId and l.linkedInvoiceValue.id = :linkedInvoiceId"),
    @NamedQuery(name = "LinkedInvoice.removeLinkedAdvances", query = "DELETE FROM LinkedInvoice li where li.id.id in (:invoiceIds) and li.type='ADVANCEMENT_PAYMENT'")
    
})
@SuppressWarnings("serial")
public class LinkedInvoice implements IEntity, Serializable {

    

    public static final int NB_PRECISION = 23;
    public static final int NB_DECIMALS = 12;
    
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", nullable = false, referencedColumnName = "id")
    private Invoice id;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linked_invoice_id", nullable = false, referencedColumnName = "id")
    private Invoice linkedInvoiceValue;
    
    
    @Column(name = "amount", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal amount;
    
    @Column(name = "type", length = 50)
    @Enumerated(EnumType.STRING)
    private InvoiceTypeEnum type;

    @Column(name = "transactional_amount", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal transactionalAmount;

    @PrePersist
    @PreUpdate
    public void prePersistOrUpdate() {
        if (this.transactionalAmount != null) {
            BigDecimal appliedRate = getInvoice().getLastAppliedRate();
            this.amount = this.transactionalAmount.divide(appliedRate, 2, RoundingMode.HALF_UP);
        }
    }

    public LinkedInvoice(Invoice id, Invoice linkedInvoiceValue) {
        super();
        this.id = id;
        this.linkedInvoiceValue = linkedInvoiceValue;
    }


    public LinkedInvoice(Invoice id, Invoice linkedInvoiceValue, BigDecimal transactionalAmount, InvoiceTypeEnum type) {
        super();
        this.id = id;
        this.linkedInvoiceValue = linkedInvoiceValue;
        this.transactionalAmount = transactionalAmount;
        this.type = type;
    }

    
    public LinkedInvoice() {
        
    }
    public Long getId() {
        return this.id!=null? this.id.getId() : null;
  }
    public Invoice getInvoice() {
        return  this.id;
    }

    public void setInvoice(Invoice invoice) {
          this.id = invoice;
    }

    public Invoice getLinkedInvoiceValue() {
        return this.linkedInvoiceValue;
    }

    public void setLinkedInvoiceValue(Invoice linkedInvoice) {
        this.linkedInvoiceValue = linkedInvoice;
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

    public BigDecimal getTransactionalAmount() {
        return transactionalAmount;
    }

    public void setTransactionalAmount(BigDecimal transactionalAmount) {
        this.transactionalAmount = transactionalAmount;
    }


    @Override
    public void setId(Long id) {
        if(this.id!=null) {
            this.id.setId(id);
        }
    }


    @Override
    public boolean isTransient() {
        return false;
    }


    @Override
    public int hashCode() {
        return Objects.hash(id.getId(), linkedInvoiceValue.getId());
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LinkedInvoice other = (LinkedInvoice) obj;
        return Objects.equals(id.getId(), other.id.getId()) && Objects.equals(linkedInvoiceValue.getId(), other.linkedInvoiceValue.getId());
    }
    
    
}
