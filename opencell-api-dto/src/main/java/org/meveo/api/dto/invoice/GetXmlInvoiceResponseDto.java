package org.meveo.api.dto.invoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetXmlInvoiceResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetXmlInvoiceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetXmlInvoiceResponseDto extends BaseResponse {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The xml content. */
    private String xmlContent;

    /**
     * Instantiates a new gets the xml invoice response dto.
     */
    public GetXmlInvoiceResponseDto() {

    }

    /**
     * Gets the xml content.
     *
     * @return the xmlContent
     */
    public String getXmlContent() {
        return xmlContent;
    }

    /**
     * Sets the xml content.
     *
     * @param xmlContent the xmlContent to set
     */
    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
    }

    @Override
    public String toString() {
        return "GetXmlInvoiceResponseDto [xmlContent=" + xmlContent + "]";
    }

}