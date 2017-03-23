package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.TaxesDto;

@XmlRootElement(name = "GetTaxesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetTaxesResponse extends BaseResponse {

	private static final long serialVersionUID = 1L;

	private TaxesDto taxesDto;

	public TaxesDto getTaxesDto() {
		return taxesDto;
	}

	public void setTaxesDto(TaxesDto taxesDto) {
		this.taxesDto = taxesDto;
	}

	@Override
	public String toString() {
		return "GetTaxesResponse [taxesDto=" + taxesDto + "]";
	}
}
