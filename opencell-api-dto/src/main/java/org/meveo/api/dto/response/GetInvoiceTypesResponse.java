package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.InvoiceTypesDto;

/**
 * The Class GetInvoiceTypesResponse.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetInvoiceTypesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetInvoiceTypesResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1336652304727158329L;

    /** The invoice types dto. */
    private InvoiceTypesDto invoiceTypesDto;

    /**
     * Instantiates a new gets the invoice types response.
     */
    public GetInvoiceTypesResponse() {
    }

    /**
     * Gets the invoice types dto.
     *
     * @return the invoiceTypesDto
     */
    public InvoiceTypesDto getInvoiceTypesDto() {
        return invoiceTypesDto;
    }

    /**
     * Sets the invoice types dto.
     *
     * @param invoiceTypesDto the invoiceTypesDto to set
     */
    public void setInvoiceTypesDto(InvoiceTypesDto invoiceTypesDto) {
        this.invoiceTypesDto = invoiceTypesDto;
    }

    @Override
    public String toString() {
        return "GetInvoiceTypesResponse [invoiceTypesDto=" + invoiceTypesDto + "]";
    }
}