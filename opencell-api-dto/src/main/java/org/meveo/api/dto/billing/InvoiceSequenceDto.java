package org.meveo.api.dto.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.billing.InvoiceSequence;

/**
 * The Class InvoiceSequenceDto.
 * 
 * @author abdelmounaim akadid
 */
@XmlRootElement(name = "InvoiceType")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceSequenceDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The sequence size. */
    private Integer sequenceSize;

    /** The current invoice nb. */
    private Long currentInvoiceNb;


    /**
     * Instantiates a new invoice type dto.
     */
    public InvoiceSequenceDto() {

    }

    /**
     * Instantiates a new invoice type dto.
     *
     * @param invoiceType the invoice type
     */
    public InvoiceSequenceDto(InvoiceSequence invoiceSequence) {
        super(invoiceSequence);
        this.sequenceSize = invoiceSequence.getSequenceSize();
        this.currentInvoiceNb = invoiceSequence.getCurrentInvoiceNb();
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

	@Override
    public String toString() {
        return "InvoiceSequenceDto [code=" + getCode() + ", description=" + getDescription() + ", sequenceSize=" + getSequenceSize() + ", sequenceSize=" + getSequenceSize() + "]";
    }
}