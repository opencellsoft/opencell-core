package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.InvoiceSequencesDto;

/**
 * The Class GetInvoiceSequencesResponse.
 *
 * @author akadid abdelmounaim
 */
@XmlRootElement(name = "GetInvoiceSequencesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetInvoiceSequencesResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1336652304727158329L;

    /** The invoice types dto. */
    private InvoiceSequencesDto invoiceSequencesDto;

    /**
     * Instantiates a new gets the invoice sequences response.
     */
    public GetInvoiceSequencesResponse() {
    }

    /**
     * Gets the invoice sequences dto.
     *
     * @return the invoiceSequencesDto
     */
    public InvoiceSequencesDto getInvoiceSequencesDto() {
        return invoiceSequencesDto;
    }

    /**
     * Sets the invoice sequences dto.
     *
     * @param invoiceSequencesDto the invoiceSequencesDto to set
     */
    public void setInvoiceSequencesDto(InvoiceSequencesDto invoiceSequencesDto) {
        this.invoiceSequencesDto = invoiceSequencesDto;
    }

    @Override
    public String toString() {
        return "GetInvoiceSequencesResponse [invoiceSequencesDto=" + invoiceSequencesDto + "]";
    }
}