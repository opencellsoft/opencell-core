package org.meveo.model.billing;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "invoiceType.code", "seller.code" })
@Table(name = "billing_seq_invoice")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "bill_seq_invoice_seq"), })
@NamedQueries({ @NamedQuery(name = "InvoiceSequence.currentInvoiceNb", query = "select max(currentInvoiceNb) from InvoiceSequence i where i.code=:invoiceSequenceCode") })
public class InvoiceSequence extends BusinessEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "ID_GENERATOR", strategy = GenerationType.AUTO)
    @Column(name = "id")
    @Access(AccessType.PROPERTY)
    protected Long id;

    @OneToMany(mappedBy = "invoiceSequence", fetch = FetchType.LAZY)
    private List<InvoiceType> invoiceTypes;

    @OneToMany(mappedBy = "invoiceSequence", fetch = FetchType.LAZY)
    private List<InvoiceTypeSellerSequence> invoiceTypeSellerSequences;

    @Column(name = "sequence_size")
    private Integer sequenceSize = 9;

    @Column(name = "current_invoice_nb")
    private Long currentInvoiceNb = 0L;

    /**
     * A previously invoiceNb held by this sequence, usually less by one, unless numbers were reserved by more than one
     */
    @Transient
    private Long previousInvoiceNb = 0L;

    public InvoiceSequence() {
    }

    public InvoiceSequence(Integer sequenceSize, Long currentInvoiceNb) {
        super();
        this.sequenceSize = sequenceSize;
        this.currentInvoiceNb = currentInvoiceNb;
    }

    public List<InvoiceType> getInvoiceTypes() {
        if (invoiceTypes == null) {
            invoiceTypes = new ArrayList<InvoiceType>();
        }
        return invoiceTypes;
    }

    public void setInvoiceTypes(List<InvoiceType> invoiceTypes) {
        this.invoiceTypes = invoiceTypes;
    }

    public List<InvoiceTypeSellerSequence> getInvoiceTypeSellerSequences() {
        return invoiceTypeSellerSequences;
    }

    public void setInvoiceTypeSellerSequences(List<InvoiceTypeSellerSequence> invoiceTypeSellerSequences) {
        this.invoiceTypeSellerSequences = invoiceTypeSellerSequences;
    }

    public Integer getSequenceSize() {
        return sequenceSize;
    }

    public void setSequenceSize(Integer sequenceSize) {
        this.sequenceSize = sequenceSize;
    }

    public Long getCurrentInvoiceNb() {
        return currentInvoiceNb;
    }

    public void setCurrentInvoiceNb(Long currentInvoiceNb) {
        this.currentInvoiceNb = currentInvoiceNb;
    }

    public Long getPreviousInvoiceNb() {
        return previousInvoiceNb;
    }

    public void setPreviousInvoiceNb(Long previousInvoiceNb) {
        this.previousInvoiceNb = previousInvoiceNb;
    }

}
