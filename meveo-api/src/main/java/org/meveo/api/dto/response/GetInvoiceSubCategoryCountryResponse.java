package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.InvoiceSubCategoryCountryDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetInvoiceSubCategoryCountryResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetInvoiceSubCategoryCountryResponse extends BaseResponse {

	private static final long serialVersionUID = 5831917945471936382L;

	private InvoiceSubCategoryCountryDto invoiceSubCategoryCountryDto;

	public InvoiceSubCategoryCountryDto getInvoiceSubCategoryCountryDto() {
		return invoiceSubCategoryCountryDto;
	}

	public void setInvoiceSubCategoryCountryDto(InvoiceSubCategoryCountryDto invoiceSubCategoryCountryDto) {
		this.invoiceSubCategoryCountryDto = invoiceSubCategoryCountryDto;
	}

	@Override
	public String toString() {
		return "GetInvoiceSubCategoryCountryResponse [invoiceSubCategoryCountryDto=" + invoiceSubCategoryCountryDto
				+ ", toString()=" + super.toString() + "]";
	}
}
