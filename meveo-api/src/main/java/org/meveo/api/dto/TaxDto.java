package org.meveo.api.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 * @since Oct 11, 2013
 **/
@XmlRootElement(name = "tax")
@XmlAccessorType(XmlAccessType.FIELD)
public class TaxDto extends BaseDto {

	private static final long serialVersionUID = 5184602572648722134L;

	@XmlElement(required = true)
	private String taxId;

	private String name;

	@XmlElement(required = true)
	private List<CountryTaxDto> countryTaxes;

	public TaxDto() {

	}

	public String getTaxId() {
		return taxId;
	}

	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<CountryTaxDto> getCountryTaxes() {
		return countryTaxes;
	}

	public void setCountryTaxes(List<CountryTaxDto> countryTaxes) {
		this.countryTaxes = countryTaxes;
	}
}
