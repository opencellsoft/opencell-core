package org.meveo.api.dto.invoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;


@XmlRootElement(name = "GetXmlInvoiceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetXmlInvoiceResponseDto extends BaseResponse{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String xmlContent;
	
	public GetXmlInvoiceResponseDto(){
		
	}

	/**
	 * @return the xmlContent
	 */
	public String getXmlContent() {
		return xmlContent;
	}

	/**
	 * @param xmlContent the xmlContent to set
	 */
	public void setXmlContent(String xmlContent) {
		this.xmlContent = xmlContent;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GetXmlInvoiceResponseDto [xmlContent=" + xmlContent + "]";
	}

	
	
	
}
