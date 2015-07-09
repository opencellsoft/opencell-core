package org.meveo.api.dto.invoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;


@XmlRootElement(name = "GenerateInvoiceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GenerateInvoiceResponseDto extends BaseResponse{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String invoiceNumber;
	
	public GenerateInvoiceResponseDto(){
		
	}

	/**
	 * @return the invoiceNumber
	 */
	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	/**
	 * @param invoiceNumber the invoiceNumber to set
	 */
	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GenerateInvoiceResponseDto [invoiceNumber=" + invoiceNumber + "]";
	}

	
	
}
