package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.InvoiceTypesDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetInvoiceTypesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetInvoiceTypesResponse extends BaseResponse {

	private static final long serialVersionUID = 1336652304727158329L;

	private InvoiceTypesDto invoiceTypesDto;

	public GetInvoiceTypesResponse() {		
	}

	/**
	 * @return the invoiceTypesDto
	 */
	public InvoiceTypesDto getInvoiceTypesDto() {
		return invoiceTypesDto;
	}

	/**
	 * @param invoiceTypesDto the invoiceTypesDto to set
	 */
	public void setInvoiceTypesDto(InvoiceTypesDto invoiceTypesDto) {
		this.invoiceTypesDto = invoiceTypesDto;
	}

	
	@Override
	public String toString() {
		return "GetInvoiceTypesResponse [invoiceTypesDto=" + invoiceTypesDto + "]";
	}

	

}
