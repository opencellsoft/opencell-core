package org.meveo.api.dto;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 * @since Oct 11, 2013
 **/
@XmlRootElement(name = "CountryTax")
@XmlAccessorType(XmlAccessType.FIELD)
public class CountryTaxDto {

	@XmlElement(required = true)
	private String countryCode;

	@XmlElement(required = true)
	private BigDecimal taxValue;
	
	public CountryTaxDto() {
		
	}
	
	public CountryTaxDto(String countryCode, BigDecimal taxValue) {
		this.countryCode = countryCode;
		this.taxValue = taxValue;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public BigDecimal getTaxValue() {
		return taxValue;
	}

	public void setTaxValue(BigDecimal taxValue) {
		this.taxValue = taxValue;
	}
}
