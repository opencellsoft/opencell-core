package org.meveo.model.billing;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.IEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.catalog.OfferServiceTemplate;

@Entity
@ExportIdentifier({ "invoiceType.code", "seller.code" })
@Table(name = "billing_seq_invtyp_sell")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "bill_seq_it_sell_seq"), })
public class InvoiceTypeSellerSequence implements IEntity {

    @Id
    @GeneratedValue(generator = "ID_GENERATOR", strategy = GenerationType.AUTO)
    @Column(name = "id")
    @Access(AccessType.PROPERTY)
    protected Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoicetype_id")
    @NotNull
    private InvoiceType invoiceType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id")
    @NotNull
    private Seller seller;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_sequence_id")
    private InvoiceSequence invoiceSequence;
    
    @Column(name = "prefix_el", length = 2000)
    @Size(max = 2000)
    private String prefixEL = "";

    public InvoiceTypeSellerSequence() {

    }

    public InvoiceTypeSellerSequence(InvoiceType invoiceType, Seller seller, InvoiceSequence invoiceSequence, String prefixEL) {
        super();
        this.invoiceType = invoiceType;
        this.seller = seller;
        this.invoiceSequence = invoiceSequence;
        this.prefixEL = prefixEL;
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

    public InvoiceSequence getInvoiceSequence() {
		return invoiceSequence;
	}

	public void setInvoiceSequence(InvoiceSequence invoiceSequence) {
		this.invoiceSequence = invoiceSequence;
	}
	
	public String getPrefixEL() {
		return prefixEL;
	}

	public void setPrefixEL(String prefixEL) {
		this.prefixEL = prefixEL;
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
             return true;
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