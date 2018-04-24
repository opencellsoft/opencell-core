package org.meveo.api.dto.invoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

/**
 * The Class GetPdfInvoiceRequestDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetPdfInvoiceRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetPdfInvoiceRequestDto extends BaseDto {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The invoice number. */
    private String invoiceNumber;
    
    /** The invoice type. */
    private String invoiceType;
    
    /** The generate pdf. */
    private Boolean generatePdf = Boolean.FALSE;

    /**
     * Instantiates a new gets the pdf invoice request dto.
     */
    public GetPdfInvoiceRequestDto() {

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

    /**
     * Gets the invoice type.
     *
     * @return the invoice type
     */
    public String getInvoiceType() {
        return invoiceType;
    }

    /**
     * Sets the invoice type.
     *
     * @param invoiceType the new invoice type
     */
    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    /**
     * Gets the generate pdf.
     *
     * @return the generatePdf
     */
    public Boolean getGeneratePdf() {
        return generatePdf;
    }

    /**
     * Sets the generate pdf.
     *
     * @param generatePdf the generatePdf to set
     */
    public void setGeneratePdf(Boolean generatePdf) {
        this.generatePdf = generatePdf;
    }


    @Override
    public String toString() {
        return "GetPdfInvoiceRequestDto [invoiceNumber=" + invoiceNumber + ", invoiceType=" + invoiceType + ", generatePdf=" + generatePdf + "]";
    }
}