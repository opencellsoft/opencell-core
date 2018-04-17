package org.meveo.api.dto.invoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

/**
 * The Class GetXmlInvoiceRequestDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetXmlInvoiceRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetXmlInvoiceRequestDto extends BaseDto {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The invoice number. */
    private String invoiceNumber;
    
    /** The invoice type. */
    private String invoiceType;

    /**
     * Instantiates a new gets the xml invoice request dto.
     */
    public GetXmlInvoiceRequestDto() {

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

    @Override
    public String toString() {
        return "GetXmlInvoiceRequestDto [invoiceNumber=" + invoiceNumber + ", invoiceType=" + invoiceType + "]";
    }
}