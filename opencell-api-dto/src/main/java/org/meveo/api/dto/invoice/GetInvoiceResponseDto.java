package org.meveo.api.dto.invoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetInvoiceResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetInvoiceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetInvoiceResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4434354113240370786L;

    /** The invoice. */
    private InvoiceDto invoice;

    /**
     * Gets the invoice.
     *
     * @return the invoice
     */
    public InvoiceDto getInvoice() {
        return invoice;
    }

    /**
     * Sets the invoice.
     *
     * @param invoice the new invoice
     */
    public void setInvoice(InvoiceDto invoice) {
        this.invoice = invoice;
    }

    @Override
    public String toString() {
        return "GetInvoiceResponseDto [invoice=" + invoice + ", toString()=" + super.toString() + "]";
    }
}