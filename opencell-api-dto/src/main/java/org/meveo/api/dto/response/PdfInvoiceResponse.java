package org.meveo.api.dto.response;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class PdfInvoiceResponse.
 *
 * @author R.AITYAAZZA
 */
@XmlRootElement(name = "PdfInvoiceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class PdfInvoiceResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3909724929599303450L;

    /** The pdf invoice. */
    private byte[] pdfInvoice;

    /**
     * Instantiates a new pdf invoice response.
     */
    public PdfInvoiceResponse() {
        super();
    }

    /**
     * Gets the pdf invoice.
     *
     * @return the pdf invoice
     */
    public byte[] getPdfInvoice() {
        return pdfInvoice;
    }

    /**
     * Sets the pdf invoice.
     *
     * @param pdfInvoice the new pdf invoice
     */
    public void setPdfInvoice(byte[] pdfInvoice) {
        this.pdfInvoice = pdfInvoice;
    }

    @Override
    public String toString() {
        return "PdfInvoiceResponse [pdfInvoice=" + Arrays.toString(pdfInvoice) + ", toString()=" + super.toString() + "]";
    }
}