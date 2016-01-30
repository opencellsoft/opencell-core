package org.meveo.api.dto.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GenerateInvoiceResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class GenerateInvoiceResultDto {
	
	private String invoiceNumber;
	
	public GenerateInvoiceResultDto(){
		
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
		return "GenerateInvoiceResultDto [invoiceNumber=" + invoiceNumber + "]";
	}

}
