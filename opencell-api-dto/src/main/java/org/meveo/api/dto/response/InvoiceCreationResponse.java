package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class InvoiceCreationResponse.
 *
 * @author R.AITYAAZZA
 */
@XmlRootElement(name = "PdfInvoiceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceCreationResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The invoice number. */
    private String invoiceNumber;

    /**
     * Instantiates a new invoice creation response.
     */
    public InvoiceCreationResponse() {
        super();
    }

    /**
     * Gets the invoice number.
     *
     * @return the invoice number
     */
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * Sets the invoice number.
     *
     * @param invoiceNumber the new invoice number
     */
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    @Override
    public String toString() {
        return "InvoiceCreationResponse [invoiceNumber=" + invoiceNumber + ", toString()=" + super.toString() + "]";
    }
}