package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.InvoiceSequenceDto;

/**
 * The Class GetInvoiceSequenceResponse.
 *
 * @author akadid abdelmounaim
 */
@XmlRootElement(name = "GetInvoiceSequenceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetInvoiceSequenceResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1336652304727158329L;

    /** The invoice type dto. */
    private InvoiceSequenceDto invoiceSequenceDto;

    /**
     * Instantiates a new gets the invoice type response.
     */
    public GetInvoiceSequenceResponse() {
    }

    /**
     * Gets the invoice sequence dto.
     *
     * @return the invoiceSequenceDto
     */
    public InvoiceSequenceDto getInvoiceSequenceDto() {
        return invoiceSequenceDto;
    }

    /**
     * Sets the invoice sequence dto.
     *
     * @param invoiceSequenceDto the invoiceSequenceDto to set
     */
    public void setInvoiceSequenceDto(InvoiceSequenceDto invoiceSequenceDto) {
        this.invoiceSequenceDto = invoiceSequenceDto;
    }

    @Override
    public String toString() {
        return "GetInvoiceSequenceResponse [invoiceSequenceDto=" + invoiceSequenceDto + "]";
    }
}