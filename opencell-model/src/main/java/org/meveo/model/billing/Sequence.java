package org.meveo.model.billing;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

@Embeddable
public class Sequence {

    @Column(name = "prefix_el", length = 2000)
    @Size(max = 2000)
    private String prefixEL = "";

    @Column(name = "sequence_size")
    private Integer sequenceSize = 9;

    @Column(name = "current_invoice_nb")
    private Long currentInvoiceNb = 0L;

    /**
     * A previously invoiceNb held by this sequence, usually less by one, unless numbers were reserved by more than one
     */
    @Transient
    private Long previousInvoiceNb = 0L;

    public Sequence() {
    }

    public Sequence(String prefixEL, Integer sequenceSize, Long currentInvoiceNb) {
        super();
        this.prefixEL = prefixEL;
        this.sequenceSize = sequenceSize;
        this.currentInvoiceNb = currentInvoiceNb;
    }

    /**
     * @return the prefixEL
     */
    public String getPrefixEL() {

        return prefixEL == null ? "" : prefixEL;
    }

    /**
     * @param prefixEL the prefixEL to set
     */
    public void setPrefixEL(String prefixEL) {
        this.prefixEL = prefixEL;
    }

    /**
     * @return the sequenceSize
     */
    public Integer getSequenceSize() {
        return sequenceSize;
    }

    /**
     * @param sequenceSize the sequenceSize to set
     */
    public void setSequenceSize(Integer sequenceSize) {
        this.sequenceSize = sequenceSize;
    }

    /**
     * @return the currentInvoiceNb
     */
    public Long getCurrentInvoiceNb() {
        return currentInvoiceNb;
    }

    /**
     * @param currentInvoiceNb the currentInvoiceNb to set
     */
    public void setCurrentInvoiceNb(Long currentInvoiceNb) {
        this.currentInvoiceNb = currentInvoiceNb;
    }

    public Long getPreviousInvoiceNb() {
        return previousInvoiceNb;
    }

    public void setPreviousInvoiceNb(Long previousInvoiceNb) {
        this.previousInvoiceNb = previousInvoiceNb;
    }

    @Override
    public String toString() {
        return "Sequence [prefixEL=" + prefixEL + ", sequenceSize=" + sequenceSize + ", currentInvoiceNb=" + currentInvoiceNb + "]";
    }
}