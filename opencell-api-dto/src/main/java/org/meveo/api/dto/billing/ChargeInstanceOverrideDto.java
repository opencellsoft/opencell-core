package org.meveo.api.dto.billing;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlAccessorType(XmlAccessType.FIELD)
public class ChargeInstanceOverrideDto implements Serializable {

	private static final long serialVersionUID = 5653231200327069483L;

	@XmlAttribute(required = true)
	private String chargeInstanceCode;

	@XmlElement(required = false)
	private BigDecimal amountWithoutTax;

	@XmlElement(required = false)
	private BigDecimal amountWithTax;
	
	private String description;

	public String getChargeInstanceCode() {
		return chargeInstanceCode;
	}

	public void setChargeInstanceCode(String chargeInstanceCode) {
		this.chargeInstanceCode = chargeInstanceCode;
	}

	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
	}

	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}


	public BigDecimal getAmountWithTax() {
		return amountWithTax;
	}

	public void setAmountWithTax(BigDecimal amountWithTax) {
		this.amountWithTax = amountWithTax;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "ChargeInstanceOverrideDto [chargeInstanceCode=" + chargeInstanceCode + ", amountWithoutTax=" + amountWithoutTax + ", amountWithTax=" + amountWithTax + ", description"+description+"]";
	}
}
