package org.meveo.model.billing;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.meveo.model.ExportIdentifier;
import org.meveo.model.IEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.catalog.OfferServiceTemplate;

@Entity
@ExportIdentifier({ "invoiceType.code", "seller.code", "seller.provider" })
@Table(name = "BILLING_SEQ_INVTYP_SELL")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILL_SEQ_IT_SELL_SEQ")
public class InvoiceTypeSellerSequence implements IEntity {

    @Id
    @GeneratedValue(generator = "ID_GENERATOR", strategy = GenerationType.AUTO)
    @Column(name = "ID")
    @Access(AccessType.PROPERTY)
    protected Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "INVOICETYPE_ID")
    @NotNull
    private InvoiceType invoiceType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SELLER_ID")
    @NotNull
    private Seller seller;

    @Embedded
    private Sequence sequence = new Sequence();

    public InvoiceTypeSellerSequence() {

    }

    public InvoiceTypeSellerSequence(InvoiceType invoiceType, Seller seller, Sequence sequence) {
        super();
        this.invoiceType = invoiceType;
        this.seller = seller;
        this.sequence = sequence;
    }

    @Override
    public Serializable getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean isTransient() {
        return id == null;
    }

    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public Sequence getSequence() {
        return sequence;
    }

    public void setSequence(Sequence sequence) {
        this.sequence = sequence;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof OfferServiceTemplate)) {
            return false;
        }

        InvoiceTypeSellerSequence other = (InvoiceTypeSellerSequence) obj;

        if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
            // return true;
        }

        if (invoiceType != null) {
            if (!invoiceType.equals(other.getInvoiceType())) {
                return false;
            }
        } else if (other.getInvoiceType() != null) {
            return false;
        }

        if (seller != null) {
            if (!seller.equals(other.getSeller())) {
                return false;
            }
        } else if (other.getSeller() != null) {
            return false;
        }
        return true;
    }
}