package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.InvoiceTypeDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetInvoiceTypeResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetInvoiceTypeResponse extends BaseResponse {

	private static final long serialVersionUID = 1336652304727158329L;

	private InvoiceTypeDto invoiceTypeDto;

	public GetInvoiceTypeResponse() {		
	}

	/**
	 * @return the invoiceTypeDto
	 */
	public InvoiceTypeDto getInvoiceTypeDto() {
		return invoiceTypeDto;
	}

	/**
	 * @param invoiceTypeDto the invoiceTypeDto to set
	 */
	public void setInvoiceTypeDto(InvoiceTypeDto invoiceTypeDto) {
		this.invoiceTypeDto = invoiceTypeDto;
	}

	
	@Override
	public String toString() {
		return "GetInvoiceTypeResponse [invoiceTypeDto=" + invoiceTypeDto + "]";
	}

	

}
