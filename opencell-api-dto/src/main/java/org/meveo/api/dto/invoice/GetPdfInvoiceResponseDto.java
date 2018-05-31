package org.meveo.api.dto.invoice;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetPdfInvoiceResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetPdfInvoiceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetPdfInvoiceResponseDto extends BaseResponse {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The pdf content. */
    private byte[] pdfContent;

    /**
     * Instantiates a new gets the pdf invoice response dto.
     */
    public GetPdfInvoiceResponseDto() {

    }

    /**
     * Gets the pdf content.
     *
     * @return the pdfContent
     */
    public byte[] getPdfContent() {
        return pdfContent;
    }

    /**
     * Sets the pdf content.
     *
     * @param pdfContent the pdfContent to set
     */
    public void setPdfContent(byte[] pdfContent) {
        this.pdfContent = pdfContent;
    }

    @Override
    public String toString() {
        return "GetPdfInvoiceResponseDto [pdfContent=" + Arrays.toString(pdfContent) + "]";
    }

}