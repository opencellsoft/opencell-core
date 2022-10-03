package org.meveo.model.billing;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;

@SuppressWarnings("serial")
@Entity
@Table(name = "billing_invoice_advance_mapping")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "billing_invoice_advance_mapping_seq"), })
public class AdvanceMapping extends BaseEntity {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advance_invoice_id")
    private Invoice advanceInvoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    public AdvanceMapping(Invoice advanceInvoice, Invoice invoice, BigDecimal amount) {
        super();
        this.advanceInvoice = advanceInvoice;
        this.invoice = invoice;
        this.amount = amount;
    }

    public AdvanceMapping() {
    }

    @Column(name = "amount", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal amount;

    public Invoice getAdvanceInvoice() {
        return advanceInvoice;
    }

    public void setAdvanceInvoice(Invoice advanceInvoice) {
        this.advanceInvoice = advanceInvoice;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    
}
