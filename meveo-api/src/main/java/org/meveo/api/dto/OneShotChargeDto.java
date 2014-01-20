package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "oneShotCharge")
@XmlAccessorType(XmlAccessType.FIELD)
public class OneShotChargeDto {
	private String chargeCode;
	private String desccription;
	private Double unitPriceWithoutTax;
	private Double taxPercent;
	private String taxCode;
	private String taxDescription;

	public String getChargeCode() {
		return chargeCode;
	}

	public void setChargeCode(String chargeCode) {
		this.chargeCode = chargeCode;
	}

	public String getDesccription() {
		return desccription;
	}

	public void setDesccription(String desccription) {
		this.desccription = desccription;
	}

	public Double getUnitPriceWithoutTax() {
		return unitPriceWithoutTax;
	}

	public void setUnitPriceWithoutTax(Double unitPriceWithoutTax) {
		this.unitPriceWithoutTax = unitPriceWithoutTax;
	}

	public Double getTaxPercent() {
		return taxPercent;
	}

	public void setTaxPercent(Double taxPercent) {
		this.taxPercent = taxPercent;
	}

	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	public String getTaxDescription() {
		return taxDescription;
	}

	public void setTaxDescription(String taxDescription) {
		this.taxDescription = taxDescription;
	}
}
