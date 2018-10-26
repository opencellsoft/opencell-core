package org.meveo.api.dto.billing;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;

/**
 * The Class InvoiceSequencesDto.
 */
@XmlRootElement(name = "InvoiceSequences")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceSequencesDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The invoice sequences. */
    private List<InvoiceSequenceDto> invoiceSequences = new ArrayList<InvoiceSequenceDto>();

    /**
     * Instantiates a new invoice sequences dto.
     */
    public InvoiceSequencesDto() {

    }

    /**
     * Gets the invoice sequences.
     *
     * @return the invoiceSequences
     */
    public List<InvoiceSequenceDto> getInvoiceSequences() {
        return invoiceSequences;
    }

    /**
     * Sets the invoice sequences.
     *
     * @param invoiceSequences the invoiceSequences to set
     */
    public void setInvoiceSequences(List<InvoiceSequenceDto> invoiceSequences) {
        this.invoiceSequences = invoiceSequences;
    }

    @Override
    public String toString() {
        return "InvoiceSequencesDto [InvoiceSequences=" + invoiceSequences + "]";
    }

}