package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.InvoiceTypeDto;

/**
 * The Class GetInvoiceTypeResponse.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetInvoiceTypeResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetInvoiceTypeResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1336652304727158329L;

    /** The invoice type dto. */
    private InvoiceTypeDto invoiceTypeDto;

    /**
     * Instantiates a new gets the invoice type response.
     */
    public GetInvoiceTypeResponse() {
    }

    /**
     * Gets the invoice type dto.
     *
     * @return the invoiceTypeDto
     */
    public InvoiceTypeDto getInvoiceTypeDto() {
        return invoiceTypeDto;
    }

    /**
     * Sets the invoice type dto.
     *
     * @param invoiceTypeDto the invoiceTypeDto to set
     */
    public void setInvoiceTypeDto(InvoiceTypeDto invoiceTypeDto) {
        this.invoiceTypeDto = invoiceTypeDto;
    }

    @Override
    public String toString() {
        return "GetInvoiceTypeResponse [invoiceTypeDto=" + invoiceTypeDto + "]";
    }
}