package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author R.AITYAAZZA
 *
 */
@XmlRootElement(name = "PdfInvoiceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceCreationResponse extends BaseResponse {

	private static final long serialVersionUID = 1L;
	
	private String invoiceNumber;

	public InvoiceCreationResponse() {
		super();
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	@Override
	public String toString() {
		return "InvoiceCreationResponse [invoiceNumber=" + invoiceNumber + ", toString()=" + super.toString() + "]";
	}


}
